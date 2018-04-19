package eu.codingschool.homeautomation.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.DeviceType;

@Repository("deviceTypeRepository")
public interface DeviceTypeRepository extends JpaRepository<DeviceType, Integer> {
	
	List<DeviceType> findByType(String type);
	
}
