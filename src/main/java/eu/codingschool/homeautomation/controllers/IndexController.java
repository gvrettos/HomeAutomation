package eu.codingschool.homeautomation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.validators.PersonValidator;

@Controller
public class IndexController {

	@Autowired
	private PersonValidator personValidator;

	@Autowired
	private PersonService personService;

	@RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
	public String home(ModelMap model) {
		Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetails instanceof UserDetails) {
			String email = ((UserDetails) userDetails).getUsername();
			model.addAttribute("loggedInUser", personService.findByEmail(email));
			return "index";
		}
		
		return "login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model, String error, String logout) {
		if (error != null)
			model.addAttribute("error", "Your username and password is invalid.");

		if (logout != null) {
			model.addAttribute("message", "You have been logged out successfully.");
		}
		
		return "login";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String registration(Model model) {
		model.addAttribute("user", new Person());
		return "registration";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String registration(@ModelAttribute("user") Person person, BindingResult bindingResult, Model model) {
		personValidator.validate(person, bindingResult);

		if (bindingResult.hasErrors()) {
			return "registration";
		}
		personService.save(person);
		return "redirect:/index";
	}
	
	@RequestMapping(value = "/error/403", method = RequestMethod.GET)
	public String error403(Model model) {
		return "error/403";
	}

}
