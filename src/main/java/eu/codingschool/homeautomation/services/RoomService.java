package eu.codingschool.homeautomation.services;

import java.util.List;

import eu.codingschool.homeautomation.model.Room;
import eu.codingschool.homeautomation.repositories.projections.RoomDevicesCount;

public interface RoomService {
	
	List<Room> findAll();
	
	Room findById(Integer id);
	
	List<RoomDevicesCount> findByUser(Integer personId);
    
    Room save(Room r);
    
	void delete(Room r);
}