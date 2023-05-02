package eu.codingschool.homeautomation.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.services.RoomService;
import eu.codingschool.homeautomation.validators.RoomValidator;

@Controller
@RequestMapping("/admin")
public class RoomController {

	private static final String ENDPOINT_ROOMS_BASE_URL = "/rooms";
	private static final String ENDPOINT_ADMIN_ROOMS_BASE_URL = "/admin" + ENDPOINT_ROOMS_BASE_URL;
	private static final String REDIRECT_ENDPOINT_ADMIN_ROOMS_BASE_URL = "redirect:" + ENDPOINT_ADMIN_ROOMS_BASE_URL;

	private static final String MODAL_ROOM_NEW_OR_EDIT = "room/modals :: modalNewOrEdit";
	private static final String MODAL_ROOM_DELETE = "room/modals :: modalDelete";

	private static final String VIEW_ROOM_LIST = "room/list";

	private final RoomService roomService;

	private final PersonService personService;

	private final RoomValidator roomValidator;

    public RoomController(RoomService roomService, PersonService personService, RoomValidator roomValidator) {
        this.roomService = roomService;
        this.personService = personService;
        this.roomValidator = roomValidator;
    }

	@GetMapping(value = ENDPOINT_ROOMS_BASE_URL)
	public String getRooms(Model model) {
		String email = "";
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails instanceof UserDetails) {
            email = ((UserDetails)userDetails).getUsername();
        }
        
        model.addAttribute("rooms", roomService.findAll());
    	model.addAttribute("loggedInUser", personService.findByEmail(email));
		return VIEW_ROOM_LIST;
	}

	/**
	 * Display form for new room with empty fields.
	 */
	@PostMapping(value = ENDPOINT_ROOMS_BASE_URL + "/form")
	public String newRoom(Model model) {
		model.addAttribute("room", new Room());
		model.addAttribute("actionUrl", ENDPOINT_ADMIN_ROOMS_BASE_URL);
		model.addAttribute("actionType", "POST");
		model.addAttribute("modalTitle", "New");
		return MODAL_ROOM_NEW_OR_EDIT;
	}

	/**
	 * Save a new room by submitting the form.
	 */
	@PostMapping(value = ENDPOINT_ROOMS_BASE_URL)
	public String addRoom(@ModelAttribute("room") Room room, BindingResult result) {
		return saveOrUpdateRoom(room, result);
	}

	/**
	 * Display form for an already saved room with pre-filled fields.
	 */
	@PutMapping(value = ENDPOINT_ROOMS_BASE_URL + "/{id}/form")
	public String viewRoom(@PathVariable(value = "id") int id, Model model) {
		Room room = roomService.findById(id);
		model.addAttribute("room", room);
		model.addAttribute("actionUrl", ENDPOINT_ADMIN_ROOMS_BASE_URL + "/" + id);
		model.addAttribute("actionType", "PUT");
		model.addAttribute("modalTitle", "Edit");
		return MODAL_ROOM_NEW_OR_EDIT;
	}
	
	/**
	 * Update a room by submitting the form.
	 */
	@PutMapping(value = ENDPOINT_ROOMS_BASE_URL + "/{id}")
	public String editRoom(@ModelAttribute("room") Room room, BindingResult result) {
		return saveOrUpdateRoom(room, result);
	}

	/**
	 * Display a confirmation dialog before deleting a room.
	 */
	@DeleteMapping(value = ENDPOINT_ROOMS_BASE_URL + "/{id}/confirmation")
	public String confirmDeleteRoom(@PathVariable(value = "id") int id, Model model) {
		Room room = roomService.findById(id);
		model.addAttribute("room", room);
		model.addAttribute("actionUrl", ENDPOINT_ADMIN_ROOMS_BASE_URL + "/" + id);
		model.addAttribute("actionType", "DELETE");
		return MODAL_ROOM_DELETE;
	}

	/**
	 * Delete the room after accepting the deletion confirmation.
	 */
	@DeleteMapping(value = ENDPOINT_ROOMS_BASE_URL + "/{id}")
	public String doDeleteRoom(@ModelAttribute("room") Room room, ModelMap model) {
		try {
			room = roomService.findById(room.getId());
			roomService.delete(room);
		} catch (DataIntegrityViolationException ex) {
			// FIXME this will return statusCode = 200 OK which is not correct
			model.addAttribute("action", "delete room");
			model.addAttribute("entityName", room.getName());
			model.addAttribute("additionalMessage", "Please check if there are any assigned devices to this room. Only free rooms can be deleted.");
			return "/error/422";
		}
		return REDIRECT_ENDPOINT_ADMIN_ROOMS_BASE_URL;
	}

	private String saveOrUpdateRoom(Room room, BindingResult result) {
		roomValidator.validate(room, result);

		if (result.hasErrors()) {
			// reload the same page fragment
			return MODAL_ROOM_NEW_OR_EDIT;
		}

		roomService.save(room);
		return REDIRECT_ENDPOINT_ADMIN_ROOMS_BASE_URL;
	}
}
