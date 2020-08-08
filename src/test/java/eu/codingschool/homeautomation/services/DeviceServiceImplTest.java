package eu.codingschool.homeautomation.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.repositories.DeviceRepository;

@RunWith(SpringRunner.class)
public class DeviceServiceImplTest {

	@TestConfiguration
	static class DeviceServiceImplTestContextConfiguration {
		@Bean
		public DeviceService deviceService() {
			return new DeviceServiceImpl();
		}
	}

	@Autowired
	private DeviceService deviceService;

	@MockBean
	private DeviceRepository deviceRepository;
	
	@MockBean
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private Device device1;
	private Device device2;
	private Device device3;
	
	private Room room1;
	private Room room2;
	
	private List<Device> room1Devices;
	private List<Device> room2Devices;
	
	private Person person1;
	private Person person2;
	
	private List<Device> person1Devices;
	private List<Device> person2Devices;

	@Before
	public void setUp() {
		device1 = new Device();
		device1.setId(1);
		
		device2 = new Device();
		device2.setId(2);
		
		device3 = new Device();
		device3.setId(3);
		
		room1 = new Room();
		room1.setId(1);
		
		room2 = new Room();
		room2.setId(2);
		
		room1Devices = Arrays.asList(device1, device2);
		room2Devices = Arrays.asList();
		
		person1 = new Person();
		person1.setId(1);
		person2 = new Person();
		person2.setId(2);
		
		person1Devices = Arrays.asList();
		person2Devices = Arrays.asList(device1, device2, device3);
		
		Mockito.when(deviceRepository.findById(device1.getId())).thenReturn(Optional.of(device1));
		Mockito.when(deviceRepository.findById(device2.getId())).thenReturn(Optional.of(device2));
		Mockito.when(deviceRepository.findById(device3.getId())).thenReturn(Optional.of(device3));
	}
	
	@Test
	public void findAll_Should_ReturnDevices_When_DevicesExist() {
		// given
		List<Device> devicesSaved = Arrays.asList(device1, device2);
		Mockito.when(deviceRepository.findAll()).thenReturn(devicesSaved);
		
		// when
		List<Device> devicesFound = deviceService.findAll();
		
		// then
		assertNotNull(devicesFound);
		assertThat(devicesFound.size()).isEqualTo(devicesSaved.size());
	}
	
	@Test
	public void findAll_Should_NotReturnDevices_When_DevicesNotExist() {
		// given
		Mockito.when(deviceRepository.findAll()).thenReturn(Arrays.asList());
		
		// when
		List<Device> devices = deviceService.findAll();
		
		// then
		assertNotNull(devices);
		assertThat(devices).isEmpty();
	}
	
	@Test
	public void findById_Should_ReturnDevice_When_DeviceWithIdExists() {
		// when
		Device device = deviceService.findById(device1.getId());
		
		// then
		assertNotNull(device);
		assertThat(device.getId()).isEqualTo(device1.getId());
	}
	
	@Test
	public void findById_Should_NotReturnDevice_When_DeviceWithIdNotExists() {
		// when
		Device device = deviceService.findById(4);
		
		// then
		assertNull(device);
	}
	
	@Test
	public void findAllByRoomId_Should_ReturnDevicesForRoom_When_DevicesForRoomExist() {
		// given
		Mockito.when(deviceRepository.findByRoomId(room1.getId())).thenReturn(room1Devices);
		
		// when
		List<Device> devicesFound = deviceService.findAllByRoomId(room1.getId());
		
		// then
		assertNotNull(devicesFound);
		assertThat(devicesFound.size()).isEqualTo(room1Devices.size());
	}
	
	@Test
	public void findAllByRoomId_Should_NotReturnDevicesForRoom_When_DevicesForRoomNotExist() {
		// given
		Mockito.when(deviceRepository.findByRoomId(room2.getId())).thenReturn(room2Devices);
		
		// when
		List<Device> devicesFound = deviceService.findAllByRoomId(room2.getId());
		
		// then
		assertNotNull(devicesFound);
		assertThat(devicesFound).isEmpty();
	}
	
