package eu.codingschool.homeautomation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.codingschool.homeautomation.model.DeviceType;
import eu.codingschool.homeautomation.services.DeviceTypeService;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.validators.DeviceTypeValidator;

@Controller
@RequestMapping("/admin")
public class DeviceTypeController {
	
	@Autowired
	private DeviceTypeService deviceTypeService;
	
	@Autowired
	private PersonService personService;
	
	@Autowired
	private DeviceTypeValidator deviceTypeValidator;
	
	
	@RequestMapping(value = "/deviceType/list", method = RequestMethod.GET)
	public String getDeviceTypes(Model model) {
		String email = "";
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails instanceof UserDetails) {
            email = ((UserDetails)userDetails).getUsername();
        }
        
		model.addAttribute("deviceTypes", deviceTypeService.findAll());
		model.addAttribute("loggedInUser", personService.findByEmail(email));
		return "deviceType/list";
	}
	
	/**
	 * Display form for new device type with empty fields.
	 */
	@RequestMapping(value = "/deviceType/new", method = RequestMethod.GET)
	public String newDeviceType(Model model) {
		model.addAttribute("deviceType", new DeviceType());
		model.addAttribute("actionUrl", "/deviceType/new");
		model.addAttribute("modalTitle", "New");
		return "deviceType/modals :: modalNewOrEdit";
	}
	
	/**
	 * Save a new device type by submitting the form.
	 */
	@RequestMapping(value = "/deviceType/new", method = RequestMethod.POST)
	public String addDeviceType(@ModelAttribute("deviceType") DeviceType deviceType, BindingResult result, 
			ModelMap model) {
		return saveOrUpdateDeviceType(deviceType, result);
	}
	
	/**
	 * Display form for an already saved device type with pre-filled fields.
	 */
	@RequestMapping(value = "/deviceType/{id}/edit", method = RequestMethod.GET)
	public String viewDeviceType(@PathVariable(value="id") int id, Model model) {
		DeviceType deviceType = deviceTypeService.findById(id);
		model.addAttribute("deviceType", deviceType);
		model.addAttribute("actionUrl", "/deviceType/" + id + "/edit");
		model.addAttribute("modalTitle", "Edit");
		return "deviceType/modals :: modalNewOrEdit";
	}
	
	/**
	 * Update a device type by submitting the form.
	 */
	@RequestMapping(value = "/deviceType/{id}/edit", method = RequestMethod.POST)
	public String editDeviceType(@ModelAttribute("deviceType") DeviceType deviceType, BindingResult result, 
			ModelMap model) {
		return saveOrUpdateDeviceType(deviceType, result);
	}
	
	/**
	 * Display a confirmation dialog before deleting a device type.
	 */
	@RequestMapping(value = "/deviceType/{id}/delete", method = RequestMethod.GET)
	public String confrimDeleteDeviceType(@PathVariable(value="id") int id, Model model) {
		DeviceType deviceType = deviceTypeService.findById(id);
		model.addAttribute("deviceType", deviceType);
		model.addAttribute("actionUrl", "/deviceType/" + id + "/delete");
		return "deviceType/modals :: modalDelete";
	}
	
	/**
	 * Delete the device type after accepting the deletion confirmation.
	 */
	@RequestMapping(value = "/deviceType/{id}/delete", method = RequestMethod.POST)
	public String doDeleteDeviceType(@ModelAttribute("deviceType") DeviceType deviceType, BindingResult result, 
			ModelMap model) {
		deviceTypeService.delete(deviceType);
		return "redirect:/deviceType/list";
	}
	
	private String saveOrUpdateDeviceType(DeviceType deviceType, BindingResult result) {
		deviceTypeValidator.validate(deviceType, result);
		
		if (result.hasErrors()) {
			// reload the same page fragment
			return "deviceType/modals :: modalNewOrEdit";
		}
		
		deviceTypeService.save(deviceType);
		return "redirect:/deviceType/list";
	}
}
