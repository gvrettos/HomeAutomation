package eu.codingschool.homeautomation.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;

import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.services.RoomService;
import eu.codingschool.homeautomation.validators.RoomValidator;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@RunWith(SpringRunner.class)
public class RoomControllerTest {

	@Mock
	private RoomService roomService;

	@Mock
	private PersonService personService;

	@Mock
	private RoomValidator roomValidator;

	@InjectMocks
	private RoomController roomController;

	private static final String ENDPOINT_ADMIN_ROOMS_BASE_URL = "/admin/rooms";
	private static final String REDIRECT_ENDPOINT_ADMIN_ROOMS_BASE_URL = "redirect:" + ENDPOINT_ADMIN_ROOMS_BASE_URL;

	private static final String MODAL_ROOM_NEW_OR_EDIT = "room/modals :: modalNewOrEdit";
	private static final String MODAL_ROOM_DELETE = "room/modals :: modalDelete";

	private static final String VIEW_ROOM_LIST = "room/list";

	private List<Room> allRooms;
	
	@Before
    public void setUp() {
		Room room1 = new Room();
		room1.setId(1);
		Room room2 = new Room();
		room2.setId(2);
		
		allRooms = Arrays.asList(room1, room2);
		
		when(roomService.findAll()).thenReturn(allRooms);
		when(roomService.findById(1)).thenReturn(room1);
		when(roomService.findById(2)).thenReturn(room2);
    }
	
	@Test
	@WithMockUser
	public void getRooms_shouldLoadRoomListView_whenRequested() {
		// given
		when(roomService.findAll()).thenReturn(allRooms);

		// when
		String returnedView = roomController.getRooms(new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(VIEW_ROOM_LIST);
	}
	
	@Test
	public void newRoom_shouldOpenModal_whenRequested() {
		// when
		String returnedView = roomController.newRoom(new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(MODAL_ROOM_NEW_OR_EDIT);
	}

	@Test
	public void addRoom_shouldSaveRoom_whenProvided() {
		// when
		String returnedView = roomController.addRoom(
				new Room(),
				new MapBindingResult(new HashMap<>(), "foo")
		);

		// then
		verify(roomService, times(1)).save(any());
		assertThat(returnedView).isEqualTo(REDIRECT_ENDPOINT_ADMIN_ROOMS_BASE_URL);
	}

	@Test
	public void addRoom_shouldNotSaveRoom_whenNameNotProvided() {
		// given
		// mock the void method to return validation errors
		doAnswer(validator -> {
			((MapBindingResult)validator.getArgument(1)).addError(new ObjectError("name", "NotEmpty"));
			return null;
		}).when(roomValidator).validate(any(), any());

		// when
		String returnedView = roomController.addRoom(
				new Room(),
				new MapBindingResult(new HashMap<>(), "foo")
		);

		// then
		verifyZeroInteractions(roomService);
		assertThat(returnedView).isEqualTo(MODAL_ROOM_NEW_OR_EDIT);
	}

	@Test
	public void viewRoom_shouldOpenModal_whenRequested() {
		// given
		int roomId = 2;

		// when
		String returnedView = roomController.viewRoom(roomId, new RedirectAttributesModelMap());

		// then
		verify(roomService, times(1)).findById(roomId);
		verify(roomService, times(0)).findById(roomId + 1);
		verify(roomService, times(0)).findById(roomId - 1);
		assertThat(returnedView).isEqualTo(MODAL_ROOM_NEW_OR_EDIT);
	}

	@Test
	public void editRoom_shouldSaveRoom_whenProvided() {
		// when
		String returnedView = roomController.editRoom(
				new Room(),
				new MapBindingResult(new HashMap<>(), "foo")
		);

		// then
		verify(roomService, times(1)).save(any());
		assertThat(returnedView).isEqualTo(REDIRECT_ENDPOINT_ADMIN_ROOMS_BASE_URL);
	}

	@Test
	public void editRoom_shouldNotSaveRoom_whenNameNotProvided() {
		// given
		// mock the void method to return validation errors
		doAnswer(validator -> {
			((MapBindingResult)validator.getArgument(1)).addError(new ObjectError("name", "NotEmpty"));
			return null;
		}).when(roomValidator).validate(any(), any());

		// when
		String returnedView = roomController.editRoom(
				new Room(),
				new MapBindingResult(new HashMap<>(), "foo")
		);

		// then
		verifyZeroInteractions(roomService);
		assertThat(returnedView).isEqualTo(MODAL_ROOM_NEW_OR_EDIT);
	}

	@Test
	public void confirmDeleteRoom_shouldOpenModal_whenRequested() {
		// given
		int roomId = 2;

		// when
		String returnedView = roomController.confirmDeleteRoom(roomId, new RedirectAttributesModelMap());

		// then
		verify(roomService, times(1)).findById(roomId);
		verify(roomService, times(0)).findById(roomId + 1);
		verify(roomService, times(0)).findById(roomId - 1);
		assertThat(returnedView).isEqualTo(MODAL_ROOM_DELETE);
	}

	@Test
	public void doDeleteRoom_shouldCallDelete_whenExists() {
		// given
		int roomId = 2;
		Room room = new Room();
		room.setId(roomId);

		// when
		String returnedView = roomController.doDeleteRoom(
				room,
				new RedirectAttributesModelMap()
		);

		// then
		verify(roomService, times(1)).delete(any());
		assertThat(returnedView).isEqualTo(REDIRECT_ENDPOINT_ADMIN_ROOMS_BASE_URL);
	}

	@Test
	public void doDeleteRoom_shouldCallDelete_whenNotExists() {
		// given
		int roomId = 3; // this room does not exist
		Room room = new Room();
		room.setId(roomId);

		// when
		String returnedView = roomController.doDeleteRoom(
				room,
				new RedirectAttributesModelMap()
		);

		// then
		verify(roomService, times(1)).delete(any());
		assertThat(returnedView).isEqualTo(REDIRECT_ENDPOINT_ADMIN_ROOMS_BASE_URL);
	}
	
}
