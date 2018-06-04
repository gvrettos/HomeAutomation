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

import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.services.RoomService;
import eu.codingschool.homeautomation.validators.RoomValidator;

@Controller
public class RoomController {

	@Autowired
	private RoomService roomService;

	@Autowired
	private RoomValidator roomValidator;

	@RequestMapping(value = "/room/list", method = RequestMethod.GET)
	public String getRoomlist(Model model) {
		List<Room> room = roomService.findAll();
		model.addAttribute("room", room);
		return "room/list";
	}

	/**
	 * Display form for new room with empty fields.
	 */
	@RequestMapping(value = "/room/new", method = RequestMethod.GET)
	public String newRoom(Model model) {
		model.addAttribute("room", new Room());
		model.addAttribute("actionUrl", "/room/new");
		model.addAttribute("modalTitle", "New");
		return "room/modals :: modalNewOrEdit";
	}

	/**
	 * Save a new room by submitting the form.
	 */
	@RequestMapping(value = "/room/new", method = RequestMethod.POST)
	public String addRoom(@ModelAttribute("room") Room room, BindingResult result, ModelMap model) {
		return saveOrUpdateRoom(room, result);
	}

	/**
	 * Display form for an already saved room with pre-filled fields.
	 */
	@RequestMapping(value = "/room/{id}/edit", method = RequestMethod.GET)
	public String viewRoom(@PathVariable(value = "id") int id, Model model) {
		Room room = roomService.findById(id);
		model.addAttribute("room", room);
		model.addAttribute("actionUrl", "/room/" + id + "/edit");
		model.addAttribute("modalTitle", "Edit");
		return "room/modals :: modalNewOrEdit";
	}
	
	/**
	 * Update a room by submitting the form.
	 */
	@RequestMapping(value = "/room/{id}/edit", method = RequestMethod.POST)
	public String editRoom(@ModelAttribute("room") Room room, BindingResult result, ModelMap model) {
		return saveOrUpdateRoom(room, result);
	}

	/**
	 * Display a confirmation dialog before deleting a room.
	 */
	@RequestMapping(value = "/room/{id}/delete", method = RequestMethod.GET)
	public String confrimDeleteRoom(@PathVariable(value = "id") int id, Model model) {
		Room room = roomService.findById(id);
		model.addAttribute("room", room);
		model.addAttribute("actionUrl", "/room/" + id + "/delete");
		return "room/modals :: modalDelete";
	}

	/**
	 * Delete the room after accepting the deletion confirmation.
	 */
	@RequestMapping(value = "/room/{id}/delete", method = RequestMethod.POST)
	public String doDeleteRoom(@ModelAttribute("room") Room room, BindingResult result, ModelMap model) {
		roomService.delete(room);
		return "redirect:/room/list";
	}

	private String saveOrUpdateRoom(Room room, BindingResult result) {
		roomValidator.validate(room, result);

		if (result.hasErrors()) {
			// reload the same page fragment
		}

		roomService.save(room);
		return "redirect:/room/list";
	}
}
