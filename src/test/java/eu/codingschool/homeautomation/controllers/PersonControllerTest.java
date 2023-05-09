package eu.codingschool.homeautomation.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;

import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.services.DeviceService;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.services.RoomService;
import eu.codingschool.homeautomation.validators.PersonValidator;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@RunWith(SpringRunner.class)
public class PersonControllerTest {

	@Mock
	private PersonValidator personValidator;
	
	@Mock
	private PersonService personService;
	
	@Mock
	private DeviceService deviceService;
	
	@Mock
	private RoomService roomService;

	@InjectMocks
	private PersonController personController;

	private static final String ENDPOINT_ADMIN_PERSONS_BASE_URL = "/admin/people";
	private static final String REDIRECT_ENDPOINT_ADMIN_PERSONS_BASE_URL = "redirect:" + ENDPOINT_ADMIN_PERSONS_BASE_URL;

	private static final String VIEW_PERSON_LIST = "person/list";
	private static final String VIEW_ERROR_404 = "/error/404";

	private static final String MODAL_PERSON_NEW_OR_EDIT = "person/modals :: modalNewOrEdit";
	private static final String MODAL_PERSON_DELETE = "person/modals :: modalDelete";

	private List<Person> allPeople;

	@Before
    public void setUp() {
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
		List<Room> allRooms = Arrays.asList(room1, room2, room3);
		
		when(personService.findAll()).thenReturn(allPeople);
		when(roomService.findAll()).thenReturn(allRooms);
		when(personService.findById(1)).thenReturn(person1);
		when(personService.findById(2)).thenReturn(person2);
    }
	
	@Test
	@WithMockUser
	public void getPeople_shouldLoadAllPeople_whenLoggedIn() {
		// given
		when(personService.getLoggedInUser()).thenReturn(new User("user-foo", "pass-foo", Collections.emptyList()));
		
		// when
		String returnedView = personController.getPeople(new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(VIEW_PERSON_LIST);
	}

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Test(expected = AccessDeniedException.class)
	@WithMockUser
	public void getPeople_shouldThrowException_whenNotLoggedIn() {
		// when
		personController.getPeople(new RedirectAttributesModelMap());
	}

	@Test
	@WithMockUser
	public void viewPerson_shouldOpenModal_whenRequested() {
		// given
		int personId = 2;

		// when
		String returnedView = personController.viewPerson(personId, new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(MODAL_PERSON_NEW_OR_EDIT);
		verify(personService, times(1)).findById(personId);
		verify(personService, times(0)).findById(personId + 1);
		verify(personService, times(0)).findById(personId - 1);
	}

	@Test
	@WithMockUser
	public void editPerson_shouldSavePerson_whenProvided() {
		// given
		int personId = 2;
		when(personService.getLoggedInUser()).thenReturn(new User("user-foo", "pass-foo", Collections.emptyList()));

		// when
		String returnedView = personController.editPerson(
				allPeople.get(personId - 1),
				Collections.emptyList(),
				new MapBindingResult(new HashMap<>(), "foo"),
				new RedirectAttributesModelMap()
		);

		// then
		assertThat(returnedView).isEqualTo(REDIRECT_ENDPOINT_ADMIN_PERSONS_BASE_URL);
		verify(personService, times(1)).getLoggedInUser();
		verify(personService, times(1)).update(any(), any());
		verify(personService, times(1)).findByEmail(any());
	}

	@Test
	@WithMockUser
	public void editPerson_shouldNotSavePerson_whenNameNotProvided() {
		// given
		int personId = 2;
		when(personService.getLoggedInUser()).thenReturn(new User("user-foo", "pass-foo", Collections.emptyList()));

		// mock the void method to return validation errors
		doAnswer(validator -> {
			((BeanPropertyBindingResult)validator.getArguments()[1]).addError(new ObjectError("name", "NotEmpty"));
			return null;
		}).when(personValidator).validate(any(), any());

		// when
		String returnedView = personController.editPerson(
				allPeople.get(personId - 1),
				Collections.emptyList(),
				new BeanPropertyBindingResult(new HashMap<>(), "foo"),
				new RedirectAttributesModelMap()
		);

		// then
		assertThat(returnedView).isEqualTo(MODAL_PERSON_NEW_OR_EDIT);
		verify(personService, times(1)).getLoggedInUser();
		verifyNoMoreInteractions(personService);
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser
	public void editPerson_shouldThrowException_whenNotLoggedIn() {
		// given
		Integer personId = 2;

		// when
		personController.editPerson(
				personService.findById(personId),
				Collections.emptyList(),
				new MapBindingResult(new HashMap<>(), "foo"),
				new RedirectAttributesModelMap()
		);
	}

	@Test
	@WithMockUser
	public void confirmDeletePerson_shouldOpenModal_whenRequested() {
		// given
		int personId = 2;

		// when
		String returnedView = personController.confirmDeletePerson(personId, new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(MODAL_PERSON_DELETE);
		verify(personService, times(1)).findById(personId);
		verify(personService, times(0)).findById(personId + 1);
		verify(personService, times(0)).findById(personId - 1);
	}

	@Test
	@WithMockUser
	public void doDeletePerson_shouldCallDelete_whenExists() {
		// given
		int personId = 2;

		// when
		String returnedView = personController.doDeletePerson(personId);

		// then
		assertThat(returnedView).isEqualTo(REDIRECT_ENDPOINT_ADMIN_PERSONS_BASE_URL);
		verify(personService, times(1)).delete(any());
	}

	@Test
	@WithMockUser
	public void doDeletePerson_shouldNotCallDelete_whenNotExists() {
		// given
		int personId = 3; // this person does not exist

		// when
		String returnedView = personController.doDeletePerson(personId);

		// then
		assertThat(returnedView).isEqualTo("redirect:" + VIEW_ERROR_404);
		verify(personService, times(0)).delete(any());
	}
	
}
