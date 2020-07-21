package eu.codingschool.homeautomation.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Room;

@Repository("roomRepository")
public interface RoomRepository extends JpaRepository<Room, Integer> {
	
	List<Room> findByName(String name);
    
    @Query("select r.devices from Room r where r.id = :id")
    Set<Device> findLocatedDevices(@Param("id") Integer id);
    
    @Query("select r " + 
      	   "from Room r " + 
      	   "join r.devices d " + 
      	   "join d.persons p " + 
      	   "where p.id = :id")
     Set<Room> findUserRooms(@Param("id") Integer personId);
}
