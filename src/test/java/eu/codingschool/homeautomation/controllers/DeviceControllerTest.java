package eu.codingschool.homeautomation.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import eu.codingschool.homeautomation.repositories.projections.RoomDevicesCount;
import org.junit.Before;
import org.junit.Test;
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

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.DeviceType;
import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.services.DeviceService;
import eu.codingschool.homeautomation.services.DeviceTypeService;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.services.RoomService;
import eu.codingschool.homeautomation.validators.DeviceValidator;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.servlet.http.HttpServletRequest;

@RunWith(SpringRunner.class)
public class DeviceControllerTest {

	@Mock
	private DeviceValidator deviceValidator;
	
	@Mock
	private PersonService personService;
	
	@Mock
	private DeviceService deviceService;
	
	@Mock
	private DeviceTypeService deviceTypeService;
	
	@Mock
	private RoomService roomService;

	@InjectMocks
	private DeviceController deviceController;

	private static final String ENDPOINT_DEVICES_BASE_URL = "/devices";
	private static final String ENDPOINT_ADMIN_DEVICES_BASE_URL = "/admin/devices";
	private static final String REDIRECT_ENDPOINT_ADMIN_DEVICES_BASE_URL = "redirect:" + ENDPOINT_ADMIN_DEVICES_BASE_URL;

	private static final String MODAL_DEVICE_NEW_OR_EDIT = "device/modals :: modalNewOrEdit";
	private static final String MODAL_DEVICE_DELETE = "device/modals :: modalDelete";

	private static final String VIEW_DEVICE_LIST = "device/list";
	private static final String VIEW_DEVICE_GRID = "device/grid";

	private Person admin;
	private Person simpleUser;
	private List<Device> allDevices;

	private static final String SIMPLE_USER_EMAIL = "user@foo.com";
	private static final String ADMIN_USER_EMAIL = "admin@foo.com";
	
	@Before
    public void setUp() {
		admin = new Person();
		admin.setId(1);
		admin.setRole("ADMIN");
		admin.setEmail(ADMIN_USER_EMAIL);
		
		simpleUser = new Person();
		simpleUser.setId(2);
		simpleUser.setRole("USER");
		simpleUser.setEmail(SIMPLE_USER_EMAIL);

		Room livingRoom = new Room("Living Room");
		livingRoom.setId(1);
		Room kitchen = new Room("Kitchen");
		kitchen.setId(2);
		Room bedroom = new Room("Bedroom");
		kitchen.setId(3);
				
		Device device1 = new Device();
		device1.setId(1);
		device1.setDeviceType(new DeviceType("Thermostat", "Target temp."));
		device1.setRoom(livingRoom);
		Device device2 = new Device();
		device2.setId(2);
		device2.setDeviceType(new DeviceType("Lights", "Illumination"));
		device2.setRoom(livingRoom);
		Device device3 = new Device();
		device3.setId(3);
		device3.setDeviceType(new DeviceType("Door Lock", "Lock Status"));
		device3.setRoom(kitchen);
		
		livingRoom.setDevices(new HashSet<>(Arrays.asList(device1, device2)));
		
		allDevices = Arrays.asList(device1, device2, device3);
		List<Room> allRooms = Arrays.asList(livingRoom, kitchen, bedroom);

		List<Device> simpleUserAllDevices = Arrays.asList(device2, device3);
		List<RoomDevicesCount> simpleUserRooms = Arrays.asList(
				new RoomDevicesCount(device2.getRoom(), (long)Math.random())
		);
		
		when(deviceService.findAll()).thenReturn(allDevices);
		when(deviceService.findByPersonsId(2)).thenReturn(simpleUserAllDevices);
		when(deviceService.findById(1)).thenReturn(device1);
		when(deviceService.findById(2)).thenReturn(device2);
		when(deviceService.findAllByRoomId(livingRoom.getId())).thenReturn(new ArrayList<>(livingRoom.getDevices()));
		
		when(personService.findById(1)).thenReturn(null);
		when(personService.findById(2)).thenReturn(simpleUser);
		
		when(roomService.findAll()).thenReturn(allRooms);
		when(roomService.findById(1)).thenReturn(livingRoom);
		when(roomService.findByUser(simpleUser.getId())).thenReturn(simpleUserRooms);
    }
	
