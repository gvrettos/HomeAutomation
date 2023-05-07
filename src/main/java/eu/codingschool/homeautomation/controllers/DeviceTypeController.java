package eu.codingschool.homeautomation.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.codingschool.homeautomation.model.DeviceType;
import eu.codingschool.homeautomation.services.DeviceTypeService;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.validators.DeviceTypeValidator;

@Controller
@RequestMapping("/admin")
public class DeviceTypeController {

	private static final String ENDPOINT_DEVICE_TYPES_BASE_URL = "/deviceTypes";
	private static final String ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL = "/admin" + ENDPOINT_DEVICE_TYPES_BASE_URL;
	private static final String REDIRECT_ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL = "redirect:" + ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL;

	private static final String MODAL_DEVICE_TYPE_NEW_OR_EDIT = "deviceType/modals :: modalNewOrEdit";
	private static final String MODAL_DEVICE_TYPE_DELETE = "deviceType/modals :: modalDelete";

	private static final String VIEW_DEVICE_TYPE_LIST = "deviceType/list";

	private final DeviceTypeService deviceTypeService;

	private final PersonService personService;

	private final DeviceTypeValidator deviceTypeValidator;

	public DeviceTypeController(
			DeviceTypeService deviceTypeService,
			PersonService personService,
			DeviceTypeValidator deviceTypeValidator) {

		this.deviceTypeService = deviceTypeService;
		this.personService = personService;
		this.deviceTypeValidator = deviceTypeValidator;
	}


	@GetMapping(value = ENDPOINT_DEVICE_TYPES_BASE_URL)
	public String getDeviceTypes(Model model) {
		String email = "";
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails instanceof UserDetails) {
            email = ((UserDetails)userDetails).getUsername();
        }
        
		model.addAttribute("deviceTypes", deviceTypeService.findAll());
		model.addAttribute("loggedInUser", personService.findByEmail(email));
		return VIEW_DEVICE_TYPE_LIST;
	}
	
	/**
	 * Display form for new device type with empty fields.
	 */
	@PostMapping(value = ENDPOINT_DEVICE_TYPES_BASE_URL + "/form")
	public String newDeviceType(Model model) {
		model.addAttribute("deviceType", new DeviceType());
		model.addAttribute("actionUrl", ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL);
		model.addAttribute("actionType", "POST");
		model.addAttribute("modalTitle", "New");
		return MODAL_DEVICE_TYPE_NEW_OR_EDIT;
	}
	
	/**
	 * Save a new device type by submitting the form.
	 */
	@PostMapping(value = ENDPOINT_DEVICE_TYPES_BASE_URL)
	public String addDeviceType(@ModelAttribute("deviceType") DeviceType deviceType, BindingResult result) {
		return saveOrUpdateDeviceType(deviceType, result);
	}
	
	/**
	 * Display form for an already saved device type with pre-filled fields.
	 */
	@PutMapping(value = ENDPOINT_DEVICE_TYPES_BASE_URL + "/{id}/form")
	public String viewDeviceType(@PathVariable(value="id") int id, Model model) {
		DeviceType deviceType = deviceTypeService.findById(id);
		model.addAttribute("deviceType", deviceType);
		model.addAttribute("actionUrl", ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL + "/" + id);
		model.addAttribute("actionType", "PUT");
		model.addAttribute("modalTitle", "Edit");
		return MODAL_DEVICE_TYPE_NEW_OR_EDIT;
	}
	
	/**
	 * Update a device type by submitting the form.
	 */
	@PutMapping(value = ENDPOINT_DEVICE_TYPES_BASE_URL + "/{id}")
	public String editDeviceType(@ModelAttribute("deviceType") DeviceType deviceType, BindingResult result) {
		return saveOrUpdateDeviceType(deviceType, result);
	}
	
	/**
	 * Display a confirmation dialog before deleting a device type.
	 */
	@DeleteMapping(value = ENDPOINT_DEVICE_TYPES_BASE_URL + "/{id}/confirmation")
	public String confirmDeleteDeviceType(@PathVariable(value="id") int id, Model model) {
		DeviceType deviceType = deviceTypeService.findById(id);
		model.addAttribute("deviceType", deviceType);
		model.addAttribute("actionUrl", ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL + "/" + id);
		model.addAttribute("actionType", "DELETE");
		return MODAL_DEVICE_TYPE_DELETE;
	}
	
	/**
	 * Delete the device type after accepting the deletion confirmation.
	 */
	@DeleteMapping(value = ENDPOINT_DEVICE_TYPES_BASE_URL + "/{id}")
	public String doDeleteDeviceType(@ModelAttribute("deviceType") DeviceType deviceType, ModelMap model) {
		try {
			deviceType = deviceTypeService.findById(deviceType.getId());
			deviceTypeService.delete(deviceType);
		} catch (DataIntegrityViolationException ex) {
			// FIXME this will return statusCode = 200 OK which is not correct
			model.addAttribute("action", "delete device type");
			model.addAttribute("entityName", deviceType.getType());
			model.addAttribute("additionalMessage", "Please check if the device type is used by any device. Only unused device types can be deleted.");
			return "/error/422";
		}
		return REDIRECT_ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL;
	}
	
	private String saveOrUpdateDeviceType(DeviceType deviceType, BindingResult result) {
		deviceTypeValidator.validate(deviceType, result);
		
		if (result.hasErrors()) {
			// reload the same page fragment
			return MODAL_DEVICE_TYPE_NEW_OR_EDIT;
		}
		
		deviceTypeService.save(deviceType);
		return REDIRECT_ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL;
	}
}