	@Test
	public void findByPersonsId_Should_ReturnDevicesForPerson_When_DevicesForPersonExist() {
		// given
		Mockito.when(deviceRepository.findByPersonsId(person2.getId())).thenReturn(person2Devices);
		
		// when
		List<Device> devicesFound = deviceService.findByPersonsId(person2.getId());
		
		// then
		assertNotNull(devicesFound);
		assertThat(devicesFound.size()).isEqualTo(person2Devices.size());
	}
	
	@Test
	public void findByPersonsId_Should_NotReturnDevicesForPerson_When_DevicesForPersonNotExist() {
		// given
		Mockito.when(deviceRepository.findByPersonsId(person1.getId())).thenReturn(person1Devices);
		
		// when
		List<Device> devicesFound = deviceService.findByPersonsId(person1.getId());
		
		// then
		assertNotNull(devicesFound);
		assertThat(devicesFound).isEmpty();
	}
	
	@Test
	public void findByPersonsIdAndRoomId_Should_ReturnDevicesForPersonAndRoom_When_DevicesForPersonAndRoomExist() {
		// given
		List<Device> person2Room1Devices = person2Devices
				.stream()
		        .filter(device -> !room1Devices.contains(device))
		        .collect(Collectors.toList());
		Mockito
			.when(deviceRepository.findByPersonsIdAndRoomId(person2.getId(), room1.getId()))
			.thenReturn(person2Room1Devices);
		
		// when
		List<Device> devicesFound = deviceService.findByPersonsIdAndRoomId(person2.getId(), room1.getId());
		
		// then
		assertNotNull(devicesFound);
		assertThat(devicesFound.size()).isEqualTo(person2Room1Devices.size());
	}
	
	@Test
	public void findByPersonsIdAndRoomId_Should_NotReturnDevicesForPersonAndRoom_When_DevicesForPersonAndRoomNotExist() {
		// given
		List<Device> person1Room1Devices = person1Devices
				.stream()
		        .filter(device -> !room1Devices.contains(device))
		        .collect(Collectors.toList());
		Mockito
			.when(deviceRepository.findByPersonsIdAndRoomId(person1.getId(), room1.getId()))
			.thenReturn(person1Room1Devices);
		
		// when
		List<Device> devicesFound = deviceService.findByPersonsIdAndRoomId(person1.getId(), room1.getId());
		
		// then
		assertNotNull(devicesFound);
		assertThat(devicesFound).isEmpty();
	}
	
	@Test
	public void getSelectedDevices_Should_ReturnDevices_When_DevicesHaveBeenSelectedAndExist() {
		// given
		List<String> selectedDeviceIds = Arrays.asList("1", "3", "4"); // "4" does not exist
		
		// when
		List<Device> devicesFound = deviceService.getSelectedDevices(selectedDeviceIds);
		
		// then
		assertNotNull(devicesFound);
		assertThat(devicesFound.size()).isEqualTo(2);
	}
	
	@Test
	public void getSelectedDevices_Should_NotReturnDevices_When_DevicesNotSelected() {
		// given
		List<String> selectedDeviceIds = Arrays.asList();
		
		// when
		List<Device> devicesFound = deviceService.getSelectedDevices(selectedDeviceIds);
		
		// then
		assertNotNull(devicesFound);
		assertThat(devicesFound).isEmpty();
	}
	
	@Test
	public void getSelectedDevices_Should_NotReturnDevices_When_DevicesSelectedButNotExist() {
		// given
		List<String> selectedDeviceIds = Arrays.asList("6", "5", "4"); // none of "6", "5", "4" exists
		
		// when
		List<Device> devicesFound = deviceService.getSelectedDevices(selectedDeviceIds);
		
		// then
		assertNotNull(devicesFound);
		assertThat(devicesFound).isEmpty();
	}	
	
	@Test
	public void save_ShouldPersistDevice_When_Called() {
		// given
		Device deviceToPersist = new Device();
		deviceToPersist.setName("air condition");
		Mockito.when(deviceRepository.save(deviceToPersist)).thenReturn(deviceToPersist);
		
		// when
		Device devicePersisted = deviceService.save(deviceToPersist);
		
		// then
		assertNotNull(devicePersisted);
		assertThat(devicePersisted.getName()).isEqualTo("air condition");
	}
	
	@Test
	public void delete_ShouldCallRepositoryDeleteOnce_When_Called() {
		// given
		Device device = new Device();
		
		// when
		deviceService.delete(device);
		
		// then
		verify(deviceRepository).delete(any());
	}
}