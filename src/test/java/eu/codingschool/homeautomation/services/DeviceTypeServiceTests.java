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
import eu.codingschool.homeautomation.model.DeviceType;

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
public class DeviceTypeServiceTests {
	
	@Autowired
	private DeviceTypeService deviceTypeService;

	@Test
	public void testSaveDeviceType1() {
		DeviceType deviceTypeSaved = deviceTypeService.save(new DeviceType("air_condition", "temperature"));
		assertNotNull("DeviceType1 was not saved", deviceTypeSaved);
	}
	
	@Test
	public void testSaveDeviceType2() {
		DeviceType deviceTypeSaved = deviceTypeService.save(new DeviceType("laundry_machine", "laundry_state"));
		assertNotNull("DeviceType2 was not saved", deviceTypeSaved);
	}
	
	@Test
	public void testSaveDeviceType3() {
		DeviceType deviceTypeSaved = deviceTypeService.save(new DeviceType("lighting", "illumination_percentage"));
		assertNotNull("DeviceType3 was not saved", deviceTypeSaved);
	}
	
	@Test
	public void testFindAll() {
		List<DeviceType> deviceTypes = deviceTypeService.findAll();
		assertNotNull("No device types exist", deviceTypes);
		assertFalse("No device types exist", deviceTypes.isEmpty());
	}
	
	/** 
	 * Get a random device type
	 */
	@Test
	public void testFindById() {
		List<DeviceType> deviceTypes = deviceTypeService.findAll();
		if (!deviceTypes.isEmpty()) {
			Integer id = deviceTypes.get(0).getId();
			DeviceType deviceType = deviceTypeService.findById(id);
			assertNotNull("Requested device type does not exist", deviceType);
		}
	}
	
	/*
	 * FIXME
	 * It throws java.util.NoSuchElementException: No value present while calling findById().
	 * This is correct if the deletion succeeds. Handle that more gracefully.
	 */
	@Test
	public void testDeleteById() {
		List<DeviceType> deviceTypes = deviceTypeService.findAll();
		if (!deviceTypes.isEmpty()) {
			Integer id = deviceTypes.get(0).getId();
			deviceTypeService.delete(id);
			DeviceType deviceType = deviceTypeService.findById(id);
			assertNull("Requested device type was not deleted", deviceType);
		}
	}
	
	/*
	 * FIXME
	 * It throws java.util.NoSuchElementException: No value present while calling findById().
	 * This is correct if the deletion succeeds. Handle that more gracefully.
	 */
	@Test
	public void testDeleteByDevice() {
		List<DeviceType> devices = deviceTypeService.findAll();
		if (!devices.isEmpty()) {
			DeviceType deviceTypeToBeDeleted = devices.get(0);
			deviceTypeService.delete(deviceTypeToBeDeleted);
			DeviceType deviceType = deviceTypeService.findById(deviceTypeToBeDeleted.getId());
			assertNull("Requested device type was not deleted", deviceType);
		}
	}
}