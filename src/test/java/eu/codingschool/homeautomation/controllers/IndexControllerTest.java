package eu.codingschool.homeautomation.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.services.RoomService;
import eu.codingschool.homeautomation.validators.PersonValidator;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@RunWith(SpringRunner.class)
public class IndexControllerTest {

	@Mock
	private PersonValidator personValidator;
	
	@Mock
	private PersonService personService;
	
	@Mock
	private RoomService roomService;

	@InjectMocks
	private IndexController indexController;

	private static final String ENDPOINT_ROOT = "/";
	private static final String VIEW_INDEX = "index";
	private static final String ENDPOINT_INDEX = ENDPOINT_ROOT + VIEW_INDEX;
	private static final String VIEW_LOGIN = "login";
	private static final String VIEW_REGISTRATION = "registration";
	
	private Person admin;
	private Person simpleUser;

	@Before
    public void setUp() {
		admin = new Person();
		admin.setRole("ADMIN");
		
		simpleUser = new Person();
		simpleUser.setRole("USER");
		
		Room room1 = new Room();
		room1.setId(1);
		Room room2 = new Room();
		room2.setId(2);

		List<Room> allRooms = Arrays.asList(room1, room2);
		List<Room> simpleUserRooms = Arrays.asList(room1);
		
		when(roomService.findAll()).thenReturn(allRooms);
		when(roomService.findByUser(simpleUser.getId())).thenReturn(simpleUserRooms);
    }
	
	@Test
	@WithMockUser
	public void home_shouldLoadIndexPage_whenAdminLoggedInAndRequestingIndex() {
		loadIndexPageWhenUserLoggedIn(admin);
	}
	
	@Test
	@WithMockUser
	public void home_shouldLoadIndexPage_whenAdminLoggedInAndRequestingRoot() {
		loadIndexPageWhenUserLoggedIn(admin);
	}
	
	@Test
	@WithMockUser
	public void home_shouldLoadIndexPage_whenSimpleUserLoggedInAndRequestingIndex() {
		loadIndexPageWhenUserLoggedIn(simpleUser);
	}
	
	@Test
	@WithMockUser
	public void home_shouldLoadIndexPage_whenSimpleUserLoggedInAndRequestingRoot() {
		loadIndexPageWhenUserLoggedIn(simpleUser);
	}
	
	@Test
	public void login_shouldLoadLoginPage_whenRequestingLoginPage() {
		// when
		String returnedView = indexController.login(new RedirectAttributesModelMap(), null, null);

		// then
		assertThat(returnedView).isEqualTo(VIEW_LOGIN);
	}
	
	@Test
	public void registration_shouldLoadRegistrationPage_whenRequestingRegistrationPage() {
		// when
		String returnedView = indexController.registration(new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(VIEW_REGISTRATION);
	}
	
	@Test
	public void registration_shouldSaveNewUser_whenSubmittedFromRegistrationPage() {
		// when
		String returnedView = indexController.registration(
				simpleUser,
				new BeanPropertyBindingResult(new HashMap<>(), "foo")
		);

		// then
		assertThat(returnedView).isEqualTo("redirect:" + ENDPOINT_INDEX);
	}
	
	private void loadIndexPageWhenUserLoggedIn(Person loggedInUser) {
		// given
		when(personService.findByEmail(any())).thenReturn(loggedInUser);

		// when
		String returnedView = indexController.home(new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(VIEW_INDEX);
	}
	
}
