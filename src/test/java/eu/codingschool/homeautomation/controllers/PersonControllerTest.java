package eu.codingschool.homeautomation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;

import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.services.DeviceService;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.services.RoomService;
import eu.codingschool.homeautomation.validators.PersonValidator;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PersonValidator personValidator;
	
	@MockBean
	@Qualifier("personServiceImpl")
	private PersonService personService;
	
	@MockBean
	@Qualifier("deviceServiceImpl")
	private DeviceService deviceService;
	
	@MockBean
	@Qualifier("roomServiceImpl")
	private RoomService roomService;
	
	private Person admin;
	private List<Person> allPeople;
	private Set<Room> allRooms;
	
	@Before
    public void setUp() throws Exception {
		admin = new Person();
		admin.setRole("ADMIN");
		
		Person person1 = new Person();
		person1.setId(1);
		Person person2 = new Person();
		person2.setId(2);
		allPeople = Arrays.asList(person1, person2);
		
		Room room1 = new Room();
		room1.setId(1);
		Room room2 = new Room();
		room2.setId(2);
		Room room3 = new Room();
		room2.setId(3);
		allRooms = new HashSet<>(Arrays.asList(room1, room2, room3));
		
		when(personService.findAll()).thenReturn(allPeople);
		when(roomService.findAll()).thenReturn(allRooms);
		when(personService.findById(1)).thenReturn(person1);
		when(personService.findById(2)).thenReturn(person2);
    }
	
	@Test
	@WithMockUser
	public void getPeople_Should_LoadAllPeople_When_AdminLoggedInAndRequestingPeople() throws Exception {
		// given
		when(personService.getLoggedInUser()).thenReturn(new User("user-foo", "pass-foo", Arrays.asList()));
		when(personService.findByEmail(any())).thenReturn(admin);
		
		// when - then
		this.mockMvc.perform(get("/admin/person/list"))
					.andExpect(status().isOk()) 
					.andExpect(model().attribute("loggedInUser", admin))
					.andExpect(model().attribute("rooms", allRooms))
					.andExpect(view().name("person/list"));
	}
	
	@Test
	@WithMockUser
	public void getPeople_Should_DenyAccess_When_NotLoggedIn() throws Exception {
		// when - then
		this.mockMvc.perform(get("/admin/person/list"))
					.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser
	public void viewPerson_Should_OpenModal_When_Requested() throws Exception {
		// given
		Integer personId = 2;
		
		// when - then
		performHttpGetAction("edit", personId, "modalNewOrEdit");
		
		verify(personService, times(1)).findById(personId);
		verify(personService, times(0)).findById(personId + 1);
		verify(personService, times(0)).findById(personId - 1);
	}
	
	@Test
	@WithMockUser
	public void editPerson_Should_SavePerson_When_Provided() throws Exception {
		// given
		Integer personId = 2;
		when(personService.getLoggedInUser()).thenReturn(new User("user-foo", "pass-foo", Arrays.asList()));
		when(personService.findByEmail(any())).thenReturn(admin);
				
		// when - then
		performHttpPostAction("edit", personId, "/admin/person/list");
		
		verify(personService, times(1)).getLoggedInUser();
		verify(personService, times(1)).update(any(), any());
		verify(personService, times(1)).findByEmail(any());
	}
	
	@Test
	@WithMockUser
	public void editPerson_Should_NotSavePerson_When_NameNotProvided() throws Exception {
		// given 
		Integer personId = 2;
		when(personService.getLoggedInUser()).thenReturn(new User("user-foo", "pass-foo", Arrays.asList()));
		
		// mock the void method to return validation errors
		doAnswer(validator -> {
			((BeanPropertyBindingResult)validator.getArguments()[1]).addError(new ObjectError("name", "NotEmpty"));
			return null;
		}).when(personValidator).validate(any(), any());
		
		// when - then
		performHttpPostActionWithValidationErrors("edit", personId, "modalNewOrEdit");
		
		verify(personService, times(1)).getLoggedInUser();
		verifyNoMoreInteractions(personService);
	}
	
	@Test
	@WithMockUser
	public void confirmDeletePerson_Should_OpenModal_When_Requested() throws Exception {
		// given
		Integer personId = 2;
		
		// when - then
		performHttpGetAction("delete", personId, "modalDelete");
		
		verify(personService, times(1)).findById(personId);
		verify(personService, times(0)).findById(personId + 1);
		verify(personService, times(0)).findById(personId - 1);
	}
	
	@Test
	@WithMockUser
	public void doDeletePerson_Should_CallDelete_When_Exists() throws Exception {
		// given
		Integer personId = 2;
				
		// when - then
		performHttpPostAction("delete", personId, "/admin/person/list");
		
		verify(personService, times(1)).delete(any());
	}
	
	@Test
	@WithMockUser
	public void doDeletePerson_Should_NotCallDelete_When_NotExists() throws Exception {
		// given
		Integer personId = 3; // this room does not exist
				
		// when - then
		performHttpPostAction("delete", personId, "/error/404");
		
		verify(personService, times(0)).delete(any());
	}
	
	private void performHttpGetAction(String actionType, Integer personId, String modalName) throws Exception {
		this.mockMvc.perform(get("/admin/person/" + (personId != -1 ? personId : "") + "/" + actionType))
					.andExpect(status().isOk())
					.andExpect(view().name("person/modals :: " + modalName));
	}
	
	private void performHttpPostAction(String actionType, Integer personId, String expectedUrl) throws Exception {
		this.mockMvc.perform(post("/admin/person/" + (personId != -1 ? personId : "") + "/" + actionType)
								.with(csrf())
					)
					.andExpect(redirectedUrl(expectedUrl))
					.andExpect(view().name("redirect:" + expectedUrl));
	}
	
	private void performHttpPostActionWithValidationErrors(String actionType, Integer personId, String modalName) 
			throws Exception {
		
		this.mockMvc.perform(post("/admin/person/" + personId + "/" + actionType)
							.with(csrf())
					)
					.andExpect(status().isOk())
					.andExpect(view().name("person/modals :: " + modalName));
	}
	
}
