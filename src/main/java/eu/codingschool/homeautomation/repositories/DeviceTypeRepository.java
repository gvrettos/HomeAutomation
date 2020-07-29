package eu.codingschool.homeautomation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.codingschool.homeautomation.model.DeviceType;

@Repository("deviceTypeRepository")
public interface DeviceTypeRepository extends JpaRepository<DeviceType, Integer> {
	
}
