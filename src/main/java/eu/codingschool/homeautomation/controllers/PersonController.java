package eu.codingschool.homeautomation.controllers;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.services.DeviceService;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.services.RoomService;
import eu.codingschool.homeautomation.validators.PersonValidator;

@Controller
@RequestMapping("/admin")
public class PersonController {

	private static final String ENDPOINT_PERSONS_BASE_URL = "/people";
	private static final String ENDPOINT_ADMIN_PERSONS_BASE_URL = "/admin" + ENDPOINT_PERSONS_BASE_URL;
	private static final String REDIRECT_ENDPOINT_ADMIN_PERSONS_BASE_URL = "redirect:" + ENDPOINT_ADMIN_PERSONS_BASE_URL;

	private static final String MODAL_PERSON_NEW_OR_EDIT = "person/modals :: modalNewOrEdit";
	private static final String MODAL_PERSON_DELETE = "person/modals :: modalDelete";

	private static final String VIEW_PERSON_LIST = "person/list";
	private static final String VIEW_ERROR_404 = "/error/404";

	private final PersonService personService;

	private final DeviceService deviceService;

	private final RoomService roomService;

	private final PersonValidator personValidator;

	public PersonController(
			PersonService personService,
			DeviceService deviceService,
			RoomService roomService,
			PersonValidator personValidator) {

		this.personService = personService;
		this.deviceService = deviceService;
		this.roomService = roomService;
		this.personValidator = personValidator;
	}

	@GetMapping(value = ENDPOINT_PERSONS_BASE_URL)
	public String getPeople(Model model) {
		
		UserDetails loggedInUser = personService.getLoggedInUser();
		if ( loggedInUser == null || loggedInUser.getUsername() == null ) {
			throw new AccessDeniedException("");
		}
		
		model.addAttribute("people", personService.findAll());
		model.addAttribute("rooms", roomService.findAll());
		model.addAttribute("loggedInUser", personService.findByEmail(loggedInUser.getUsername()));
		return VIEW_PERSON_LIST;
	}

	/**
	 * Display form for an already saved person with pre-filled fields.
	 */
	@PutMapping(value = ENDPOINT_PERSONS_BASE_URL + "/{id}/form")
	public String viewPerson(@PathVariable("id") int id, Model model) {
		model.addAttribute("personDevices", deviceService.findByPersonsId(id));
		model.addAttribute("devices", deviceService.findAll());
		model.addAttribute("actionUrl", ENDPOINT_ADMIN_PERSONS_BASE_URL + "/" + id);
		model.addAttribute("actionType", "PUT");
		model.addAttribute("modalTitle", "Edit");
		model.addAttribute("person", personService.findById(id));
		return MODAL_PERSON_NEW_OR_EDIT;
	}

	/**
	 * Update a person by submitting the form.
	 */
	@PutMapping(value = ENDPOINT_PERSONS_BASE_URL + "/{id}")
	public String editPerson(
			@ModelAttribute("person") Person person,
			@RequestParam(value = "selectedDeviceIds", required = false) List<String> selectedDeviceIds,
			BindingResult result,
			Model model) {
		
		UserDetails loggedInUser = personService.getLoggedInUser();
		if ( loggedInUser == null || loggedInUser.getUsername() == null ) {
			throw new AccessDeniedException("");
		}
		
		personValidator.validate(person, result);

		if (result.hasErrors()) {
			// reload the same page fragment
			model.addAttribute("person", person);
			return MODAL_PERSON_NEW_OR_EDIT;
		}		
		
		personService.update(person, deviceService.getSelectedDevices(selectedDeviceIds));
		
		model.addAttribute("rooms", roomService.findAll());
		model.addAttribute("loggedInUser", personService.findByEmail(loggedInUser.getUsername()));
		return REDIRECT_ENDPOINT_ADMIN_PERSONS_BASE_URL;
	}

	/**
	 * Display a confirmation dialog before deleting a user.
	 */
	@DeleteMapping(value = ENDPOINT_PERSONS_BASE_URL + "/{id}/confirmation")
	public String confirmDeletePerson(@PathVariable("id") int id, Model model) {
		Person person = personService.findById(id);
		model.addAttribute("person", person);
		model.addAttribute("actionUrl", ENDPOINT_ADMIN_PERSONS_BASE_URL + "/" + id);
		model.addAttribute("actionType", "DELETE");
		return MODAL_PERSON_DELETE;
	}

	/**
	 * Delete the person after accepting the deletion confirmation.
	 */
	@DeleteMapping(value = ENDPOINT_PERSONS_BASE_URL + "/{id}")
	public String doDeletePerson(@PathVariable("id") int id) {
		Person person = personService.findById(id);
		if (person == null) {
			return "redirect:" + VIEW_ERROR_404;
		}
		person.removeAllDevices();
		personService.delete(person);
		return REDIRECT_ENDPOINT_ADMIN_PERSONS_BASE_URL;
	}

}