package eu.codingschool.homeautomation.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.repositories.RoomRepository;

@Service
public class RoomServiceImpl implements RoomService {
	
	@Autowired
	RoomRepository roomRepository;

	@Override
	public List<Room> findAll() {
		return roomRepository.findAll();
	}
	
	@Override
	public Room findById(Integer id) {
		return roomRepository.findById(id).get();
	}
	
	@Override
	public Set<Room> findByUser(Integer personId) {
		return roomRepository.findUserRooms(personId);
	}

	@Override
	public Room save(Room r) {
		return roomRepository.save(r);
	}

	@Override
	public void delete(Room r) {
		roomRepository.delete(r);
	}
}
