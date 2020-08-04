package eu.codingschool.homeautomation.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.DeviceType;
import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.model.Room;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DeviceRepositoryTest {
	
	@Autowired
    private TestEntityManager entityManager;

	@Autowired
	private DeviceRepository deviceRepository;
	
	@Test
	public void findByRoomId_Should_ReturnDevices_When_DevicesForRoomExist() {
	    // given
		Person person = new Person();
		Room room = new Room();
		initDevices(person, room);
	 
	    // when
	    List<Device> devicesFound = deviceRepository.findByRoomId(room.getId());
	 
	    // then
	    assertThat(devicesFound.size()).isEqualTo(4);
	}
	
	@Test
	public void findByRoomId_Should_NotReturnDevices_When_DevicesForRoomNotExist() {
	    // given
		Room room = new Room();
		persistRoom(room);
		entityManager.flush();
	 
	    // when
	    List<Device> devicesFound = deviceRepository.findByRoomId(room.getId());
	 
	    // then
	    assertThat(devicesFound.size()).isEqualTo(0);
	}
	
	@Test
	public void findByPersonsId_Should_ReturnDevices_When_DevicesForPersonExist() {
	    // given
		Person person = new Person();
		Room room = new Room();
		initDevices(person, room);
	 
	    // when
	    List<Device> devicesFound = deviceRepository.findByPersonsId(person.getId());
	 
	    // then
	    assertThat(devicesFound.size()).isEqualTo(3);
	}
	
	@Test
	public void findByPersonsId_Should_NotReturnDevices_When_DevicesForPersonNotExist() {
	    // given
		Person person = new Person();
		entityManager.persist(person);
		entityManager.flush();
	 
	    // when
	    List<Device> devicesFound = deviceRepository.findByPersonsId(person.getId());
	 
	    // then
	    assertThat(devicesFound.size()).isEqualTo(0);
	}
	
	@Test
	public void findByPersonsIdAndRoomId_Should_ReturnDevices_When_DevicesForPersonAndRoomExist() {
	    // given
		Person person = new Person();
		Room room = new Room();
		initDevices(person, room);
	 
	    // when
	    List<Device> devicesFound = deviceRepository.findByPersonsIdAndRoomId(person.getId(), room.getId());
	 
	    // then
	    assertThat(devicesFound.size()).isEqualTo(2);
	}
	
	@Test
	public void findByPersonsIdAndRoomId_Should_NotReturnDevices_When_DevicesForPersonAndRoomNotExist() {
	    // given
		Person person = new Person();
		Room room = new Room();
		entityManager.persist(person);
		persistRoom(room);
		entityManager.flush();
	 
	    // when
	    List<Device> devicesFound = deviceRepository.findByPersonsIdAndRoomId(person.getId(), room.getId());
	 
	    // then
	    assertThat(devicesFound.size()).isEqualTo(0);
	}
	
	private void initDevices(Person person, Room room) {
		persistDeviceTypes();
		persistRoom(room);
	    persistDevices(person, room);
	    persistDevices(new Person(), room);
	}

	private void persistDeviceTypes() {
		DeviceType thermostatDeviceType = new DeviceType("Thermostat", "Target temp.");
		DeviceType lightsDeviceType = new DeviceType("Lights", "Illumination");
		entityManager.persist(thermostatDeviceType);
	    entityManager.persist(lightsDeviceType);
	}

	private void persistRoom(Room room) {
		entityManager.persist(room);
	}
	
	private void persistDevices(Person person, Room room) {
		Device device1 = new Device();
	    device1.setRoom(room);
		Device device2 = new Device();
		device2.setRoom(room);
		
		Room otherRoom = new Room();
		persistRoom(otherRoom);
		Device device3 = new Device();
		device3.setRoom(otherRoom);
	    
	    person.addDevice(device1);
		person.addDevice(device2);	
		person.addDevice(device3);
		entityManager.persist(person);
		
	    entityManager.persist(device1);
	    entityManager.persist(device2);
	    entityManager.persist(device3);
	    entityManager.flush();
	}
	
}