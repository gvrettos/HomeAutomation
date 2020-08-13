package eu.codingschool.homeautomation.services;

import java.util.Set;

import eu.codingschool.homeautomation.model.Room;

public interface RoomService {
	
	Set<Room> findAll();
	
	Room findById(Integer id);
	
	Set<Room> findByUser(Integer personId);
    
    Room save(Room r);
    
	void delete(Room r);
}