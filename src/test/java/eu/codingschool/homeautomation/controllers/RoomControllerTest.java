package eu.codingschool.homeautomation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;

import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.services.RoomService;
import eu.codingschool.homeautomation.validators.RoomValidator;

@RunWith(SpringRunner.class)
@WebMvcTest(RoomController.class)
public class RoomControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RoomValidator roomValidator;
	
	@MockBean
	@Qualifier("personServiceImpl")
	private PersonService personService;
	
	@MockBean
	@Qualifier("roomServiceImpl")
	private RoomService roomService;
	
	private Person admin;
	private Set<Room> allRooms;
	
	@Before
    public void setUp() throws Exception {
		admin = new Person();
		admin.setRole("ADMIN");
		
		Room room1 = new Room();
		room1.setId(1);
		Room room2 = new Room();
		room2.setId(2);
		
		allRooms = new HashSet<>(Arrays.asList(room1, room2));
		
		when(roomService.findAll()).thenReturn(allRooms);
		when(roomService.findById(1)).thenReturn(room1);
		when(roomService.findById(2)).thenReturn(room2);
    }
	
	@Test
	@WithMockUser
	public void getRooms_Should_LoadAllRooms_When_AdminLoggedInAndRequestingRooms() throws Exception {
		// given
		when(personService.findByEmail(any())).thenReturn(admin);
		
		// when - then
		this.mockMvc.perform(get("/admin/room/list"))
					.andExpect(status().isOk()) 
					.andExpect(model().attribute("loggedInUser", admin))
					.andExpect(model().attribute("rooms", allRooms))
					.andExpect(view().name("room/list"));
	}
	
	@Test
	@WithMockUser
	public void newRoom_Should_OpenModal_When_Requested() throws Exception {
		// when - then
		performHttpGetAction("new", -1, "modalNewOrEdit");
	}
	
	@Test
	@WithMockUser
	public void addRoom_Should_SaveRoom_When_Provided() throws Exception {
		// when - then
		performHttpPostAction("new", -1, "/admin/room/list");
		
		verify(roomService, times(1)).save(any());
	}
	
	@Test
	@WithMockUser
	public void addRoom_Should_NotSaveRoom_When_NameNotProvided() throws Exception {
		// given 
		// mock the void method to return validation errors
		doAnswer(validator -> {
			((BeanPropertyBindingResult)validator.getArguments()[1]).addError(new ObjectError("name", "NotEmpty"));
			return null;
		}).when(roomValidator).validate(any(), any());
		
		// when - then
		this.mockMvc.perform(post("/admin/room/new")
								.with(csrf())
					)
					.andExpect(status().isOk())
					.andExpect(view().name("room/modals :: modalNewOrEdit"));
		
		verifyZeroInteractions(roomService);
	}
	
	@Test
	@WithMockUser
	public void viewRoom_Should_OpenModal_When_Requested() throws Exception {
		// given
		Integer roomId = 2;
		
		// when - then
		performHttpGetAction("edit", roomId, "modalNewOrEdit");
		
		verify(roomService, times(1)).findById(roomId);
		verify(roomService, times(0)).findById(roomId + 1);
		verify(roomService, times(0)).findById(roomId - 1);
	}
	
	@Test
	@WithMockUser
	public void editRoom_Should_SaveRoom_When_Provided() throws Exception {
		// given
		Integer roomId = 2;
				
		// when - then
		performHttpPostAction("edit", roomId, "/admin/room/list");
		
		verify(roomService, times(1)).save(any());
	}
	
	@Test
	@WithMockUser
	public void editRoom_Should_NotSaveRoom_When_NameNotProvided() throws Exception {
		// given 
		Integer roomId = 2;
		
		// mock the void method to return validation errors
		doAnswer(validator -> {
			((BeanPropertyBindingResult)validator.getArguments()[1]).addError(new ObjectError("name", "NotEmpty"));
			return null;
		}).when(roomValidator).validate(any(), any());
		
		// when - then
		performHttpPostActionWithValidationErrors("edit", roomId, "modalNewOrEdit");
		
		verifyZeroInteractions(roomService);
	}
	
	@Test
	@WithMockUser
	public void confrimDeleteRoom_Should_OpenModal_When_Requested() throws Exception {
		// given
		Integer roomId = 2;
		
		// when - then
		performHttpGetAction("delete", roomId, "modalDelete");
		
		verify(roomService, times(1)).findById(roomId);
		verify(roomService, times(0)).findById(roomId + 1);
		verify(roomService, times(0)).findById(roomId - 1);
	}
	
	@Test
	@WithMockUser
	public void doDeleteRoom_Should_CallDelete_When_Exists() throws Exception {
		// given
		Integer roomId = 2;
				
		// when - then
		performHttpPostAction("delete", roomId, "/admin/room/list");
		
		verify(roomService, times(1)).delete(any());
	}
	
	@Test
	@WithMockUser
	public void doDeleteRoom_Should_CallDelete_When_NotExists() throws Exception {
		// given
		Integer roomId = 3; // this room does not exist
				
		// when - then
		performHttpPostAction("delete", roomId, "/admin/room/list");
		
		verify(roomService, times(1)).delete(any());
	}
	
	private void performHttpGetAction(String actionType, Integer roomId, String modalName) throws Exception {
		this.mockMvc.perform(get("/admin/room/" + (roomId != -1 ? roomId : "") + "/" + actionType))
					.andExpect(status().isOk())
					.andExpect(view().name("room/modals :: " + modalName));
	}
	
	private void performHttpPostAction(String actionType, Integer roomId, String expectedUrl) throws Exception {
		this.mockMvc.perform(post("/admin/room/" + (roomId != -1 ? roomId : "") + "/" + actionType)
								.with(csrf())
					)
					.andExpect(redirectedUrl(expectedUrl))
					.andExpect(view().name("redirect:" + expectedUrl));
	}
	
	private void performHttpPostActionWithValidationErrors(String actionType, Integer roomId, String modalName) 
			throws Exception {
		
		this.mockMvc.perform(post("/admin/room/" + roomId + "/" + actionType)
							.with(csrf())
					)
					.andExpect(status().isOk())
					.andExpect(view().name("room/modals :: " + modalName));
	}
	
}
