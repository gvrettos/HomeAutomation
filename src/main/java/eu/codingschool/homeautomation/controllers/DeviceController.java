package eu.codingschool.homeautomation.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.services.DeviceService;
import eu.codingschool.homeautomation.services.DeviceTypeService;
import eu.codingschool.homeautomation.services.RoomService;
import eu.codingschool.homeautomation.validators.DeviceValidator;

@Controller
public class DeviceController {
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private DeviceTypeService deviceTypeService;
	
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private DeviceValidator deviceValidator;
	
	
	@RequestMapping(value = "/device/list", method = RequestMethod.GET)
	public String getDevices(Model model) {
		List<Device> devices = deviceService.findAll();
		model.addAttribute("devices", devices);
		return "device/list";
	}
	
	/**
	 * Display form for new device type with empty fields.
	 */
	@RequestMapping(value = "/device/new", method = RequestMethod.GET)
	public String newDevice(Model model) {
		model.addAttribute("device", new Device());
		List<DeviceType> allDeviceTypes = deviceTypeService.findAll();
		List<Room> allRooms = roomService.findAll();
		model.addAttribute("allDeviceTypes", allDeviceTypes);
		model.addAttribute("allRooms", allRooms);
		model.addAttribute("actionUrl", "/device/new");
		model.addAttribute("modalTitle", "New");
		return "device/modals :: modalNewOrEdit";
	}
	
	/**
	 * Save a new device type by submitting the form.
	 */
	@RequestMapping(value = "/device/new", method = RequestMethod.POST)
	public String addDevice(@ModelAttribute("device") Device device, BindingResult result, 
			ModelMap model) {
		return saveOrUpdateDevice(device, result);
	}
	
	/**
	 * Display form for an already saved device type with pre-filled fields.
	 */
	@RequestMapping(value = "/device/{id}/edit", method = RequestMethod.GET)
	public String viewDevice(@PathVariable(value="id") int id, Model model) {
		Device device = deviceService.findById(id);
		List<DeviceType> allDeviceTypes = deviceTypeService.findAll();
		List<Room> allRooms = roomService.findAll();
		model.addAttribute("allDeviceTypes", allDeviceTypes);
		model.addAttribute("allRooms", allRooms);
		model.addAttribute("device", device);
		model.addAttribute("actionUrl", "/device/" + id + "/edit");
		model.addAttribute("modalTitle", "Edit");
		return "device/modals :: modalNewOrEdit";
	}
	
	/**
	 * Update a device type by submitting the form.
	 */
	@RequestMapping(value = "/device/{id}/edit", method = RequestMethod.POST)
	public String editDevice(@ModelAttribute("device") Device device, BindingResult result, 
			ModelMap model) {
		return saveOrUpdateDevice(device, result);
	}
	
	/**
	 * Display a confirmation dialog before deleting a device type.
	 */
	@RequestMapping(value = "/device/{id}/delete", method = RequestMethod.GET)
	public String confrimDeleteDevice(@PathVariable(value="id") int id, Model model) {
		Device device = deviceService.findById(id);
		model.addAttribute("device", device);
		model.addAttribute("actionUrl", "/device/" + id + "/delete");
		return "device/modals :: modalDelete";
	}
	
	/**
	 * Delete the device type after accepting the deletion confirmation.
	 */
	@RequestMapping(value = "/device/{id}/delete", method = RequestMethod.POST)
	public String doDeleteDevice(@ModelAttribute("device") Device device, BindingResult result, 
			ModelMap model) {
		deviceService.delete(device);
		return "redirect:/device/list";
	}
	
	private String saveOrUpdateDevice(Device device, BindingResult result) {
		deviceValidator.validate(device, result);
		
		if (result.hasErrors()) {
			// reload the same page fragment
			return "device/modals :: modalNewOrEdit";
		}
		
		deviceService.save(device);
		return "redirect:/device/list";
	}
	
//	@RequestMapping(value = "/device/{id}/updateStatus/{status}", method = RequestMethod.POST)
//	public String updateDeviceStatus(@ModelAttribute("device") Device device, 
//			@PathVariable(value="status") String status, BindingResult result, ModelMap model) {
//		device.setStatus(status);
//		deviceService.save(device);
//		return "redirect:/device/list";
//	}
//	
//	@RequestMapping(value = "/device/{id}/updateValue/{value}", method = RequestMethod.POST)
//	public String updateDeviceInformationValue(@ModelAttribute("device") Device device, 
//			@PathVariable(value="value") String informationValue, BindingResult result, ModelMap model) {
//		device.setInformationValue(informationValue);
//		deviceService.save(device);
//		return "redirect:/device/list";
//	}
}
