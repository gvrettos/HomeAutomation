package eu.codingschool.homeautomation.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.codingschool.homeautomation.model.Device;
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
	public List<Room> findByName(String name) {
		return roomRepository.findByName(name);
	}

	@Override
	public Room save(Room r) {
		return roomRepository.save(r);
	}
	
	@Override
	public void delete(Integer id) {
		roomRepository.deleteById(id);
	}

	@Override
	public void delete(Room r) {
		roomRepository.delete(r);
	}
	
	@Override
	public Set<Device> findLocatedDevices(Integer id) {
		return roomRepository.findLocatedDevices(id);
	}
	
}
