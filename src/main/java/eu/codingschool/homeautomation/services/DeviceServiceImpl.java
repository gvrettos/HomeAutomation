package eu.codingschool.homeautomation.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.repositories.DeviceRepository;

@Service
public class DeviceServiceImpl implements DeviceService {
	
	@Autowired
	DeviceRepository deviceRepository;

	@Override
	public List<Device> findAll() {
		return deviceRepository.findAll();
	}

	@Override
	public Device findById(Integer id) {
		return deviceRepository.findById(id).orElse(null);
	}
	
	@Override
	public List<Device> findAllByRoomId(Integer roomId) {
		return deviceRepository.findByRoomId(roomId);
	}

	@Override
	public List<Device> findByPersonsId(Integer id) {
		return deviceRepository.findByPersonsId(id);
	}
	
	@Override
	public List<Device> findByPersonsIdAndRoomId(Integer userId, Integer roomId) {
		return deviceRepository.findByPersonsIdAndRoomId(userId, roomId);
	}
	
	@Override
	public List<Device> getSelectedDevices(List<String> selectedDeviceIds) {
		List<Device> devices = new ArrayList<>();
		if (selectedDeviceIds != null) {
			for (String deviceIdStr : selectedDeviceIds) {
				int deviceId = Integer.parseInt(deviceIdStr);
				devices.add(findById(deviceId));
			}
		}
		return devices;
	}
	
	@Override
	public Device save(Device d) {
		return deviceRepository.save(d);
	}

	@Override
	public void delete(Device d) {
		deviceRepository.delete(d);
	}
}
