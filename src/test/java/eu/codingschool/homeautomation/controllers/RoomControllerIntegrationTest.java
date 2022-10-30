package eu.codingschool.homeautomation.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import eu.codingschool.homeautomation.HomeAutomationApplication;
import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.repositories.RoomRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = HomeAutomationApplication.class)
// We can @Autowire MockMvc if Spring security was absent and we didn't configure that via setUp().
// @AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@SqlGroup({ 
	@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/test-data-population.sql"),
	@Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/test-data-cleanup.sql") 
})
public class RoomControllerIntegrationTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private RoomRepository roomRepository;

	private MockMvc mockMvc;

	private static final String ENDPOINT_ROOM_LIST = "/admin/room/list";
	private static final String ENDPOINT_ROOM_NEW = "/admin/room/new";
	private static final String ENDPOINT_ROOM_EDIT = "/admin/room/{id}/edit";
	private static final String ENDPOINT_ROOM_DELETE = "/admin/room/{id}/delete";
	
	private static final String LAYOUT_ROOM_LIST = "room/list";
	private static final String LAYOUT_ERROR_403 = "/error/403";
	private static final String LAYOUT_ERROR_422 = "/error/422";
	
	private static final String MODAL_ROOM_NEW_OR_EDIT = "room/modals :: modalNewOrEdit";
	private static final String REDIRECT = "redirect:";
	
	private static final String USER_DETAILS_SERVICE = "userDetailsService";

	// User inserted in database via test/resources/test-data-population.sql
	private static final String USER_ADMIN = "testadmin@foo.com";
	private static final String USER_SIMPLE = "testuser1@foo.com";

	@Before
	public void setup() {
		// explicitly configure the filter chain
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void getRooms_shouldReturnAllRooms_whenAdminUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_ROOM_LIST))
			   .andDo(print())
			   .andExpect(view().name(LAYOUT_ROOM_LIST));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void getRooms_shouldBeForbidden_whenSimpleUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_ROOM_LIST))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewNewRoomForm_shouldDisplayForm_whenAdminUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_ROOM_NEW))
			   .andDo(print())
			   .andExpect(view().name(MODAL_ROOM_NEW_OR_EDIT));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewNewRoomForm_shouldBeForbidden_whenSimpleUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_ROOM_NEW))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void createNewRoom_shouldDisplayRoomsList_whenSubmittingFormSucceeds() throws Exception {
		long roomsCountBefore = roomRepository.count();

		mockMvc.perform(post(ENDPOINT_ROOM_NEW)
							.param("name", "Another room")
				)
				.andDo(print())
				.andExpect(view().name(REDIRECT + ENDPOINT_ROOM_LIST));

		long roomsCountAfter = roomRepository.count();
		assertEquals(roomsCountBefore + 1, roomsCountAfter);
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void createNewRoom_shouldDisplayForm_whenSubmittingFormFails() throws Exception {
		mockMvc.perform(post(ENDPOINT_ROOM_NEW)
							.param("name", "")
			   )
			   .andDo(print())
			   .andExpect(view().name(MODAL_ROOM_NEW_OR_EDIT));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void createNewRoom_shouldBeForbidden_whenSimpleUser() throws Exception {
		mockMvc.perform(post(ENDPOINT_ROOM_NEW))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewEditRoomForm_shouldDisplayForm_whenAdminUser() throws Exception {
		Integer roomId = 3;
		mockMvc.perform(get(ENDPOINT_ROOM_EDIT, roomId))
			   .andDo(print())
			   .andExpect(view().name(MODAL_ROOM_NEW_OR_EDIT))
			   .andExpect(model().size(3))
			   .andExpect(model().attribute("room", isA(Room.class)))
			   .andExpect(model().attribute("room", hasProperty("id", equalTo(roomId))))
			   .andExpect(model().attribute("room", hasProperty("name", equalTo("Bedroom"))))
			   .andExpect(model().attribute("actionUrl", String.format("/admin/room/%s/edit", roomId)))
			   .andExpect(model().attribute("modalTitle", "Edit"));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewEditRoomForm_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer roomId = 3;
		mockMvc.perform(get(ENDPOINT_ROOM_EDIT, roomId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void editRoom_shouldDisplayDevicesList_whenSubmittingFormSucceeds() throws Exception {
		Integer roomId = 3;
		Room roomBeforeEdit = roomRepository.findById(roomId).get();
		String nameBeforeEdit = roomBeforeEdit.getName();

		mockMvc.perform(post(ENDPOINT_ROOM_EDIT, roomId)
							.param("name", "Another room")
				)
				.andDo(print())
				.andExpect(view().name(REDIRECT + ENDPOINT_ROOM_LIST));

		// Check that the room was edited
		Room roomAfterEdit = roomRepository.findById(roomId).get();
		String nameAfterEdit = roomAfterEdit.getName();

		assertNotEquals(nameBeforeEdit, nameAfterEdit);
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void editRoom_shouldDisplayForm_whenSubmittingFormFails() throws Exception {
		Integer roomId = 3;
		mockMvc.perform(post(ENDPOINT_ROOM_EDIT, roomId)
							.param("name", "")
				)
				.andDo(print())
				.andExpect(view().name(MODAL_ROOM_NEW_OR_EDIT));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void editRoom_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer roomId = 3;
		mockMvc.perform(post(ENDPOINT_ROOM_EDIT, roomId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void selectDeleteRoom_shouldDisplayConfirmationDialog_whenAdminUser() throws Exception {
		Integer roomId = 3;
		mockMvc.perform(get(ENDPOINT_ROOM_DELETE, roomId))
			   .andDo(print())
			   .andExpect(view().name("room/modals :: modalDelete"))
			   .andExpect(model().size(2))
			   .andExpect(model().attribute("room", hasProperty("id", equalTo(roomId))))
			   .andExpect(model().attribute("room", hasProperty("name", equalTo("Bedroom"))))
			   .andExpect(model().attribute("actionUrl", String.format("/admin/room/%s/delete", roomId)));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void selectDeleteRoom_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer roomId = 3;
		mockMvc.perform(get(ENDPOINT_ROOM_DELETE, roomId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void doDeleteRoom_shouldDisplayRoomsList_whenRoomFree() throws Exception {
		Room savedRoom = roomRepository.save(new Room());
		long devicesCountBefore = roomRepository.count();
		Integer roomId = savedRoom.getId();
		mockMvc.perform(post(ENDPOINT_ROOM_DELETE, roomId))
			   .andDo(print())
			   .andExpect(view().name(REDIRECT + ENDPOINT_ROOM_LIST));

		// Check that the selected room was deleted
		long devicesCountAfter = roomRepository.count();
		assertEquals(devicesCountBefore - 1, devicesCountAfter);
		assertFalse(roomRepository.findById(roomId).isPresent());
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void doDeleteRoom_shouldFail_whenRoomUsedByDevice() throws Exception {
		Integer deviceId = 3;
		mockMvc.perform(post(ENDPOINT_ROOM_DELETE, deviceId))
			   .andDo(print())
			   .andExpect(view().name(LAYOUT_ERROR_422))
			   .andExpect(model().attribute("action", "delete room"))
			   .andExpect(model().attribute("entityName", "Bedroom"))
			   .andExpect(model().attribute("additionalMessage", notNullValue()));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void doDeleteRoom_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer roomId = 3;
		mockMvc.perform(post(ENDPOINT_ROOM_DELETE, roomId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}

}
