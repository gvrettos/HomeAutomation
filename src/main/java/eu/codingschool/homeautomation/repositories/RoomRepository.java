package eu.codingschool.homeautomation.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.codingschool.homeautomation.model.Room;

@Repository("roomRepository")
public interface RoomRepository extends JpaRepository<Room, Integer> {
    
    @Query("select r " +
      	   "from Room r " + 
      	   "join r.devices d " + 
      	   "join d.persons p " + 
      	   "where p.id = :id " +
		   "group by r.id")
	List<Room> findUserRooms(@Param("id") Integer personId);
}
