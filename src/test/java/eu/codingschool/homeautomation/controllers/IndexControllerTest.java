package eu.codingschool.homeautomation.controllers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.services.RoomService;
import eu.codingschool.homeautomation.validators.PersonValidator;

@RunWith(SpringRunner.class)
@WebMvcTest(IndexController.class)
public class IndexControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PersonValidator personValidator;
	
	@MockBean
	@Qualifier("personServiceImpl")
	private PersonService personService;
	
	@MockBean
	@Qualifier("roomServiceImpl")
	private RoomService roomService;
	
	private Person admin;
	private Person simpleUser;
	
	private Set<Room> allRooms;
	private Set<Room> simpleUserRooms;
	
	@Before
    public void setUp() throws Exception {
		admin = new Person();
		admin.setRole("ADMIN");
		
		simpleUser = new Person();
		simpleUser.setRole("USER");
		
		Room room1 = new Room();
		room1.setId(1);
		Room room2 = new Room();
		room2.setId(2);
		
		allRooms = new HashSet<>(Arrays.asList(room1, room2));
		simpleUserRooms = new HashSet<>(Arrays.asList(room1));
		
		when(roomService.findAll()).thenReturn(allRooms);
		when(roomService.findByUser(simpleUser.getId())).thenReturn(simpleUserRooms);
    }
	
	@Test
	@WithMockUser
	public void home_Should_LoadIndexPage_When_AdminLoggedInAndRequestingIndex() throws Exception {
		loadIndexPageWhenUserLoggedIn("/index", admin, allRooms);
	}
	
	@Test
	@WithMockUser
	public void home_Should_LoadIndexPage_When_AdminLoggedInAndRequestingRoot() throws Exception {
		loadIndexPageWhenUserLoggedIn("/", admin, allRooms);
	}
	
	@Test
	@WithMockUser
	public void home_Should_LoadIndexPage1_When_SimpleUserLoggedInAndRequestingIndex() throws Exception {
		loadIndexPageWhenUserLoggedIn("/index", simpleUser, simpleUserRooms);
	}
	
	@Test
	@WithMockUser
	public void home_Should_LoadIndexPage_When_SimpleUserLoggedInAndRequestingRoot() throws Exception {
		loadIndexPageWhenUserLoggedIn("/", simpleUser, simpleUserRooms);
	}
	
	@Test
	@WithMockUser // TODO the /login URL should normally be permitted without a mock user
	public void login_Should_LoadLoginPage_When_RequestingLoginPage() throws Exception {
		// when - then
		this.mockMvc.perform(get("/login").with(csrf()))
					.andExpect(status().isOk())
					.andExpect(content().string(containsString("<h3>Login with Username and Password</h3>")));
	}
	
	@Test
	@WithMockUser // TODO the /registration URL should be permitted without a mock user
	public void registration_Should_LoadRegistrationPage_When_RequestingRegistrationPage() throws Exception {
		// when - then
		this.mockMvc.perform(get("/registration"))
					.andExpect(status().isOk())
					.andExpect(view().name("registration"));
	}
	
	@Test
	@WithMockUser // TODO the /registration URL should normally be permitted without a mock user
	public void registration_Should_SaveNewUser_When_SubmittedFromRegistrationPage() throws Exception {
		// when - then
		this.mockMvc.perform(post("/registration")
								.with(csrf())
								.param("name", "user2")
								.param("surname", "surname")
								.param("email", "user2@foo.com")
					)
					.andExpect(redirectedUrl("/index"))
					.andExpect(view().name("redirect:/index"));
	}
	
	private void loadIndexPageWhenUserLoggedIn(String url, Person loggedInUser, Set<Room> rooms) throws Exception {
		// given
		when(personService.findByEmail(any())).thenReturn(loggedInUser);
		
		// when - then
		this.mockMvc.perform(get(url))
//					.andDo(print())
					.andExpect(status().isOk()) 
					.andExpect(model().attribute("loggedInUser", loggedInUser))
					.andExpect(model().attribute("rooms", rooms))
					.andExpect(view().name("index"));
	}
	
}
