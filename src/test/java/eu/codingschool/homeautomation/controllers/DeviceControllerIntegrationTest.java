package eu.codingschool.homeautomation.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
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
import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.repositories.DeviceRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.MOCK, 
		classes = HomeAutomationApplication.class
)
//We can @Autowire MockMvc if Spring security was absent and we didn't configure that via setUp().
//@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@SqlGroup({
	@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/test-data-population.sql"),
	@Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/test-data-cleanup.sql")
})
public class DeviceControllerIntegrationTest {
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	private DeviceRepository deviceRepository;

    private MockMvc mockMvc;
    
    private static final String ENDPOINT_DEVICE_LIST = "/admin/device/list";
    private static final String ENDPOINT_DEVICE_NEW = "/admin/device/new";
    private static final String ENDPOINT_DEVICE_EDIT = "/admin/device/{id}/edit";
    private static final String ENDPOINT_DEVICE_DELETE = "/admin/device/{id}/delete";
    private static final String ENDPOINT_DEVICE_LIST_ALL_ALTERNATIVE = "/admin/device/user/all";
    private static final String ENDPOINT_DEVICE_PER_USER = "/device/user/{id}";
    private static final String ENDPOINT_DEVICE_PER_ROOM_ALL = "/admin/device/user/all/room/{roomId}";
    private static final String ENDPOINT_DEVICE_PER_ROOM_PER_USER = "/device/user/{userId}/room/{roomId}";
    private static final String ENDPOINT_DEVICE_STATUS_EDIT = "/device/{id}/updateStatus/{status}";
    private static final String ENDPOINT_DEVICE_VALUE_EDIT = "/device/{id}/updateValue/{value}";
    
    private static final String LAYOUT_DEVICE_LIST = "device/list";
    private static final String LAYOUT_DEVICE_LIST_ALTERNATIVE = "userDevices/grid";
    private static final String LAYOUT_ERROR_403 = "/error/403";
    private static final String LAYOUT_ERROR_422 = "/error/422";
    
    private static final String MODAL_DEVICE_NEW_OR_EDIT = "device/modals :: modalNewOrEdit";
    private static final String REDIRECT = "redirect:";
    
    private static final String USER_DETAILS_SERVICE = "userDetailsService";
    
