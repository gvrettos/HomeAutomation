package eu.codingschool.homeautomation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
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

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.DeviceType;
import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.services.DeviceService;
import eu.codingschool.homeautomation.services.DeviceTypeService;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.services.RoomService;
import eu.codingschool.homeautomation.validators.DeviceValidator;

@RunWith(SpringRunner.class)
@WebMvcTest(DeviceController.class)
public class DeviceControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DeviceValidator deviceValidator;
	
	@MockBean
	@Qualifier("personServiceImpl")
	private PersonService personService;
	
	@MockBean
	@Qualifier("deviceServiceImpl")
	private DeviceService deviceService;
	
	@MockBean
	@Qualifier("deviceTypeServiceImpl")
	private DeviceTypeService deviceTypeService;
	
	@MockBean
	@Qualifier("roomServiceImpl")
	private RoomService roomService;

	private static final String ENDPOINT_DEVICES_BASE_URL = "/devices";
	private static final String ENDPOINT_ADMIN_DEVICES_BASE_URL = "/admin/devices";
	private static final String ENDPOINT_ADMIN_DEVICES_EDIT_OR_DELETE_BASE_URL = ENDPOINT_ADMIN_DEVICES_BASE_URL + "/{id}";
	private static final String REDIRECT_ENDPOINT_ADMIN_DEVICES_BASE_URL = "redirect:" + ENDPOINT_ADMIN_DEVICES_BASE_URL;

	private static final String MODAL_DEVICE_NEW_OR_EDIT = "device/modals :: modalNewOrEdit";
	private static final String MODAL_DEVICE_DELETE = "device/modals :: modalDelete";

	private static final String VIEW_DEVICE_LIST = "device/list";
	private static final String VIEW_DEVICE_GRID = "device/grid";
	
	private Room livingRoom;
	private Room kitchen;
	private Room bedroom;
	
	private Person admin;
	private List<Device> allDevices;
	private Set<Room> allRooms;
	
	private Person simpleUser;
	private List<Device> simpleUserAllDevices;
	private List<Device> simpleUserLivingRoomDevices;
	private Set<Room> simpleUserRooms;
	
	private static final String SIMPLE_USER_EMAIL = "user@foo.com"; 
	
	@Before
    public void setUp() throws Exception {
		admin = new Person();
		admin.setId(1);
		admin.setRole("ADMIN");
		
		simpleUser = new Person();
		simpleUser.setId(2);
		simpleUser.setRole("USER");
		simpleUser.setEmail(SIMPLE_USER_EMAIL);
		
		livingRoom = new Room("Living Room");
		livingRoom.setId(1);
		kitchen = new Room("Kitchen");
		kitchen.setId(2);
		bedroom = new Room("Bedroom");
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
		allRooms = new HashSet<>(Arrays.asList(livingRoom, kitchen, bedroom));
		
		simpleUserAllDevices = Arrays.asList(device2, device3);
		simpleUserLivingRoomDevices = Arrays.asList(device2);
		simpleUserRooms = new HashSet<>(Arrays.asList(device2.getRoom()));
		
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
	public void getAdminDevicesList_Should_LoadAllDevices_When_AdminLoggedInAndRequestingDevices() throws Exception {
		getAllDevices(ENDPOINT_ADMIN_DEVICES_BASE_URL, VIEW_DEVICE_LIST);
	}
	
	@Test
	@WithMockUser
	public void newDevice_Should_OpenModal_When_Requested() throws Exception {
		// when - then
		this.mockMvc.perform(post(ENDPOINT_ADMIN_DEVICES_BASE_URL + "/form").with(csrf()))
					.andExpect(status().isOk())
					.andExpect(view().name(MODAL_DEVICE_NEW_OR_EDIT));
	}
	
	@Test
	@WithMockUser
	public void addDevice_Should_SaveDevice_When_Provided() throws Exception {
		// when - then
		this.mockMvc.perform(post(ENDPOINT_ADMIN_DEVICES_BASE_URL).with(csrf()))
					.andExpect(redirectedUrl(ENDPOINT_ADMIN_DEVICES_BASE_URL))
					.andExpect(view().name(REDIRECT_ENDPOINT_ADMIN_DEVICES_BASE_URL));
		
		verify(deviceService, times(1)).save(any());
	}
	
	@Test
	@WithMockUser
	public void addDevice_Should_NotSaveDeviceType_When_NameNotProvided() throws Exception {
		// given 
		// mock the void method to return validation errors
		doAnswer(validator -> {
			((BeanPropertyBindingResult)validator.getArguments()[1]).addError(new ObjectError("name", "NotEmpty"));
			return null;
		}).when(deviceValidator).validate(any(), any());
		
		// when - then
		this.mockMvc.perform(post(ENDPOINT_ADMIN_DEVICES_BASE_URL).with(csrf()))
					.andExpect(status().isOk())
					.andExpect(view().name(MODAL_DEVICE_NEW_OR_EDIT));
		
		verifyZeroInteractions(deviceService);
	}
	
	@Test
	@WithMockUser
	public void viewDevice_Should_OpenModal_When_Requested() throws Exception {
		// given
		Integer deviceId = 2;
		
		// when - then
		this.mockMvc.perform(put(ENDPOINT_ADMIN_DEVICES_EDIT_OR_DELETE_BASE_URL + "/form", deviceId).with(csrf()))
					.andExpect(status().isOk())
					.andExpect(view().name(MODAL_DEVICE_NEW_OR_EDIT));
		
		verify(deviceService, times(1)).findById(deviceId);
		verify(deviceService, times(0)).findById(deviceId + 1);
		verify(deviceService, times(0)).findById(deviceId - 1);
	}
	
	@Test
	@WithMockUser
	public void editDevice_Should_SaveDevice_When_Provided() throws Exception {
		// given
		Integer deviceId = 2;
				
		// when - then
		this.mockMvc.perform(put(ENDPOINT_ADMIN_DEVICES_EDIT_OR_DELETE_BASE_URL, deviceId).with(csrf()))
					.andExpect(redirectedUrl(ENDPOINT_ADMIN_DEVICES_BASE_URL))
					.andExpect(view().name(REDIRECT_ENDPOINT_ADMIN_DEVICES_BASE_URL));
		
		verify(deviceService, times(1)).save(any());
	}
	
	@Test
	@WithMockUser
	public void editDevice_Should_NotSaveDevice_When_NameNotProvided() throws Exception {
		// given 
		Integer deviceId = 2;
		
		// mock the void method to return validation errors
		doAnswer(validator -> {
			((BeanPropertyBindingResult)validator.getArguments()[1]).addError(new ObjectError("name", "NotEmpty"));
			return null;
		}).when(deviceValidator).validate(any(), any());
		
		// when - then
		this.mockMvc.perform(put(ENDPOINT_ADMIN_DEVICES_EDIT_OR_DELETE_BASE_URL, deviceId).with(csrf()))
					.andExpect(status().isOk())
					.andExpect(view().name(MODAL_DEVICE_NEW_OR_EDIT));
		
		verifyZeroInteractions(deviceService);
	}
	
	@Test
	@WithMockUser
	public void confirmDeleteDevice_Should_OpenModal_When_Requested() throws Exception {
		// given
		Integer deviceId = 2;
		
		// when - then
		this.mockMvc.perform(delete(ENDPOINT_ADMIN_DEVICES_EDIT_OR_DELETE_BASE_URL + "/confirmation", deviceId).with(csrf()))
					.andExpect(status().isOk())
					.andExpect(view().name(MODAL_DEVICE_DELETE));
		
		verify(deviceService, times(1)).findById(deviceId);
		verify(deviceService, times(0)).findById(deviceId + 1);
		verify(deviceService, times(0)).findById(deviceId - 1);
	}
	
	@Test
	@WithMockUser
	public void doDeleteDevice_Should_CallDelete_When_Exists() throws Exception {
		// given
		Integer deviceId = 2;
				
		// when - then
		this.mockMvc.perform(delete(ENDPOINT_ADMIN_DEVICES_EDIT_OR_DELETE_BASE_URL, deviceId).with(csrf()))
					.andExpect(redirectedUrl(ENDPOINT_ADMIN_DEVICES_BASE_URL))
					.andExpect(view().name(REDIRECT_ENDPOINT_ADMIN_DEVICES_BASE_URL));
		
		verify(deviceService, times(1)).delete(any());
	}
	
	@Test
	@WithMockUser
	public void doDeleteDevice_Should_CallDelete_When_NotExists() throws Exception {
		// given
		Integer deviceId = 4; // this device does not exist
				
		// when - then
		this.mockMvc.perform(delete(ENDPOINT_ADMIN_DEVICES_EDIT_OR_DELETE_BASE_URL, deviceId).with(csrf()))
					.andExpect(redirectedUrl(ENDPOINT_ADMIN_DEVICES_BASE_URL))
					.andExpect(view().name(REDIRECT_ENDPOINT_ADMIN_DEVICES_BASE_URL));
		
		verify(deviceService, times(1)).delete(any());
	}
	
	@Test
	@WithMockUser
	public void showAdminDevices_Should_LoadAllDevices_When_AdminLoggedInAndRequestingDevices() throws Exception {
		getAllDevices(ENDPOINT_ADMIN_DEVICES_BASE_URL + "/user/all", VIEW_DEVICE_GRID);
	}
	
	@Test
	@WithMockUser
	public void showUserDevices_Should_LoadUserDevicesOnly_When_SimpleLoggedInAndRequestingDevices() throws Exception {
		// given
		Integer userId = 2;
		when(personService.getLoggedInUser()).thenReturn(new User(SIMPLE_USER_EMAIL, "pass-foo", Arrays.asList()));
		when(personService.findByEmail(personService.getLoggedInUser().getUsername())).thenReturn(simpleUser);
		
		// when - then
		this.mockMvc.perform(get(ENDPOINT_DEVICES_BASE_URL + "/user/" + userId))
			.andExpect(status().isOk()) 
			.andExpect(model().attribute("loggedInUser", simpleUser))
			.andExpect(model().attribute("devices", simpleUserAllDevices))
			.andExpect(model().attribute("rooms", simpleUserRooms))
			.andExpect(view().name(VIEW_DEVICE_GRID));
	}
	
	@Test
	@WithMockUser
	public void showAdminDevicesPerRoom_Should_LoadAllRoomDevices_When_AdminLoggedInAndRequestingRoomDevices() 
		throws Exception {
		
		// given
		Integer roomId = 1;
		when(personService.getLoggedInUser()).thenReturn(new User("user-foo", "pass-foo", Arrays.asList()));
		when(personService.findByEmail(any())).thenReturn(admin);
		
		// when - then
		this.mockMvc.perform(get(ENDPOINT_ADMIN_DEVICES_BASE_URL + "/user/all/room/" + roomId))
			.andExpect(status().isOk()) 
			.andExpect(model().attribute("loggedInUser", admin))
			.andExpect(model().attribute("devices", new ArrayList<>(livingRoom.getDevices())))
			.andExpect(model().attribute("rooms", allRooms))
			.andExpect(model().attribute("selectedRoom", livingRoom.getName()))
			.andExpect(view().name(VIEW_DEVICE_GRID));
	}
	
	@Test
	@WithMockUser
	public void showUserDevicesPerRoom_Should_LoadAllRoomDevicesAssigned_When_SimpleUserLoggedInAndRequestingRoomDevices() 
		throws Exception {
		
		// given
		Integer userId = 2;
		Integer roomId = 1;
		when(personService.getLoggedInUser()).thenReturn(new User(SIMPLE_USER_EMAIL, "pass-foo", Arrays.asList()));
		when(personService.findByEmail(any())).thenReturn(simpleUser);
		when(deviceService.findByPersonsIdAndRoomId(userId, roomId)).thenReturn(simpleUserLivingRoomDevices);
		
		// when - then
		this.mockMvc.perform(get(ENDPOINT_DEVICES_BASE_URL + "/user/" + userId + "/room/" + roomId))
			.andExpect(status().isOk()) 
			.andExpect(model().attribute("loggedInUser", simpleUser))
			.andExpect(model().attribute("devices", simpleUserLivingRoomDevices))
			.andExpect(model().attribute("rooms", simpleUserRooms))
			.andExpect(model().attribute("selectedRoom", livingRoom.getName()))
			.andExpect(view().name(VIEW_DEVICE_GRID));
	}
	
	@Test
	@WithMockUser
	public void updateDeviceStatus_Should_RedirectToAllDevices_When_AdminLoggedInAndSettingStatusToOn() 
			throws Exception {
		
		updateDeviceStatus_Should_RedirectToDevices_When_AnyUserLoggedInAndSettingStatusToAnything(
				new User("user-foo", "pass-foo", Arrays.asList()), admin, true, ENDPOINT_ADMIN_DEVICES_BASE_URL + "/user/all");
	}
	
	@Test
	@WithMockUser
	public void updateDeviceStatus_Should_RedirectToAllDevices_When_AdminLoggedInAndSettingStatusToOff() 
			throws Exception {
		
		updateDeviceStatus_Should_RedirectToDevices_When_AnyUserLoggedInAndSettingStatusToAnything(
				new User("user-foo", "pass-foo", Arrays.asList()), admin, false, ENDPOINT_ADMIN_DEVICES_BASE_URL + "/user/all");
	}
	
	@Test
	@WithMockUser
	public void updateDeviceStatus_Should_RedirectToUserDevices_When_SimpleUserLoggedInAndSettingStatusToOn() 
			throws Exception {
		
		updateDeviceStatus_Should_RedirectToDevices_When_AnyUserLoggedInAndSettingStatusToAnything(
				new User(SIMPLE_USER_EMAIL, "pass-foo", Arrays.asList()), 
				simpleUser,
				true,
				ENDPOINT_DEVICES_BASE_URL + "/user/" + simpleUser.getId());
	}
	
	@Test
	@WithMockUser
	public void updateDeviceStatus_Should_RedirectToUserDevices_When_SimpleUserLoggedInAndSettingStatusToOff() 
			throws Exception {
		
		updateDeviceStatus_Should_RedirectToDevices_When_AnyUserLoggedInAndSettingStatusToAnything(
				new User(SIMPLE_USER_EMAIL, "pass-foo", Arrays.asList()), 
				simpleUser, 
				false,
				ENDPOINT_DEVICES_BASE_URL + "/user/" + simpleUser.getId());
	}
	
	@Test
	@WithMockUser
	public void updateDeviceInformationValue_Should_RedirectToAllDevices_When_AdminLoggedInAndChangingValue() 
			throws Exception {
		
		updateDeviceInformationValue_Should_RedirectToDevices_When_AnyUserLoggedInAndChangingInformationValue(
				new User("user-foo", "pass-foo", Arrays.asList()), admin, "20", ENDPOINT_ADMIN_DEVICES_BASE_URL + "/user/all");
	}
	
	@Test
	@WithMockUser
	public void updateDeviceInformationValue_Should_RedirectToUserDevices_When_SimpleUserLoggedInAndChangingValue() 
			throws Exception {
		
		updateDeviceInformationValue_Should_RedirectToDevices_When_AnyUserLoggedInAndChangingInformationValue(
				new User(SIMPLE_USER_EMAIL, "pass-foo", Arrays.asList()), 
				simpleUser, 
				"20",
				ENDPOINT_DEVICES_BASE_URL + "/user/" + simpleUser.getId());
	}
	
	private void getAllDevices(String endpointUrl, String expectedView) throws Exception {
		// given
		when(personService.getLoggedInUser()).thenReturn(new User("user-foo", "pass-foo", Arrays.asList()));
		when(personService.findByEmail(any())).thenReturn(admin);
		
		// when - then
		this.mockMvc.perform(get(endpointUrl))
					.andExpect(status().isOk()) 
					.andExpect(model().attribute("loggedInUser", admin))
					.andExpect(model().attribute("devices", allDevices))
					.andExpect(model().attribute("rooms", allRooms))
					.andExpect(view().name(expectedView));
	}
	
	private void updateDeviceStatus_Should_RedirectToDevices_When_AnyUserLoggedInAndSettingStatusToAnything(
			User userDetails, Person user, boolean status, String expectedUrl) throws Exception {
		
		// given
		Integer deviceId = 1;
		when(personService.getLoggedInUser()).thenReturn(userDetails);
		when(personService.findByEmail(any())).thenReturn(user);
		
		// when - then
		this.mockMvc.perform(patch(ENDPOINT_DEVICES_BASE_URL + "/" + deviceId +  "/updateStatus/" + status)
								.with(csrf())
					)
					.andExpect(redirectedUrl(expectedUrl))
					.andExpect(view().name("redirect:" + expectedUrl));
	}
	
	private void updateDeviceInformationValue_Should_RedirectToDevices_When_AnyUserLoggedInAndChangingInformationValue(
			User userDetails, Person user, String value, String expectedUrl) throws Exception {
		
		// given
		Integer deviceId = 1;
		when(personService.getLoggedInUser()).thenReturn(userDetails);
		when(personService.findByEmail(any())).thenReturn(user);
		
		// when - then
		this.mockMvc.perform(patch(ENDPOINT_DEVICES_BASE_URL + "/" + deviceId +  "/updateValue/" + value)
								.with(csrf())
					)
					.andExpect(redirectedUrl(expectedUrl))
					.andExpect(view().name("redirect:" + expectedUrl));
	}
	
}
