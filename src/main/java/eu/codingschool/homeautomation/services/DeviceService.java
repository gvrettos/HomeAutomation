package eu.codingschool.homeautomation.services;

import java.util.List;
import java.util.Set;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Person;

public interface DeviceService {
	
	List<Device> findAll();
	Device findById(Integer id);
	List<Device> findByName(String name);
	List<Device> findByStatus(String status);
    Set<Person> findUsersAssigned(Integer id);
    
    Device save(Device d);
    
	void delete(Integer id);
	void delete(Device d);
}
