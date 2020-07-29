package eu.codingschool.homeautomation.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.codingschool.homeautomation.model.Device;

@Repository("deviceRepository")
public interface DeviceRepository extends JpaRepository<Device, Integer> {
	
	List<Device> findByRoomId(Integer roomId);
    
    List<Device> findByPersonsId(Integer id);
    
    List<Device> findByPersonsIdAndRoomId(Integer userId, Integer roomId);
}
