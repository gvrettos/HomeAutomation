package eu.codingschool.homeautomation.services;

import java.util.List;
import java.util.Set;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Room;

public interface RoomService {
	
	List<Room> findAll();
	Room findById(Integer id);
	List<Room> findByName(String name);
	Set<Room> findByUser(Integer personId);
    Set<Device> findLocatedDevices(Integer id);
    
    Room save(Room r);
    
	void delete(Integer id);
	void delete(Room r);
}