    // User inserted in database via test/resources/test-data-population.sql
    private static final String USER_ADMIN = "testadmin@foo.com";
    private static final String USER_SIMPLE = "testuser1@foo.com";
    
	
	@Before
    public void setup() {
		// explicitly configure the filter chain
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
								 .apply(springSecurity())
								 .build();
    }
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void getAllDevices_shouldReturnAllAvailableDevices_whenAdminUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_DEVICE_LIST))
			   .andDo(print())
			   .andExpect(view().name(LAYOUT_DEVICE_LIST));
	}
	
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void getAllDevices_shouldBeForbidden_whenSimpleUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_DEVICE_LIST))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewNewDeviceForm_shouldDisplayForm_whenAdminUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_DEVICE_NEW))
			   .andDo(print())
			   .andExpect(view().name(MODAL_DEVICE_NEW_OR_EDIT));
	}
	
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewNewDeviceForm_shouldBeForbidden_whenSimpleUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_DEVICE_NEW))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void createNewDevice_shouldDisplayDevicesList_whenSubmittingFormSucceeds() throws Exception {
		long devicesCountBefore = deviceRepository.count();
		
		mockMvc.perform(post(ENDPOINT_DEVICE_NEW)
							.param("name", "Test lighting")
							.param("deviceType", "2") // Lights
							.param("room", "2") // Kitchen
			   )
			   .andDo(print())
			   .andExpect(view().name(REDIRECT + ENDPOINT_DEVICE_LIST));
		
		long devicesCountAfter = deviceRepository.count();
		assertEquals(devicesCountBefore + 1, devicesCountAfter);
	}
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void createNewDevice_shouldDisplayForm_whenSubmittingFormFails() throws Exception {
		mockMvc.perform(post(ENDPOINT_DEVICE_NEW)
							.param("deviceType", "2") // Lights
							.param("room", "2") // Kitchen
			   )
			   .andDo(print())
			   .andExpect(view().name(MODAL_DEVICE_NEW_OR_EDIT));
	}
	
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void createNewDevice_shouldBeForbidden_whenSimpleUser() throws Exception {
		mockMvc.perform(post(ENDPOINT_DEVICE_NEW))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewEditDeviceForm_shouldDisplayForm_whenAdminUser() throws Exception {
		Integer deviceId = 7;
		mockMvc.perform(get(ENDPOINT_DEVICE_EDIT, deviceId))
			   .andDo(print())
			   .andExpect(view().name(MODAL_DEVICE_NEW_OR_EDIT)).andExpect(model().size(5))
			   .andExpect(model().attribute("allDeviceTypes", hasSize(3)))
			   .andExpect(model().attribute("allRooms", hasSize(3)))
			   .andExpect(model().attribute("device", isA(Device.class)))
			   .andExpect(model().attribute("device", hasProperty("id", equalTo(deviceId))))
			   .andExpect(model().attribute("device", hasProperty("name", equalTo("Lighting #3"))))
			   .andExpect(model().attribute("actionUrl", String.format("/admin/device/%s/edit", deviceId)))
			   .andExpect(model().attribute("modalTitle", "Edit"));
	}
	
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewEditDeviceForm_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer deviceId = 7;
		mockMvc.perform(get(ENDPOINT_DEVICE_EDIT, deviceId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void editDevice_shouldDisplayDevicesList_whenSubmittingFormSucceeds() throws Exception {
		Integer deviceId = 7;
		Device deviceBeforeEdit = deviceRepository.findById(deviceId).get();
		String nameBeforeEdit = deviceBeforeEdit.getName();
		Integer deviceTypeBeforeEdit = deviceBeforeEdit.getDeviceType().getId();
		Integer roomBeforeEdit = deviceBeforeEdit.getRoom().getId();
		
		mockMvc.perform(post(ENDPOINT_DEVICE_EDIT, deviceId)
							.param("name", "Test lighting")
							.param("deviceType", "2") // Lights
							.param("room", "2") // Kitchen
			   )
			   .andDo(print())
			   .andExpect(view().name(REDIRECT + ENDPOINT_DEVICE_LIST));
		
		// Check that the device was edited
		Device deviceAfterEdit = deviceRepository.findById(deviceId).get();
		String nameAfterEdit = deviceAfterEdit.getName();
		Integer deviceTypeAfterEdit = deviceAfterEdit.getDeviceType().getId();
		Integer roomAfterEdit = deviceAfterEdit.getRoom().getId();
		
		assertNotEquals(nameBeforeEdit, nameAfterEdit);
		assertEquals(deviceTypeBeforeEdit, deviceTypeAfterEdit);
		assertNotEquals(roomBeforeEdit, roomAfterEdit);
	}
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void editDevice_shouldDisplayForm_whenSubmittingFormFails() throws Exception {
		Integer deviceId = 7;
		mockMvc.perform(post(ENDPOINT_DEVICE_EDIT, deviceId)
							.param("deviceType", "2") // Lights
							.param("room", "2") // Kitchen
			   )
			   .andDo(print())
			   .andExpect(view().name(MODAL_DEVICE_NEW_OR_EDIT));
	}
	
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void editDevice_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer deviceId = 7;
		mockMvc.perform(post(ENDPOINT_DEVICE_EDIT, deviceId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void selectDeleteDevice_shouldDisplayConfirmationDialog_whenAdminUser() throws Exception {
		Integer deviceId = 7;
		mockMvc.perform(get(ENDPOINT_DEVICE_DELETE, deviceId))
			   .andDo(print())
			   .andExpect(view().name("device/modals :: modalDelete")).andExpect(model().size(2))
			   .andExpect(model().attribute("device", hasProperty("id", equalTo(deviceId))))
			   .andExpect(model().attribute("device", hasProperty("name", equalTo("Lighting #3"))))
			   .andExpect(model().attribute("actionUrl", String.format("/admin/device/%s/delete", deviceId)));
	}
	
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void selectDeleteDevice_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer deviceId = 7;
		mockMvc.perform(get(ENDPOINT_DEVICE_DELETE, deviceId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void doDeleteDevice_shouldDisplayDevicesList_whenDeviceUnassigned() throws Exception {
		Device savedDevice = deviceRepository.save(new Device());
		long devicesCountBefore = deviceRepository.count();
		Integer deviceId = savedDevice.getId();
		mockMvc.perform(post(ENDPOINT_DEVICE_DELETE, deviceId))
			   .andDo(print())
			   .andExpect(view().name(REDIRECT + ENDPOINT_DEVICE_LIST));
		
		// Check that the selected device was deleted
		long devicesCountAfter = deviceRepository.count();
		assertEquals(devicesCountBefore - 1, devicesCountAfter);
		assertFalse(deviceRepository.findById(deviceId).isPresent());
	}
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void doDeleteDevice_shouldFail_whenDeviceAssignedToUser() throws Exception {
		Integer deviceId = 7;
		mockMvc.perform(post(ENDPOINT_DEVICE_DELETE, deviceId))
			   .andDo(print())
			   .andExpect(view().name(LAYOUT_ERROR_422))
			   .andExpect(model().attribute("action", "delete device"))
			   .andExpect(model().attribute("entityName", "Lighting #3"))
			   .andExpect(model().attribute("additionalMessage", notNullValue()));
	}
	
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void doDeleteDevice_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer deviceId = 7;
		mockMvc.perform(post(ENDPOINT_DEVICE_DELETE, deviceId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void getAllDevicesAlternativeViewStyle_shouldReturnAllAvailableDevices_whenAdminUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_DEVICE_LIST_ALL_ALTERNATIVE))
			   .andDo(print())
			   .andExpect(view().name(LAYOUT_DEVICE_LIST_ALTERNATIVE));
	}
	
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void getAllDevicesAlternativeViewStyle_shouldBeForbidden_whenSimpleUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_DEVICE_LIST_ALL_ALTERNATIVE))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void getUserDevicesAlternativeViewStyle_shouldBeForbidden_whenAdminUserAsksDataForOtherUser() 
			throws Exception {
		
		Integer userId = 103;
		mockMvc.perform(get(ENDPOINT_DEVICE_PER_USER, userId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}
	
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void getUserDevicesAlternativeViewStyle_shouldBeForbidden_whenSimpleUserAsksDevicesForOtherUser() 
			throws Exception {
		
		Integer userId = 103;
		mockMvc.perform(get(ENDPOINT_DEVICE_PER_USER, userId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}
	
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	@SuppressWarnings("unchecked")
	public void getUserDevicesAlternativeViewStyle_shouldReturnUserDevices_whenUserAsksTheirDevices() throws Exception {
		Integer userId = 102;
		mockMvc.perform(get(ENDPOINT_DEVICE_PER_USER, userId))
			   .andDo(print())
			   .andExpect(view().name(LAYOUT_DEVICE_LIST_ALTERNATIVE))
			   .andExpect(model().attribute("devices", hasSize(3)))
			   .andExpect(model().attribute("devices", contains(
					   hasProperty("name", equalTo("Lighting #1")), 
					   hasProperty("name", equalTo("Lighting #2")),
					   hasProperty("name", equalTo("Lighting #3"))
				)));
	}
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void showAllDevicesPerRoom_shouldReturnAllAvailableDevices_whenAdminUser() throws Exception {
		Integer roomId = 3;
		mockMvc.perform(get(ENDPOINT_DEVICE_PER_ROOM_ALL, roomId))
			   .andDo(print())
			   .andExpect(view().name(LAYOUT_DEVICE_LIST_ALTERNATIVE))
			   .andExpect(model().attribute("devices", hasSize(2)))
			   .andExpect(model().attribute("rooms", hasSize(3)))
			   .andExpect(model().attribute("selectedRoom", "Bedroom"));
	}
	
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void showAllDevicesPerRoom_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer roomId = 3;
		mockMvc.perform(get(ENDPOINT_DEVICE_PER_ROOM_ALL, roomId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void showUserDevicesPerRoom_shouldBeForbidden_whenAdminUserAsksDataForOtherUser() throws Exception {
		Integer userId = 103;
		Integer roomId = 1;
		mockMvc.perform(get(ENDPOINT_DEVICE_PER_ROOM_PER_USER, userId, roomId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}
	
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void showUserDevicesPerRoom_shouldBeForbidden_whenSimpleUserAsksDevicesForOtherUser() throws Exception {
		Integer userId = 103;
		Integer roomId = 1;
		mockMvc.perform(get(ENDPOINT_DEVICE_PER_ROOM_PER_USER, userId, roomId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}
	
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void showUserDevicesPerRoom_shouldReturnUserDevices_whenUserAsksTheirDevices() throws Exception {
		Integer userId = 102;
		Integer roomId = 1;
		mockMvc.perform(get(ENDPOINT_DEVICE_PER_ROOM_PER_USER, userId, roomId))
			   .andDo(print())
			   .andExpect(view().name(LAYOUT_DEVICE_LIST_ALTERNATIVE))
			   .andExpect(model().attribute("devices", hasSize(1)))
			   .andExpect(model().attribute("devices", contains(hasProperty("name", equalTo("Lighting #1")))))
			   .andExpect(model().attribute("selectedRoom", "Living Room"));
	}
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void updateDeviceStatus_shouldUpdateStatusAndRedirectToAllDevices_whenAdminUser() throws Exception {
		Integer deviceId = 6;
		boolean deviceStatusBefore = deviceRepository.findById(deviceId).get().isStatusOn();
		
		mockMvc.perform(post(ENDPOINT_DEVICE_STATUS_EDIT, 
								String.valueOf(deviceId), 
								String.valueOf(!deviceStatusBefore)
							)
			   )
			   .andDo(print())
			   .andExpect(view().name(REDIRECT + ENDPOINT_DEVICE_LIST_ALL_ALTERNATIVE));
		
		boolean deviceStatusAfter = deviceRepository.findById(deviceId).get().isStatusOn();
		assertEquals(!deviceStatusBefore, deviceStatusAfter);
	}
	
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void updateDeviceStatus_shouldUpdateStatusAndRedirectToUserDevices_whenSimpleUserUpdatesTheirDevice() 
			throws Exception {
		
		Integer userId = 102;
		Integer deviceId = 7;
		boolean deviceStatusBefore = deviceRepository.findById(deviceId).get().isStatusOn();
		
		mockMvc.perform(post(ENDPOINT_DEVICE_STATUS_EDIT, 
								String.valueOf(deviceId), 
								String.valueOf(!deviceStatusBefore)
							)
			   )
			   .andDo(print())
			   .andExpect(view().name(REDIRECT + ENDPOINT_DEVICE_PER_USER.replace("{id}", String.valueOf(userId))));
		
		boolean deviceStatusAfter = deviceRepository.findById(deviceId).get().isStatusOn();
		assertEquals(!deviceStatusBefore, deviceStatusAfter);
	}
	
	// A simple user can update a device which is assigned to another user and not them!
	// It denotes a problem in the security of the implementation!!!
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void updateDeviceStatus_shouldUpdateStatusAndRedirectToUserDevices_whenSimpleUserUpdatesDeviceForOtherUser() 
			throws Exception {
		
		Integer userId = 102; // testuser1@foo.com
		Integer deviceId = 6; // deviceId should be controlled only by ADMIN and testuser2@foo.com!
		boolean deviceStatusBefore = deviceRepository.findById(deviceId).get().isStatusOn();
		
		mockMvc.perform(post(ENDPOINT_DEVICE_STATUS_EDIT, 
								String.valueOf(deviceId), 
								String.valueOf(!deviceStatusBefore)
							)
			   )
			   .andDo(print())
			   .andExpect(view().name(REDIRECT + ENDPOINT_DEVICE_PER_USER.replace("{id}", String.valueOf(userId))));
		
		boolean deviceStatusAfter = deviceRepository.findById(deviceId).get().isStatusOn();
		assertEquals(!deviceStatusBefore, deviceStatusAfter);
	}
	
	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void updateDeviceInformationValue_shouldUpdateInformationValueAndRedirectToAllDevices_whenAdminUser() 
			throws Exception {
		
		Integer deviceId = 6;
		int deviceInformationValueBefore = Integer.parseInt(
				deviceRepository.findById(deviceId).get().getInformationValue()
		);
		
		mockMvc.perform(post(ENDPOINT_DEVICE_VALUE_EDIT, 
								String.valueOf(deviceId), 
								String.valueOf(deviceInformationValueBefore + 1)
							)
			   )
			   .andDo(print())
			   .andExpect(view().name(REDIRECT + ENDPOINT_DEVICE_LIST_ALL_ALTERNATIVE));
		
		int deviceInformationValueAfter = Integer.parseInt(
				deviceRepository.findById(deviceId).get().getInformationValue()
		);
		assertEquals(deviceInformationValueBefore + 1L, deviceInformationValueAfter);
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void updateDeviceInformationValue_shouldUpdateInformationValue_whenSimpleUserUpdatesTheirDevice() 
			throws Exception {
		
		Integer userId = 102;
		Integer deviceId = 7;
		int deviceInformationValueBefore = Integer.parseInt(
				deviceRepository.findById(deviceId).get().getInformationValue()
		);
		
		mockMvc.perform(post(ENDPOINT_DEVICE_VALUE_EDIT, 
								String.valueOf(deviceId), 
								String.valueOf(deviceInformationValueBefore + 1)
							)
			   )
			   .andDo(print())
			   .andExpect(view().name(REDIRECT + ENDPOINT_DEVICE_PER_USER.replace("{id}", String.valueOf(userId))));
		
		int deviceInformationValueAfter = Integer.parseInt(
				deviceRepository.findById(deviceId).get().getInformationValue()
		);
		assertEquals(deviceInformationValueBefore + 1L, deviceInformationValueAfter);
	}
	
	// A simple user can update a device which is assigned to another user and not them!
	// It denotes a problem in the security of the implementation!!!
	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void updateDeviceInformationValue_shouldUpdateInformationValue_whenSimpleUserUpdatesDeviceForOtherUser() 
			 throws Exception {
		
		Integer userId = 102; // testuser1@foo.com
		Integer deviceId = 6; // deviceId should be controlled only by ADMIN and testuser2@foo.com!
		int deviceInformationValueBefore = Integer.parseInt(
				deviceRepository.findById(deviceId).get().getInformationValue()
		);
		
		mockMvc.perform(post(ENDPOINT_DEVICE_VALUE_EDIT, 
								String.valueOf(deviceId), 
								String.valueOf(deviceInformationValueBefore + 1)
							)
			   )
			   .andDo(print())
			   .andExpect(view().name(REDIRECT + ENDPOINT_DEVICE_PER_USER.replace("{id}", String.valueOf(userId))));
		
		int deviceInformationValueAfter = Integer.parseInt(
				deviceRepository.findById(deviceId).get().getInformationValue()
		);
		assertEquals(deviceInformationValueBefore + 1L, deviceInformationValueAfter);
	}
	
}
