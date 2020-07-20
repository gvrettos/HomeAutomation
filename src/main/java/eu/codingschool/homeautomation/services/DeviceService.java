package eu.codingschool.homeautomation.services;

import java.util.List;
import java.util.Set;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Person;

public interface DeviceService {
	
	List<Device> findAll();
	Device findById(Integer id);
	List<Device> findByName(String name);
	List<Device> findByStatus(boolean statusOn);
    Set<Person> findUsersAssigned(Integer id);
    
    Device save(Device d);
    
	void delete(Integer id);
	void delete(Device d);
	
	List<Device> findByPersonsId(Integer id);
	List<Device> getSelectedDevices(List<String> selectedDeviceIds);
}
