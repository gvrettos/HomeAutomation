package eu.codingschool.homeautomation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

	private static final String ENDPOINT_ADMIN_ROOMS_BASE_URL = "/admin/rooms";
	private static final String ENDPOINT_ADMIN_ROOMS_EDIT_OR_DELETE_BASE_URL = ENDPOINT_ADMIN_ROOMS_BASE_URL + "/{id}";
	private static final String REDIRECT_ENDPOINT_ADMIN_ROOMS_BASE_URL = "redirect:" + ENDPOINT_ADMIN_ROOMS_BASE_URL;

	private static final String MODAL_ROOM_NEW_OR_EDIT = "room/modals :: modalNewOrEdit";
	private static final String MODAL_ROOM_DELETE = "room/modals :: modalDelete";

	private static final String VIEW_ROOM_LIST = "room/list";

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
		this.mockMvc.perform(get(ENDPOINT_ADMIN_ROOMS_BASE_URL))
					.andExpect(status().isOk()) 
					.andExpect(model().attribute("loggedInUser", admin))
					.andExpect(model().attribute("rooms", allRooms))
					.andExpect(view().name(VIEW_ROOM_LIST));
	}
	
	@Test
	@WithMockUser
	public void newRoom_Should_OpenModal_When_Requested() throws Exception {
		// when - then
		this.mockMvc.perform(post(ENDPOINT_ADMIN_ROOMS_BASE_URL + "/form").with(csrf()))
					.andExpect(status().isOk())
					.andExpect(view().name(MODAL_ROOM_NEW_OR_EDIT));
	}
	
	@Test
	@WithMockUser
	public void addRoom_Should_SaveRoom_When_Provided() throws Exception {
		// when - then
		this.mockMvc.perform(post(ENDPOINT_ADMIN_ROOMS_BASE_URL).with(csrf()))
					.andExpect(redirectedUrl(ENDPOINT_ADMIN_ROOMS_BASE_URL))
					.andExpect(view().name(REDIRECT_ENDPOINT_ADMIN_ROOMS_BASE_URL));
		
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
		this.mockMvc.perform(post(ENDPOINT_ADMIN_ROOMS_BASE_URL).with(csrf()))
					.andExpect(status().isOk())
					.andExpect(view().name(MODAL_ROOM_NEW_OR_EDIT));
		
		verifyZeroInteractions(roomService);
	}
	
	@Test
	@WithMockUser
	public void viewRoom_Should_OpenModal_When_Requested() throws Exception {
		// given
		Integer roomId = 2;
		
		// when - then
		this.mockMvc.perform(put(ENDPOINT_ADMIN_ROOMS_EDIT_OR_DELETE_BASE_URL + "/form", roomId).with(csrf()))
					.andExpect(status().isOk())
					.andExpect(view().name(MODAL_ROOM_NEW_OR_EDIT));
		
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
		this.mockMvc.perform(put(ENDPOINT_ADMIN_ROOMS_EDIT_OR_DELETE_BASE_URL, roomId).with(csrf()))
					.andExpect(redirectedUrl(ENDPOINT_ADMIN_ROOMS_BASE_URL))
					.andExpect(view().name(REDIRECT_ENDPOINT_ADMIN_ROOMS_BASE_URL));
		
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
		this.mockMvc.perform(put(ENDPOINT_ADMIN_ROOMS_EDIT_OR_DELETE_BASE_URL, roomId).with(csrf()))
					.andExpect(status().isOk())
					.andExpect(view().name(MODAL_ROOM_NEW_OR_EDIT));
		
		verifyZeroInteractions(roomService);
	}
	
	@Test
	@WithMockUser
	public void confirmDeleteRoom_Should_OpenModal_When_Requested() throws Exception {
		// given
		Integer roomId = 2;
		
		// when - then
		this.mockMvc.perform(delete(ENDPOINT_ADMIN_ROOMS_EDIT_OR_DELETE_BASE_URL + "/confirmation", roomId).with(csrf()))
					.andExpect(status().isOk())
					.andExpect(view().name(MODAL_ROOM_DELETE));
		
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
		this.mockMvc.perform(delete(ENDPOINT_ADMIN_ROOMS_EDIT_OR_DELETE_BASE_URL, roomId).with(csrf()))
					.andExpect(redirectedUrl(ENDPOINT_ADMIN_ROOMS_BASE_URL))
					.andExpect(view().name(REDIRECT_ENDPOINT_ADMIN_ROOMS_BASE_URL));
		
		verify(roomService, times(1)).delete(any());
	}
	
	@Test
	@WithMockUser
	public void doDeleteRoom_Should_CallDelete_When_NotExists() throws Exception {
		// given
		Integer roomId = 3; // this room does not exist
				
		// when - then
		this.mockMvc.perform(delete(ENDPOINT_ADMIN_ROOMS_EDIT_OR_DELETE_BASE_URL, roomId).with(csrf()))
					.andExpect(redirectedUrl(ENDPOINT_ADMIN_ROOMS_BASE_URL))
					.andExpect(view().name(REDIRECT_ENDPOINT_ADMIN_ROOMS_BASE_URL));
		
		verify(roomService, times(1)).delete(any());
	}
	
}
