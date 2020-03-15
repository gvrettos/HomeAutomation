package eu.codingschool.homeautomation.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eu.codingschool.homeautomation.app.HomeAutomationApplication;
import eu.codingschool.homeautomation.model.Room;

/**
 * FIXME
 * 
 * Execution order of tests is not guaranteed. So, results from a test must NOT rely on the results of another one.
 * For now the tests are quite dummy and do not fulfill this condition. 
 * Each test should be totally independent from the rest.
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HomeAutomationApplication.class)
public class RoomServiceTests {
	
	@Autowired
	private RoomService roomService;

	@Test
	public void testSaveRoom1() {
		Room roomSaved = roomService.save(new Room("Bedroom"));
		assertNotNull("Room1 was not saved", roomSaved);
	}
	
	@Test
	public void testSaveRoom2() {
		Room roomSaved = roomService.save(new Room("Dining Room"));
		assertNotNull("Room2 was not saved", roomSaved);
	}
	
	@Test
	public void testSaveRoom3() {
		Room roomSaved = roomService.save(new Room("Kitchen"));
		assertNotNull("Room3 was not saved", roomSaved);
	}
	
	@Test
	public void testFindAll() {
		List<Room> rooms = roomService.findAll();
		assertNotNull("No room exist", rooms);
		assertFalse("No room exist", rooms.isEmpty());
	}
	
	/** 
	 * Get a random device type
	 */
	@Test
	public void testFindById() {
		List<Room> rooms = roomService.findAll();
		if (!rooms.isEmpty()) {
			Integer id = rooms.get(0).getId();
			Room room = roomService.findById(id);
			assertNotNull("Requested room does not exist", room);
		}
	}
	
	/*
	 * FIXME
	 * It throws java.util.NoSuchElementException: No value present while calling findById().
	 * This is correct if the deletion succeeds. Handle that more gracefully.
	 */
	@Test
	public void testDeleteById() {
		List<Room> rooms = roomService.findAll();
		if (!rooms.isEmpty()) {
			Integer id = rooms.get(0).getId();
			roomService.delete(id);
			Room room = roomService.findById(id);
			assertNull("Requested room was not deleted", room);
		}
	}
	
	/*
	 * FIXME
	 * It throws java.util.NoSuchElementException: No value present while calling findById().
	 * This is correct if the deletion succeeds. Handle that more gracefully.
	 */
	@Test
	public void testDeleteByDevice() {
		List<Room> rooms = roomService.findAll();
		if (!rooms.isEmpty()) {
			Room roomToBeDeleted = rooms.get(0);
			roomService.delete(roomToBeDeleted);
			Room room = roomService.findById(roomToBeDeleted.getId());
			assertNull("Requested room was not deleted", room);
		}
	}
}