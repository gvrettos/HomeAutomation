package eu.codingschool.homeautomation.services;

import java.util.List;

import eu.codingschool.homeautomation.model.DeviceType;

public interface DeviceTypeService {
	
	List<DeviceType> findAll();
	DeviceType findById(Integer id);
	List<DeviceType> findByType(String type);
    
	DeviceType save(DeviceType dt);
    
	void delete(Integer id);
	void delete(DeviceType dt);
}
