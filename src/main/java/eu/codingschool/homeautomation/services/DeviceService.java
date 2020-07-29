package eu.codingschool.homeautomation.services;

import java.util.List;

import eu.codingschool.homeautomation.model.Device;

public interface DeviceService {
	
	List<Device> findAll();
	
	Device findById(Integer id);
	
	List<Device> findAllByRoomId(Integer roomId);
	
	List<Device> findByPersonsId(Integer id);
	
	List<Device> findByPersonsIdAndRoomId(Integer userId, Integer roomId);
	
	List<Device> getSelectedDevices(List<String> selectedDeviceIds);
	
    Device save(Device d);
    
	void delete(Device d);
}
