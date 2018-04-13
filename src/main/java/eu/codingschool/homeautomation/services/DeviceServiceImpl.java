package eu.codingschool.homeautomation.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Person;
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
		return deviceRepository.findById(id).get();
	}

	@Override
	public List<Device> findByName(String name) {
		return deviceRepository.findByName(name);
	}

	@Override
	public List<Device> findByStatus(String status) {
		return deviceRepository.findByStatus(status);
	}

	@Override
	public Set<Person> findUsersAssigned(Integer id) {
		return deviceRepository.findUsersAssigned(id);
	}

	@Override
	public Device save(Device d) {
		return deviceRepository.save(d);
	}

	@Override
	public void delete(Integer id) {
		deviceRepository.deleteById(id);
	}

	@Override
	public void delete(Device d) {
		deviceRepository.delete(d);
	}

}
