package eu.codingschool.homeautomation.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.repositories.PersonRepository;

@Service
public class PersonServiceImpl implements PersonService {
	
	@Autowired
	PersonRepository personRepository;

	@Override
	public List<Person> findAll() {
		return personRepository.findAll();
	}
	
	@Override
	public Person findById(Integer id) {
		return personRepository.findById(id).get();
	}
	
	@Override
	public List<Person> findByName(String name) {
		return personRepository.findByName(name);
	}

	@Override
	public List<Person> findBySurname(String surname) {
		return personRepository.findBySurname(surname);
	}
	
	@Override
	public List<Person> findByEmail(String email) {
		return personRepository.findByEmail(email);
	}

	@Override
	public List<Person> findByRole(String role) {
		return personRepository.findByRole(role);
	}
	
	@Override
	public Set<Device> findUserDevices(Integer id) {
		return personRepository.findUserDevices(id);
	}

	@Override
	public Person save(Person p) {
		return personRepository.save(p);
	}

	@Override
	public void delete(Integer id) {
		personRepository.deleteById(id);
	}

	@Override
	public void delete(Person p) {
		personRepository.delete(p);
	}
}
