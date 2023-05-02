package eu.codingschool.homeautomation.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.DeviceType;
import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.services.DeviceService;
import eu.codingschool.homeautomation.services.DeviceTypeService;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.services.RoomService;
import eu.codingschool.homeautomation.validators.DeviceValidator;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DeviceController {

	private static final String ENDPOINT_DEVICES_BASE_URL = "/devices";
	private static final String ENDPOINT_ADMIN_DEVICES_BASE_URL = "/admin" + ENDPOINT_DEVICES_BASE_URL;
	private static final String REDIRECT_ENDPOINT_ADMIN_DEVICES_BASE_URL = "redirect:" + ENDPOINT_ADMIN_DEVICES_BASE_URL;

	private static final String MODAL_DEVICE_NEW_OR_EDIT = "device/modals :: modalNewOrEdit";
	private static final String MODAL_DEVICE_DELETE = "device/modals :: modalDelete";

	private static final String VIEW_DEVICE_LIST = "device/list";
	public static final String VIEW_DEVICE_GRID = "device/grid";


	private final PersonService personService;

	private final DeviceService deviceService;

	private final DeviceTypeService deviceTypeService;

	private final RoomService roomService;

	private final DeviceValidator deviceValidator;

	public DeviceController(
			PersonService personService,
			DeviceService deviceService,
			DeviceTypeService deviceTypeService,
			RoomService roomService,
			DeviceValidator deviceValidator) {

		this.personService = personService;
		this.deviceService = deviceService;
		this.deviceTypeService = deviceTypeService;
		this.roomService = roomService;
		this.deviceValidator = deviceValidator;
	}
	
	
	@GetMapping(value = ENDPOINT_ADMIN_DEVICES_BASE_URL)
	public String getAdminDevicesList(Model model) {
		getAllDevices(model);
		return VIEW_DEVICE_LIST;
	}
	
	/**
	 * Display form for new device with empty fields.
	 */
	@PostMapping(value = ENDPOINT_ADMIN_DEVICES_BASE_URL + "/form")
	public String newDevice(Model model) {
		model.addAttribute("device", new Device());
		List<DeviceType> allDeviceTypes = deviceTypeService.findAll();
		Set<Room> allRooms = roomService.findAll();
		model.addAttribute("allDeviceTypes", allDeviceTypes);
		model.addAttribute("allRooms", allRooms);
		model.addAttribute("actionUrl", ENDPOINT_ADMIN_DEVICES_BASE_URL);
		model.addAttribute("actionType", "POST");
		model.addAttribute("modalTitle", "New");
		return MODAL_DEVICE_NEW_OR_EDIT;
	}
	
	/**
	 * Save a new device by submitting the form.
	 */
	@PostMapping(value = ENDPOINT_ADMIN_DEVICES_BASE_URL)
	public String addDevice(@ModelAttribute("device") Device device, BindingResult result) {
		return saveOrUpdateDevice(device, result);
	}
	
	/**
	 * Display form for an already saved device with pre-filled fields.
	 */
	@PutMapping(value = ENDPOINT_ADMIN_DEVICES_BASE_URL + "/{id}/form")
	public String viewDevice(@PathVariable(value="id") int id, Model model) {
		Device device = deviceService.findById(id);
		List<DeviceType> allDeviceTypes = deviceTypeService.findAll();
		Set<Room> allRooms = roomService.findAll();
		model.addAttribute("allDeviceTypes", allDeviceTypes);
		model.addAttribute("allRooms", allRooms);
		model.addAttribute("device", device);
		model.addAttribute("actionUrl", ENDPOINT_ADMIN_DEVICES_BASE_URL + "/" + id);
		model.addAttribute("actionType", "PUT");
		model.addAttribute("modalTitle", "Edit");
		return MODAL_DEVICE_NEW_OR_EDIT;
	}
	
	/**
	 * Update a device by submitting the form.
	 */
	@PutMapping(value = ENDPOINT_ADMIN_DEVICES_BASE_URL + "/{id}")
	public String editDevice(@ModelAttribute("device") Device device, BindingResult result) {
		return saveOrUpdateDevice(device, result);
	}
	
	/**
	 * Display a confirmation dialog before deleting a device.
	 */
	@DeleteMapping(value = ENDPOINT_ADMIN_DEVICES_BASE_URL + "/{id}/confirmation")
	public String confirmDeleteDevice(@PathVariable(value="id") int id, Model model) {
		Device device = deviceService.findById(id);
		model.addAttribute("device", device);
		model.addAttribute("actionUrl", ENDPOINT_ADMIN_DEVICES_BASE_URL + "/" + id);
		model.addAttribute("actionType", "DELETE");
		return MODAL_DEVICE_DELETE;
	}
	
	/**
	 * Delete the device after accepting the deletion confirmation.
	 */
	@DeleteMapping(value = ENDPOINT_ADMIN_DEVICES_BASE_URL + "/{id}")
	public String doDeleteDevice(@ModelAttribute("device") Device device, ModelMap model) {
		try {
			device = deviceService.findById(device.getId());
			deviceService.delete(device);
		} catch (DataIntegrityViolationException ex) {
			model.addAttribute("action", "delete device");
			model.addAttribute("entityName", device.getName());
			model.addAttribute("additionalMessage", "Please check if the device has been assigned to any user. Only unassigned devices can be deleted.");
			return "/error/422";
		}
		return REDIRECT_ENDPOINT_ADMIN_DEVICES_BASE_URL;
	}
	
	private String saveOrUpdateDevice(Device device, BindingResult result) {
		deviceValidator.validate(device, result);
		
		if (result.hasErrors()) {
			// reload the same page fragment
			return MODAL_DEVICE_NEW_OR_EDIT;
		}
		
		deviceService.save(device);
		return REDIRECT_ENDPOINT_ADMIN_DEVICES_BASE_URL;
	}
	
	private void getAllDevices(Model model) {
		UserDetails loggedInUser = personService.getLoggedInUser();
		if ( loggedInUser == null || loggedInUser.getUsername() == null ) {
			throw new AccessDeniedException("");
		}
		
		model.addAttribute("devices", deviceService.findAll());
		model.addAttribute("rooms", roomService.findAll());
		model.addAttribute("loggedInUser", personService.findByEmail(loggedInUser.getUsername()));
	}
	
	/**
	 * Display the devices that can be operated by an ADMIN only
	 */
	@GetMapping(value =  ENDPOINT_ADMIN_DEVICES_BASE_URL + "/user/all")
	public String showAdminDevices(Model model) {
		getAllDevices(model);
		return VIEW_DEVICE_GRID;
	}

	/**
	 * Display the devices that can be operated by any USER
	 */
	@GetMapping(value = ENDPOINT_DEVICES_BASE_URL + "/user/{id}")
	public String showUserDevices(@PathVariable(value="id") int userId, Model model) {
		
		UserDetails loggedInUserDetails = personService.getLoggedInUser();
		Person requestedUser = personService.findById(userId);
		if ( loggedInUserDetails == null 
				|| requestedUser == null 
				|| loggedInUserDetails.getUsername() == null 
				|| !loggedInUserDetails.getUsername().equals(requestedUser.getEmail()) ) {
			throw new AccessDeniedException("");
		}
    	
    	populateSideMenu(model, loggedInUserDetails);
    	model.addAttribute("devices", deviceService.findByPersonsId(userId));
		return VIEW_DEVICE_GRID;
	}
	
	/**
	 * Display all the devices for a specific room
	 */
	@GetMapping(value = ENDPOINT_ADMIN_DEVICES_BASE_URL + "/user/all/room/{roomId}")
	public String showAdminDevicesPerRoom(@PathVariable(value="roomId") int roomId, Model model) {
		
		UserDetails loggedInUser = personService.getLoggedInUser();
		if ( loggedInUser == null || loggedInUser.getUsername() == null ) {
			throw new AccessDeniedException("");
		}
		
		model.addAttribute("devices", deviceService.findAllByRoomId(roomId));
		model.addAttribute("rooms", roomService.findAll());
		model.addAttribute("selectedRoom", roomService.findById(roomId).getName());
		model.addAttribute("loggedInUser", personService.findByEmail(loggedInUser.getUsername()));
		return VIEW_DEVICE_GRID;
	}
	
	/**
	 * Display the devices that are assigned to a USER for a specific room
	 */
	@GetMapping(value = ENDPOINT_DEVICES_BASE_URL + "/user/{userId}/room/{roomId}")
	public String showUserDevicesPerRoom(
			@PathVariable(value="userId") int userId, 
			@PathVariable(value="roomId") int roomId, 
			Model model) {
		
		UserDetails loggedInUserDetails = personService.getLoggedInUser();
		Person requestedUser = personService.findById(userId);
		if ( loggedInUserDetails == null 
				|| requestedUser == null 
				|| loggedInUserDetails.getUsername() == null 
				|| !loggedInUserDetails.getUsername().equals(requestedUser.getEmail()) ) {
			throw new AccessDeniedException("");
		}
		
		populateSideMenu(model, loggedInUserDetails);
		model.addAttribute("devices", deviceService.findByPersonsIdAndRoomId(userId, roomId));
		model.addAttribute("selectedRoom", roomService.findById(roomId).getName());
		return VIEW_DEVICE_GRID;
	}
	
	/**
	 * Set the device on/off.
	 */
	// FIXME A simple user can update a device which is assigned to another user and not them!
	@PatchMapping(value = ENDPOINT_DEVICES_BASE_URL + "/{id}/updateStatus/{status}")
	public String updateDeviceStatus(
			@PathVariable(value="id") int deviceId, 
			@PathVariable(value="status") boolean status,
			HttpServletRequest request) {
		
		Device device = deviceService.findById(deviceId);
		device.setStatusOn(status);
		deviceService.save(device);
		
		return redirectPage(request);
	}
	
	/**
	 * Increase/Decrease the device's information value. 
	 */
	// FIXME A simple user can update a device which is assigned to another user and not them!
	@PatchMapping(value = ENDPOINT_DEVICES_BASE_URL + "/{id}/updateValue/{value}")
	public String updateDeviceInformationValue(
			@PathVariable(value="id") int deviceId, 
			@PathVariable(value="value") String informationValue,
			HttpServletRequest request) {
		
		Device device = deviceService.findById(deviceId);
		device.setInformationValue(informationValue);
		deviceService.save(device);
		
		return redirectPage(request);
	}
	
	private void populateSideMenu(Model model, UserDetails loggedInUserDetails) {
		Person loggedInUser = personService.findByEmail(loggedInUserDetails.getUsername());
		if (loggedInUser != null) {
			model.addAttribute(
					"rooms", 
					loggedInUser.isAdmin() ? roomService.findAll() : roomService.findByUser(loggedInUser.getId()));
		}
		model.addAttribute("loggedInUser", loggedInUser);
	}

	// FIXME the actual redirection URLs may be different
	// FIXME E.g. after ADMIN updates the status of a device in a specific room we do not want to return them back to all devices
	private String redirectPage(HttpServletRequest request) {
		UserDetails loggedInUser = personService.getLoggedInUser();
		if (loggedInUser != null && loggedInUser.getUsername() != null) {
			Person loggedInPerson = personService.findByEmail(loggedInUser.getUsername());
			if (loggedInPerson != null) {
				// We set the response status to 303: See Other as a workaround to make the redirection from a PATCH
				// method to a GET work.
				// When using XHR requests other than GET or POST and redirecting after the request then some browsers
				// will follow the redirect using the original request method. This may lead to undesirable behavior
				// such as a double PATCH here.
				request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.SEE_OTHER);
				if ("ADMIN".equals(loggedInPerson.getRole())) {
					return "redirect:" + ENDPOINT_ADMIN_DEVICES_BASE_URL + "/user/all";
				}
				else if ("USER".equals(loggedInPerson.getRole())) {
					return "redirect:" + ENDPOINT_DEVICES_BASE_URL + "/user/" + loggedInPerson.getId();
				}
			}
		}
		return null;
	}

}