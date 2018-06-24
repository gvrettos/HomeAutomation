
package eu.codingschool.homeautomation.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.services.DeviceService;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.validators.PersonValidator;

@Controller
public class PersonController {

	@Autowired
	public PersonService personService;
	@Autowired
	public DeviceService deviceService;
	@Autowired
	public PersonValidator personValidator;

	@RequestMapping(value = "/person/list", method = RequestMethod.GET)
	public String getPeople(Model model) {
		List<Person> people = personService.findAll();
		model.addAttribute("people", people);
		return "person/list";
	}

	/**
	 * Display form for an already saved person with pre-filled fields.
	 */
	@RequestMapping(value = "/person/{id}/edit", method = RequestMethod.GET)
	public String viewPerson(@PathVariable("id") int id, Model model) {
		Person person = personService.findById(id);
		List<Device> devices = deviceService.findAll();
		List<Device> personDevices = deviceService.findByPersonsId(id);
		model.addAttribute("personDevices", personDevices);
		model.addAttribute("devices", devices);
		model.addAttribute("actionUrl", "/person/" + id + "/edit");
		model.addAttribute("modalTitle", "Edit");
		model.addAttribute("person", person);
		return "person/modals :: modalNewOrEdit";
	}

	/**
	 * Update a person by submitting the form.
	 */
	@RequestMapping(value = "/person/{id}/edit", method = RequestMethod.POST)
	public String editPerson(
			@ModelAttribute("person") Person person,
			@RequestParam(value = "selectedDeviceIds", required = false) List<String> selectedDeviceIds,
			BindingResult result, Model model) {
		personValidator.validate(person, result);

		if (result.hasErrors()) {
			// reload the same page fragment
			model.addAttribute("person", person);
			return "person/modals :: modalNewOrEdit";
		}

		// add user-assigned devices
		if (selectedDeviceIds != null) {
			for (String deviceIdStr : selectedDeviceIds) {
				int deviceId = Integer.parseInt(deviceIdStr);
				Device device = deviceService.findById(deviceId);
				person.addDevice(device);
			}
		}

		personService.save(person);
		return "redirect:/person/list";
	}

	/**
	 * Display a confirmation dialog before deleting a user.
	 */
	@RequestMapping(value = "/person/{id}/delete", method = RequestMethod.GET)
	public String confirmDeletePerson(@PathVariable("id") int id, Model model) {
		Person person = personService.findById(id);
		model.addAttribute("person", person);
		model.addAttribute("actionUrl", "/person/" + id + "/delete");
		return "person/modals :: modalDelete";
	}

	/**
	 * Delete the person after accepting the deletion confirmation.
	 */
	@RequestMapping(value = "/person/{id}/delete", method = RequestMethod.POST)
	public String doDeletePerson(@PathVariable("id") int id, Model model) {
		Person person = personService.findById(id);
		person.removeAllDevices();
		personService.delete(person);
		return "redirect:/person/list";
	}

}