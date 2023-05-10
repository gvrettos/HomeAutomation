package eu.codingschool.homeautomation.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import eu.codingschool.homeautomation.repositories.projections.RoomDevicesCount;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.model.Room;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RoomRepositoryTest {
	
	@Autowired
    private TestEntityManager entityManager;

	@Autowired
	private RoomRepository roomRepository;
	
	@Test
	public void findUserRooms_shouldReturnRooms_whenUserHasAccessToAtLeastOneRoom() {
	    // given
		Room room = new Room();
	    entityManager.persist(room);
	    
	    Device device = new Device();
	    device.setRoom(room);
	    entityManager.persist(device);
	    
	    Person person = new Person();
	    person.addDevice(device);
	    entityManager.persist(person);
	    
	    entityManager.flush();
	 
	    // when
	    List<RoomDevicesCount> roomsFound = roomRepository.findUserRooms(person.getId());
	 
	    // then
	    assertThat(roomsFound.size()).isGreaterThan(0);
	}
	
	@Test
	public void findUserRooms_shouldNotReturnRooms_whenUserHasNotAccessToAtLeastOneRoom() {
	    // given
	    Person person = new Person();
	    entityManager.persist(person);
	    entityManager.flush();
	 
	    // when
	    List<RoomDevicesCount> roomsFound = roomRepository.findUserRooms(person.getId());
	 
	    // then
	    assertTrue(roomsFound.isEmpty());
	}
}