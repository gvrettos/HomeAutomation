package eu.codingschool.homeautomation.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.DeviceType;
import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.services.DeviceService;
import eu.codingschool.homeautomation.services.DeviceTypeService;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.services.RoomService;
import eu.codingschool.homeautomation.validators.DeviceValidator;

@Controller
public class DeviceController {
	
	@Autowired
	private PersonService personService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private DeviceTypeService deviceTypeService;
	
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private DeviceValidator deviceValidator;
	
	
	@RequestMapping(value = "/admin/device/list", method = RequestMethod.GET)
	public String getAdminDevicesList(Model model) {
		getAllDevices(model);
		return "device/list";
	}
	
	/**
	 * Display form for new device type with empty fields.
	 */
	@RequestMapping(value = "/admin/device/new", method = RequestMethod.GET)
	public String newDevice(Model model) {
		model.addAttribute("device", new Device());
		List<DeviceType> allDeviceTypes = deviceTypeService.findAll();
		Set<Room> allRooms = roomService.findAll();
		model.addAttribute("allDeviceTypes", allDeviceTypes);
		model.addAttribute("allRooms", allRooms);
		model.addAttribute("actionUrl", "/admin/device/new");
		model.addAttribute("modalTitle", "New");
		return "device/modals :: modalNewOrEdit";
	}
	
	/**
	 * Save a new device type by submitting the form.
	 */
	@RequestMapping(value = "/admin/device/new", method = RequestMethod.POST)
	public String addDevice(@ModelAttribute("device") Device device, BindingResult result, 
			ModelMap model) {
		return saveOrUpdateDevice(device, result);
	}
	
	/**
	 * Display form for an already saved device type with pre-filled fields.
	 */
	@RequestMapping(value = "/admin/device/{id}/edit", method = RequestMethod.GET)
	public String viewDevice(@PathVariable(value="id") int id, Model model) {
		Device device = deviceService.findById(id);
		List<DeviceType> allDeviceTypes = deviceTypeService.findAll();
		Set<Room> allRooms = roomService.findAll();
		model.addAttribute("allDeviceTypes", allDeviceTypes);
		model.addAttribute("allRooms", allRooms);
		model.addAttribute("device", device);
		model.addAttribute("actionUrl", "/admin/device/" + id + "/edit");
		model.addAttribute("modalTitle", "Edit");
		return "device/modals :: modalNewOrEdit";
	}
	
	/**
	 * Update a device type by submitting the form.
	 */
	@RequestMapping(value = "/admin/device/{id}/edit", method = RequestMethod.POST)
	public String editDevice(@ModelAttribute("device") Device device, BindingResult result, ModelMap model) {
		return saveOrUpdateDevice(device, result);
	}
	
	/**
	 * Display a confirmation dialog before deleting a device type.
	 */
	@RequestMapping(value = "/admin/device/{id}/delete", method = RequestMethod.GET)
	public String confrimDeleteDevice(@PathVariable(value="id") int id, Model model) {
		Device device = deviceService.findById(id);
		model.addAttribute("device", device);
		model.addAttribute("actionUrl", "/admin/device/" + id + "/delete");
		return "device/modals :: modalDelete";
	}
	
	/**
	 * Delete the device type after accepting the deletion confirmation.
	 */
	@RequestMapping(value = "/admin/device/{id}/delete", method = RequestMethod.POST)
	public String doDeleteDevice(@ModelAttribute("device") Device device, BindingResult result, ModelMap model) {
		try {
			device = deviceService.findById(device.getId());
			deviceService.delete(device);
		} catch (DataIntegrityViolationException ex) {
			model.addAttribute("action", "delete device");
			model.addAttribute("entityName", device.getName());
			model.addAttribute("additionalMessage", "Please check if the device has been assigned to any user. Only unassigned devices can be deleted.");
			return "/error/422";
		}
		return "redirect:/admin/device/list";
	}
	
	private String saveOrUpdateDevice(Device device, BindingResult result) {
		deviceValidator.validate(device, result);
		
		if (result.hasErrors()) {
			// reload the same page fragment
			return "device/modals :: modalNewOrEdit";
		}
		
		deviceService.save(device);
		return "redirect:/admin/device/list";
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
	 * Display the devices that can be operated by an ADMIN
	 */
	@RequestMapping(value = "/admin/device/user/all", method = RequestMethod.GET)
	public String showAdminDevices(Model model) {
		getAllDevices(model);
		return "userDevices/grid";
	}
	
	/**
	 * Display the devices that can be operated by any USER
	 */
	@RequestMapping(value = "/device/user/{id}", method = RequestMethod.GET)
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
		return "userDevices/grid";
	}
	
	/**
	 * Display all the devices for a specific room
	 */
	@RequestMapping(value = "/admin/device/user/all/room/{roomId}", method = RequestMethod.GET)
	public String showAdminDevicesPerRoom(@PathVariable(value="roomId") int roomId, Model model) {
		
		UserDetails loggedInUser = personService.getLoggedInUser();
		if ( loggedInUser == null || loggedInUser.getUsername() == null ) {
			throw new AccessDeniedException("");
		}
		
		model.addAttribute("devices", deviceService.findAllByRoomId(roomId));
		model.addAttribute("rooms", roomService.findAll());
		model.addAttribute("selectedRoom", roomService.findById(roomId).getName());
		model.addAttribute("loggedInUser", personService.findByEmail(loggedInUser.getUsername()));
		return "userDevices/grid";
	}
	
	/**
	 * Display the devices that are assigned to a USER for a specific room
	 */
	@RequestMapping(value = "/device/user/{userId}/room/{roomId}", method = RequestMethod.GET)
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
		return "userDevices/grid";
	}
	
	/**
	 * Set the device on/off.
	 */
	// FIXME A simple user can update a device which is assigned to another user and not them!
	@RequestMapping(value = "/device/{id}/updateStatus/{status}", method = RequestMethod.POST)
	public String updateDeviceStatus(
			@PathVariable(value="id") int deviceId, 
			@PathVariable(value="status") boolean status) {
		
		Device device = deviceService.findById(deviceId);
		device.setStatusOn(status);
		deviceService.save(device);
		
		return redirectPage();
	}
	
	/**
	 * Increase/Decrease the device's information value. 
	 */
	// FIXME A simple user can update a device which is assigned to another user and not them!
	@RequestMapping(value = "/device/{id}/updateValue/{value}", method = RequestMethod.POST)
	public String updateDeviceInformationValue(
			@PathVariable(value="id") int deviceId, 
			@PathVariable(value="value") String informationValue) {
		
		Device device = deviceService.findById(deviceId);
		device.setInformationValue(informationValue);
		deviceService.save(device);
		
		return redirectPage();
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
	
	private String redirectPage() {
		UserDetails loggedInUser = personService.getLoggedInUser();
		if (loggedInUser != null && loggedInUser.getUsername() != null) {
			Person loggedInPerson = personService.findByEmail(loggedInUser.getUsername());
			if (loggedInPerson != null) {
				if ("ADMIN".equals(loggedInPerson.getRole())) {
					return "redirect:/admin/device/user/all";
				}
				else if ("USER".equals(loggedInPerson.getRole())) {
					return "redirect:/device/user/" + loggedInPerson.getId();
				}
			}
		}
		return null;
	}

}