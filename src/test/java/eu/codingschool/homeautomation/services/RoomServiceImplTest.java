package eu.codingschool.homeautomation.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import eu.codingschool.homeautomation.repositories.projections.RoomDevicesCount;
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

import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.repositories.RoomRepository;

@RunWith(SpringRunner.class)
public class RoomServiceImplTest {

	@TestConfiguration
	static class RoomServiceImplTestContextConfiguration {
		@Bean
		public RoomService roomService() {
			return new RoomServiceImpl();
		}
	}

	@Autowired
	private RoomService roomService;

	@MockBean
	private RoomRepository roomRepository;
	
	@MockBean
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private Person person1;
	private Person person2;
	
	private Room room1;
	private Room room2;
	
	private List<RoomDevicesCount> person1Rooms;
	private List<RoomDevicesCount> person2Rooms;

	@Before
	public void setUp() {
		room1 = new Room();
		room1.setId(1);
		
		room2 = new Room();
		room2.setId(2);
		
		person1 = new Person();
		person1.setId(1);
		person2 = new Person();
		person2.setId(2);
		
		person1Rooms = Arrays.asList(
				new RoomDevicesCount(room1, (long)Math.random()),
				new RoomDevicesCount(room2, (long)Math.random())
		);
		person2Rooms = Arrays.asList();
		
		Mockito.when(roomRepository.findById(room1.getId())).thenReturn(Optional.of(room1));
		Mockito.when(roomRepository.findById(room2.getId())).thenReturn(Optional.of(room2));
	}
	
	@Test
	public void findAll_Should_ReturnRooms_When_RoomsExist() {
		// given
		List<Room> roomsSaved = Arrays.asList(room1, room2);
		Mockito.when(roomRepository.findAll()).thenReturn(roomsSaved);
		
		// when
		List<Room> roomsFound = roomService.findAll();
		
		// then
		assertNotNull(roomsFound);
		assertThat(roomsFound.size()).isEqualTo(roomsSaved.size());
	}
	
	@Test
	public void findAll_Should_NotReturnRooms_When_RoomsNotExist() {
		// given
		Mockito.when(roomRepository.findAll()).thenReturn(Arrays.asList());
		
		// when
		List<Room> rooms = roomService.findAll();
		
		// then
		assertNotNull(rooms);
		assertThat(rooms).isEmpty();
	}
	
	@Test
	public void findById_Should_ReturnRoom_When_RoomWithIdExists() {
		// when
		Room room = roomService.findById(room1.getId());
		
		//then
		assertNotNull(room);
		assertThat(room.getId()).isEqualTo(room1.getId());
	}
	
	@Test
	public void findById_Should_NotReturnRoom_When_RoomWithIdNotExists() {
		// when
		Room room = roomService.findById(3);
		
		// then
		assertNull(room);
	}

	@Test
	public void findByUser_Should_ReturnRooms_When_RoomsForUserExists() {
		// given
		Mockito.when(roomRepository.findUserRooms(person1.getId())).thenReturn(person1Rooms);
		
		// when
		List<RoomDevicesCount> rooms = roomService.findByUser(person1.getId());
		
		// then
		assertNotNull(rooms);
		assertThat(rooms.size()).isEqualTo(person1Rooms.size());
	}
	
	@Test
	public void findByUser_Should_NotReturnRooms_When_RoomsForUserNotExists() {
		// given
		Mockito.when(roomRepository.findUserRooms(person2.getId())).thenReturn(person2Rooms);
		
		// when
		List<RoomDevicesCount> room = roomService.findByUser(person2.getId());
		
		// then
		assertThat(room).isEmpty();
	}
	
	@Test
	public void save_ShouldPersistRoom_When_Called() {
		// given
		Room roomToPersist = new Room();
		roomToPersist.setName("Dining Room");
		Mockito.when(roomRepository.save(roomToPersist)).thenReturn(roomToPersist);
		
		// when
		Room roomPersisted = roomService.save(roomToPersist);
		
		// then
		assertNotNull(roomPersisted);
		assertThat(roomPersisted.getName()).isEqualTo("Dining Room");
	}
	
	@Test
	public void delete_ShouldCallRepositoryDeleteOnce_When_Called() {
		// given
		Room room = new Room();
		
		// when
		roomService.delete(room);
		
		// then
		verify(roomRepository).delete(any());
	}
}