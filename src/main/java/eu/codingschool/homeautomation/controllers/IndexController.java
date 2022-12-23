package eu.codingschool.homeautomation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.services.RoomService;
import eu.codingschool.homeautomation.validators.PersonValidator;

@Controller
public class IndexController {

	@Autowired
	private PersonValidator personValidator;

	@Autowired
	private PersonService personService;
	
	@Autowired
	private RoomService roomService;

	private static final String ENDPOINT_ROOT = "/";
	private static final String VIEW_INDEX = "index";
	private static final String ENDPOINT_INDEX = ENDPOINT_ROOT + VIEW_INDEX;
	private static final String VIEW_LOGIN = "login";
	private static final String ENDPOINT_LOGIN = ENDPOINT_ROOT + VIEW_LOGIN;
	private static final String VIEW_REGISTRATION = "registration";
	private static final String ENDPOINT_REGISTRATION = ENDPOINT_ROOT + VIEW_REGISTRATION;

	@GetMapping(value = { ENDPOINT_ROOT, ENDPOINT_INDEX })
	public String home(ModelMap model) {
		Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetails instanceof UserDetails) {
			String email = ((UserDetails) userDetails).getUsername();
			Person loggedInUser = personService.findByEmail(email);
			model.addAttribute("loggedInUser", loggedInUser);
			if (loggedInUser != null) {
				if (loggedInUser.isAdmin()) {
					model.addAttribute("rooms", roomService.findAll());
				}
				else {
					model.addAttribute("rooms", roomService.findByUser(loggedInUser.getId()));
				}
			}
			return VIEW_INDEX;
		}
		
		return VIEW_LOGIN;
	}

	@GetMapping(value = ENDPOINT_LOGIN)
	public String login(Model model, String error, String logout) {
		if (error != null)
			model.addAttribute("error", "Your username and password is invalid.");

		if (logout != null) {
			model.addAttribute("message", "You have been logged out successfully.");
		}
		
		return VIEW_LOGIN;
	}

	@GetMapping(value = ENDPOINT_REGISTRATION)
	public String registration(Model model) {
		model.addAttribute("user", new Person());
		return VIEW_REGISTRATION;
	}

	@PostMapping(value = ENDPOINT_REGISTRATION)
	public String registration(@ModelAttribute("user") Person person, BindingResult bindingResult, Model model) {
		personValidator.validate(person, bindingResult);

		if (bindingResult.hasErrors()) {
			return VIEW_REGISTRATION;
		}
		personService.save(person);
		return "redirect:" + ENDPOINT_INDEX;
	}
	
	@GetMapping(value = "/error/403")
	public String error403(Model model) {
		return "error/403";
	}
	
	@GetMapping(value = "/error/422")
	public String error422(Model model) {
		return "error/422";
	}

}
