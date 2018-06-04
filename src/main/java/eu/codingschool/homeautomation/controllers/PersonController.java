
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
	public String Index(Model model) {
		List<Person> listOfPersons = personService.findAll();
		model.addAttribute("listOfPersons", listOfPersons);
		return "person/list";
	}

	/**
	 * Display form for an already saved person with pre-filled fields.
	 */
	@RequestMapping(value = "/person/{id}/edit", method = RequestMethod.GET)
	public String EditPerson(@PathVariable("id") int id, Model model) {
		Person person = personService.findById(id);
		List<Device> listOfDevices = deviceService.findAll();
		List<Device> listOfPersonDevices = deviceService.findByPersonsId(id);
		model.addAttribute("listOfPersonDevices", listOfPersonDevices);
		model.addAttribute("listOfDevices", listOfDevices);
		model.addAttribute("actionUrl", "/person/" + id + "/edit");
		model.addAttribute("modalTitle", "Edit");
		model.addAttribute("person", person);
		return "person/modals :: modalNewOrEdit";
	}

	/**
	 * Update a person by submitting the form.
	 */
	@RequestMapping(value = "/person/{id}/edit", method = RequestMethod.POST)
	public String EditPerson(@ModelAttribute("person") Person person,
			@RequestParam(value = "listOfSelectedDeviceIds", required = false) List<String> listOfSelectedDeviceIds,
			BindingResult result, Model model) {

		// personValidator.validate(person, result);

		if (result.hasErrors()) {
			model.addAttribute("person", person);
			return "person/modals :: modalNewOrEdit";
		}

		if (listOfSelectedDeviceIds != null) {
			for (String deviceIdStr : listOfSelectedDeviceIds) {
				int deviceId = Integer.parseInt(deviceIdStr);
				Device device = deviceService.findById(deviceId);
				person.AddDevices(device);
			}
		}

		personService.save(person);

		model.addAttribute("listOfPersons", personService.findAll());
		return "redirect:/person/list";
	}

	/**
	 * Display a confirmation dialog before deleting a user.
	 */
	@RequestMapping(value = "/person/{id}/delete", method = RequestMethod.GET)
	public String DeletePerson(@PathVariable("id") int id, Model model) {

		Person person = personService.findById(id);
		model.addAttribute("person", person);
		model.addAttribute("actionUrl", "/person/" + id + "/delete");

		return "person/modals :: modalDelete";
	}

	/**
	 * Delete the person after accepting the deletion confirmation.
	 */
	@RequestMapping(value = "/person/{id}/delete", method = RequestMethod.POST)
	public String DeletePersonConfirm(@PathVariable("id") int id, Model model) {

		Person person = personService.findById(id);
		List<Device> PersonDevices = deviceService.findByPersonsId(id);
		if (PersonDevices.size() != 0) {
			person.RemoveDevices();
		}
		personService.delete(person);

		model.addAttribute("listOfPersons", personService.findAll());
		return "redirect:/person/list";
	}

}