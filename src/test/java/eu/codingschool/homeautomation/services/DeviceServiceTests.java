package eu.codingschool.homeautomation.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eu.codingschool.homeautomation.app.HomeAutomationApplication;
import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.DeviceType;
import eu.codingschool.homeautomation.model.Person;
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
public class DeviceServiceTests {
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private DeviceTypeService deviceTypeService;
	
	@Autowired
	private RoomService roomService;
	

	private DeviceType getRandomDeviceType() {
		List<DeviceType> deviceTypes = deviceTypeService.findAll();
		if (!deviceTypes.isEmpty()) {
			return deviceTypes.get(0);
		}
		
		return null;
	}
	
	private Room getRandomRoom() {
		List<Room> room = roomService.findAll();
		if (!room.isEmpty()) {
			return room.get(0);
		}
		
		return null;
	}
	
	@Test
	public void testSaveDevice1() {
		Device device = new Device("Air Condition #1", "ON", "20", getRandomDeviceType(), getRandomRoom());
		Device deviceSaved = deviceService.save(device);
		assertNotNull("Device1 was not saved", deviceSaved);
	}
	
	@Test
	public void testSaveDevice2() {
		Device device = new Device("Lighting #1", "ON", "50", getRandomDeviceType(), getRandomRoom());
		Device deviceSaved = deviceService.save(device);
		assertNotNull("Device2 was not saved", deviceSaved);
	}
	
	@Test
	public void testFindAll() {
		List<Device> devices = deviceService.findAll();
		assertNotNull("No devices exist", devices);
		assertFalse("No devices exist", devices.isEmpty());
	}
	
	/** 
	 * Get a random device and try finding them by id 
	 */
	@Test
	public void testFindById() {
		List<Device> devices = deviceService.findAll();
		if (!devices.isEmpty()) {
			Integer id = devices.get(0).getId();
			Device device = deviceService.findById(id);
			assertNotNull("Requested device does not exist", device);
		}
	}
	
	/** 
	 * Get a random device and try finding them by name 
	 */
	@Test
	public void testFindByName() {
		List<Device> devices = deviceService.findAll();
		if (!devices.isEmpty()) {
			String name = devices.get(0).getName();
			List<Device> devicesFoundByCriteria = deviceService.findByName(name);
			assertNotNull("Requested device does not exist", devicesFoundByCriteria);
			assertFalse("Requested device does not exist", devicesFoundByCriteria.isEmpty());
		}
	}
	
	/** 
	 * Get a random device and try finding them by surname
	 */
	@Test
	public void testFindByStatus() {
		List<Device> devices = deviceService.findAll();
		if (!devices.isEmpty()) {
			String status = devices.get(0).getStatus();
			List<Device> devicesFoundByCriteria = deviceService.findByStatus(status);
			assertNotNull("Requested device does not exist", devicesFoundByCriteria);
			assertFalse("Requested device does not exist", devicesFoundByCriteria.isEmpty());
		}
	}
	
	@Test
	public void testFindUsersAssigned() {
		// TODO This is a dummy test. Make it be behave with parameterized values.
		Set<Person> persons = deviceService.findUsersAssigned(1);
		System.err.println("persons: " + persons != null ? persons.size() : "-1");
	}
	
	/*
	 * FIXME
	 * It throws java.util.NoSuchElementException: No value present while calling findById().
	 * This is correct if the deletion succeeds. Handle that more gracefully.
	 */
	@Test
	public void testDeleteById() {
		List<Device> devices = deviceService.findAll();
		if (!devices.isEmpty()) {
			Integer id = devices.get(0).getId();
			deviceService.delete(id);
			Device device = deviceService.findById(id);
			assertNull("Requested device was not deleted", device);
		}
	}
	
	/*
	 * FIXME
	 * It throws java.util.NoSuchElementException: No value present while calling findById().
	 * This is correct if the deletion succeeds. Handle that more gracefully.
	 */
	@Test
	public void testDeleteByDevice() {
		List<Device> devices = deviceService.findAll();
		if (!devices.isEmpty()) {
			Device deviceToBeDeleted = devices.get(0);
			deviceService.delete(deviceToBeDeleted);
			Device device = deviceService.findById(deviceToBeDeleted.getId());
			assertNull("Requested device was not deleted", device);
		}
	}
}