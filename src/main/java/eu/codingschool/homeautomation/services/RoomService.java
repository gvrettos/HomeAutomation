package eu.codingschool.homeautomation.services;

import java.util.List;

import eu.codingschool.homeautomation.model.Room;

public interface RoomService {
	
	List<Room> findAll();
	
	Room findById(Integer id);
	
	List<Room> findByUser(Integer personId);
    
    Room save(Room r);
    
	void delete(Room r);
}