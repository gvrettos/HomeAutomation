package eu.codingschool.homeautomation.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.codingschool.homeautomation.model.DeviceType;
import eu.codingschool.homeautomation.repositories.DeviceTypeRepository;

@Service
public class DeviceTypeServiceImpl implements DeviceTypeService {
	
	@Autowired
	DeviceTypeRepository deviceTypeRepository;

	@Override
	public List<DeviceType> findAll() {
		return deviceTypeRepository.findAll();
	}
	
	@Override
	public DeviceType findById(Integer id) {
		return deviceTypeRepository.findById(id).get();
	}
	
	@Override
	public DeviceType save(DeviceType dt) {
		return deviceTypeRepository.save(dt);
	}

	@Override
	public void delete(Integer id) {
		deviceTypeRepository.deleteById(id);
	}

	@Override
	public void delete(DeviceType dt) {
		deviceTypeRepository.delete(dt);
	}

}