	@Test
	@WithMockUser
	public void getAdminDevicesList_shouldLoadAllDevices_whenAdminLoggedInAndRequestingDevices() {
		// given
		when(personService.getLoggedInUser())
				.thenReturn(new User(ADMIN_USER_EMAIL, "pass-foo", Collections.emptyList()));

		when(personService.findByEmail(ADMIN_USER_EMAIL)).thenReturn(admin);

		// when
		String returnedView = deviceController.getAdminDevicesList(new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(VIEW_DEVICE_LIST);
	}
	
	@Test
	@WithMockUser
	public void newDevice_shouldOpenModal_whenRequested() {
		// given
		when(personService.getLoggedInUser())
				.thenReturn(new User(ADMIN_USER_EMAIL, "pass-foo", Collections.emptyList()));

		when(personService.findByEmail(ADMIN_USER_EMAIL)).thenReturn(admin);

		// when
		String returnedView = deviceController.newDevice(new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(MODAL_DEVICE_NEW_OR_EDIT);
	}

	@Test
	@WithMockUser
	public void addDevice_shouldSaveDevice_whenProvided() {
		// given
		when(personService.getLoggedInUser())
				.thenReturn(new User(ADMIN_USER_EMAIL, "pass-foo", Collections.emptyList()));

		when(personService.findByEmail(ADMIN_USER_EMAIL)).thenReturn(admin);

		// when
		String returnedView = deviceController.addDevice(
				new Device(),
				new MapBindingResult(new HashMap<>(), "foo")
		);

		// then
		assertThat(returnedView).isEqualTo(REDIRECT_ENDPOINT_ADMIN_DEVICES_BASE_URL);
		verify(deviceService, times(1)).save(any());
	}

	@Test
	@WithMockUser
	public void addDevice_shouldNotSaveDeviceType_whenNameNotProvided() {
		// given
		when(personService.getLoggedInUser())
				.thenReturn(new User(ADMIN_USER_EMAIL, "pass-foo", Collections.emptyList()));

		when(personService.findByEmail(ADMIN_USER_EMAIL)).thenReturn(admin);

		// mock the void method to return validation errors
		doAnswer(validator -> {
			((BeanPropertyBindingResult)validator.getArguments()[1]).addError(new ObjectError("name", "NotEmpty"));
			return null;
		}).when(deviceValidator).validate(any(), any());

		// when
		String returnedView = deviceController.addDevice(
				new Device(),
				new BeanPropertyBindingResult(new HashMap<>(), "foo")
		);

		// then
		assertThat(returnedView).isEqualTo(MODAL_DEVICE_NEW_OR_EDIT);
		verifyZeroInteractions(deviceService);
	}

	@Test
	@WithMockUser
	public void viewDevice_shouldOpenModal_whenRequested() {
		// given
		int deviceId = 2;
		when(personService.getLoggedInUser())
				.thenReturn(new User(ADMIN_USER_EMAIL, "pass-foo", Collections.emptyList()));

		when(personService.findByEmail(ADMIN_USER_EMAIL)).thenReturn(admin);

		// when
		String returnedView = deviceController.viewDevice(2, new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(MODAL_DEVICE_NEW_OR_EDIT);
		verify(deviceService, times(1)).findById(deviceId);
		verify(deviceService, times(0)).findById(deviceId + 1);
		verify(deviceService, times(0)).findById(deviceId - 1);
	}

	@Test
	@WithMockUser
	public void editDevice_shouldSaveDevice_whenProvided() {
		// given
		Integer deviceId = 2;
		when(personService.getLoggedInUser())
				.thenReturn(new User(ADMIN_USER_EMAIL, "pass-foo", Collections.emptyList()));

		when(personService.findByEmail(ADMIN_USER_EMAIL)).thenReturn(admin);

		// when
		String returnedView = deviceController.editDevice(
				deviceService.findById(deviceId),
				new MapBindingResult(new HashMap<>(), "foo")
		);

		// then
		assertThat(returnedView).isEqualTo(REDIRECT_ENDPOINT_ADMIN_DEVICES_BASE_URL);
		verify(deviceService, times(1)).save(any());
	}

	@Test
	@WithMockUser
	public void editDevice_shouldNotSaveDevice_whenNameNotProvided() {
		// given
		int deviceId = 2;
		when(personService.getLoggedInUser())
				.thenReturn(new User(ADMIN_USER_EMAIL, "pass-foo", Collections.emptyList()));

		when(personService.findByEmail(ADMIN_USER_EMAIL)).thenReturn(admin);

		// mock the void method to return validation errors
		doAnswer(validator -> {
			((BeanPropertyBindingResult)validator.getArguments()[1]).addError(new ObjectError("name", "NotEmpty"));
			return null;
		}).when(deviceValidator).validate(any(), any());

		// when
		String returnedView = deviceController.editDevice(
				allDevices.get(deviceId - 1),
				new BeanPropertyBindingResult(new HashMap<>(), "foo")
		);

		// then
		assertThat(returnedView).isEqualTo(MODAL_DEVICE_NEW_OR_EDIT);
		verifyZeroInteractions(deviceService);
	}

	@Test
	@WithMockUser
	public void confirmDeleteDevice_shouldOpenModal_whenRequested() {
		// given
		int deviceId = 2;
		when(personService.getLoggedInUser())
				.thenReturn(new User(ADMIN_USER_EMAIL, "pass-foo", Collections.emptyList()));

		when(personService.findByEmail(ADMIN_USER_EMAIL)).thenReturn(admin);

		// when
		String returnedView = deviceController.confirmDeleteDevice(deviceId, new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(MODAL_DEVICE_DELETE);
		verify(deviceService, times(1)).findById(deviceId);
		verify(deviceService, times(0)).findById(deviceId + 1);
		verify(deviceService, times(0)).findById(deviceId - 1);
	}

	@Test
	@WithMockUser
	public void doDeleteDevice_shouldCallDelete_whenExists() {
		// given
		Integer deviceId = 2;
		when(personService.getLoggedInUser())
				.thenReturn(new User(ADMIN_USER_EMAIL, "pass-foo", Collections.emptyList()));

		when(personService.findByEmail(ADMIN_USER_EMAIL)).thenReturn(admin);

		// when
		String returnedView = deviceController.doDeleteDevice(
				deviceService.findById(deviceId),
				new RedirectAttributesModelMap()
		);

		// then
		assertThat(returnedView).isEqualTo(REDIRECT_ENDPOINT_ADMIN_DEVICES_BASE_URL);
		verify(deviceService, times(1)).delete(any());
	}

	@Test
	@WithMockUser
	public void doDeleteDevice_shouldCallDelete_whenNotExists() {
		// given
		int deviceId = 4; // this device does not exist
		Device device = new Device();
		device.setId(deviceId);

		when(personService.getLoggedInUser())
				.thenReturn(new User(ADMIN_USER_EMAIL, "pass-foo", Collections.emptyList()));

		when(personService.findByEmail(ADMIN_USER_EMAIL)).thenReturn(admin);

		// when
		String returnedView = deviceController.doDeleteDevice(device, new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(REDIRECT_ENDPOINT_ADMIN_DEVICES_BASE_URL);
		verify(deviceService, times(1)).delete(any());
	}

	@Test
	@WithMockUser
	public void showAdminDevices_shouldLoadAllDevices_whenAdminLoggedInAndRequestingDevices() {
		// given
		when(personService.getLoggedInUser())
				.thenReturn(new User(ADMIN_USER_EMAIL, "pass-foo", Collections.emptyList()));

		when(personService.findByEmail(ADMIN_USER_EMAIL)).thenReturn(admin);

		// when
		String returnedView = deviceController.showAdminDevices(new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(VIEW_DEVICE_GRID);
	}

	@Test
	@WithMockUser
	public void showUserDevices_shouldLoadUserDevicesOnly_whenSimpleLoggedInAndRequestingDevices() {
		// given
		int userId = 2;
		when(personService.getLoggedInUser())
				.thenReturn(new User(SIMPLE_USER_EMAIL, "pass-foo", Collections.emptyList()));

		when(personService.findByEmail(SIMPLE_USER_EMAIL)).thenReturn(simpleUser);

		// when
		String returnedView = deviceController.showUserDevices(userId, new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(VIEW_DEVICE_GRID);
	}

	@Test
	@WithMockUser
	public void showAdminDevicesPerRoom_shouldLoadAllRoomDevices_whenAdminLoggedInAndRequestingRoomDevices() {
		// given
		int roomId = 1;
		when(personService.getLoggedInUser())
				.thenReturn(new User(ADMIN_USER_EMAIL, "pass-foo", Collections.emptyList()));

		when(personService.findByEmail(ADMIN_USER_EMAIL)).thenReturn(admin);

		// when
		String returnedView = deviceController.showAdminDevicesPerRoom(roomId, new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(VIEW_DEVICE_GRID);
	}

	@Test
	@WithMockUser
	public void showUserDevicesPerRoom_shouldLoadAllRoomDevicesAssigned_whenSimpleUserLoggedInAndRequestingRoomDevices() {
		// given
		int userId = 2;
		int roomId = 1;
		when(personService.getLoggedInUser())
				.thenReturn(new User(SIMPLE_USER_EMAIL, "pass-foo", Collections.emptyList()));

		when(personService.findByEmail(SIMPLE_USER_EMAIL)).thenReturn(simpleUser);

		// when
		String returnedView = deviceController.showUserDevicesPerRoom(userId, roomId, new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(VIEW_DEVICE_GRID);
	}

	@Test
	@WithMockUser
	public void updateDeviceStatus_shouldRedirectToAllDevices_whenAdminLoggedInAndSettingStatusToOn() {
		updateDeviceStatus_shouldRedirectToDevices_whenAnyUserLoggedInAndSettingStatusToAnything(
				new User(ADMIN_USER_EMAIL, "pass-foo", Collections.emptyList()),
				admin,
				true,
				ENDPOINT_ADMIN_DEVICES_BASE_URL + "/user/all"
		);
	}

	@Test
	@WithMockUser
	public void updateDeviceStatus_shouldRedirectToAllDevices_whenAdminLoggedInAndSettingStatusToOff() {
		updateDeviceStatus_shouldRedirectToDevices_whenAnyUserLoggedInAndSettingStatusToAnything(
				new User(ADMIN_USER_EMAIL, "pass-foo", Collections.emptyList()),
				admin,
				false,
				ENDPOINT_ADMIN_DEVICES_BASE_URL + "/user/all"
		);
	}

	@Test
	@WithMockUser
	public void updateDeviceStatus_shouldRedirectToUserDevices_whenSimpleUserLoggedInAndSettingStatusToOn() {
		updateDeviceStatus_shouldRedirectToDevices_whenAnyUserLoggedInAndSettingStatusToAnything(
				new User(SIMPLE_USER_EMAIL, "pass-foo", Collections.emptyList()),
				simpleUser,
				true,
				ENDPOINT_DEVICES_BASE_URL + "/user/" + simpleUser.getId()
		);
	}

	@Test(expected = AccessDeniedException.class) // then
	@WithMockUser
	public void updateDeviceStatus_shouldFail_whenSimpleUserLoggedInAndSettingStatusToOnForUnauthorizedDevice() {
		// given
		int deviceId = 1;
		String value = String.valueOf(new Random().nextInt(101));
		User userDetails = new User(SIMPLE_USER_EMAIL, "pass-foo", Collections.emptyList());
		when(personService.getLoggedInUser()).thenReturn(userDetails);
		when(personService.findByEmail(any())).thenReturn(simpleUser);

		// when
		deviceController.updateDeviceStatus(deviceId, true, mock(HttpServletRequest.class));
	}

	@Test
	@WithMockUser
	public void updateDeviceStatus_shouldRedirectToUserDevices_whenSimpleUserLoggedInAndSettingStatusToOff() {
		updateDeviceStatus_shouldRedirectToDevices_whenAnyUserLoggedInAndSettingStatusToAnything(
				new User(SIMPLE_USER_EMAIL, "pass-foo", Collections.emptyList()),
				simpleUser,
				false,
				ENDPOINT_DEVICES_BASE_URL + "/user/" + simpleUser.getId()
		);
	}

	@Test(expected = AccessDeniedException.class) // then
	@WithMockUser
	public void updateDeviceStatus_shouldFail_whenSimpleUserLoggedInAndSettingStatusToOffForUnauthorizedDevice() {
		// given
		int deviceId = 1;
		String value = String.valueOf(new Random().nextInt(101));
		User userDetails = new User(SIMPLE_USER_EMAIL, "pass-foo", Collections.emptyList());
		when(personService.getLoggedInUser()).thenReturn(userDetails);
		when(personService.findByEmail(any())).thenReturn(simpleUser);

		// when
		deviceController.updateDeviceStatus(deviceId, false, mock(HttpServletRequest.class));
	}

	@Test
	@WithMockUser
	public void updateDeviceInformationValue_shouldRedirectToAllDevices_When_AdminLoggedInAndChangingValue() {
		updateDeviceInformationValue_shouldRedirectToDevices_whenAnyUserLoggedInAndChangingInformationValue(
				new User(ADMIN_USER_EMAIL, "pass-foo", Collections.emptyList()),
				admin,
				ENDPOINT_ADMIN_DEVICES_BASE_URL + "/user/all"
		);
	}

	@Test
	@WithMockUser
	public void updateDeviceInformationValue_shouldRedirectToUserDevices_whenSimpleUserLoggedInAndChangingValue() {
		updateDeviceInformationValue_shouldRedirectToDevices_whenAnyUserLoggedInAndChangingInformationValue(
				new User(SIMPLE_USER_EMAIL, "pass-foo", Collections.emptyList()),
				simpleUser,
				ENDPOINT_DEVICES_BASE_URL + "/user/" + simpleUser.getId()
		);
	}

	@Test(expected = AccessDeniedException.class) // then
	@WithMockUser
	public void updateDeviceInformationValue_shouldFail_whenSimpleUserLoggedInAndChangingValueForUnauthorizedDevice() {
		// given
		int deviceId = 1;
		String value = String.valueOf(new Random().nextInt(101));
		User userDetails = new User(SIMPLE_USER_EMAIL, "pass-foo", Collections.emptyList());
		when(personService.getLoggedInUser()).thenReturn(userDetails);
		when(personService.findByEmail(any())).thenReturn(simpleUser);

		// when
		deviceController.updateDeviceInformationValue(deviceId, value, mock(HttpServletRequest.class));
	}

	private void updateDeviceStatus_shouldRedirectToDevices_whenAnyUserLoggedInAndSettingStatusToAnything(
			User userDetails, Person user, boolean status, String expectedUrl) {

		// given
		int deviceId = 1;
		Device device = new Device();
		device.setId(deviceId);
		when(personService.getLoggedInUser()).thenReturn(userDetails);
		when(personService.findByEmail(user.getEmail())).thenReturn(user);
		if (!user.isAdmin()) {
			// we assume that device belongs to the user if they are not ADMIN
			user.setDevices(new HashSet<>(Arrays.asList(allDevices.get(deviceId - 1))));
		}

		// when
		String returnedView = deviceController.updateDeviceStatus(deviceId, status, mock(HttpServletRequest.class));

		// then
		assertThat(returnedView).isEqualTo("redirect:" + expectedUrl);
	}

	private void updateDeviceInformationValue_shouldRedirectToDevices_whenAnyUserLoggedInAndChangingInformationValue(
			User userDetails, Person user, String expectedUrl) {

		// given
		int deviceId = 1;
		String value = String.valueOf(new Random().nextInt(101));
		when(personService.getLoggedInUser()).thenReturn(userDetails);
		when(personService.findByEmail(any())).thenReturn(user);
		if (!user.isAdmin()) {
			// we assume that device belongs to the user if they are not ADMIN
			user.setDevices(new HashSet<>(Arrays.asList(allDevices.get(deviceId - 1))));
		}

		// when
		String returnedView = deviceController.updateDeviceInformationValue(deviceId, value, mock(HttpServletRequest.class));

		// then
		assertThat(returnedView).isEqualTo("redirect:" + expectedUrl);
	}
	
}
