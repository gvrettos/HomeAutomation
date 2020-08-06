package eu.codingschool.homeautomation.services;

import java.util.HashSet;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.repositories.PersonRepository;

@Service
public class PersonServiceImpl implements PersonService {
	
	@Autowired
	PersonRepository personRepository;
	
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public List<Person> findAll() {
		return personRepository.findAll();
	}
	
	@Override
	public Person findById(Integer id) {
		return personRepository.findById(id).orElse(null);
	}
	
	@Override
	public Person findByEmail(String email) {
		return personRepository.findByEmail(email);
	}

	@Override
	public Person save(Person p) {
		if (p.getPassword() != null) {
			p.setPassword(bCryptPasswordEncoder.encode(p.getPassword()));
			p.setRole("USER");
		}
		return personRepository.save(p);
	}
	
	@Override
	@Transactional
	public void update(Person personModel, List<Device> selectedDevices) {
		if (personModel != null) {
			Person personDB = findById(personModel.getId());
			personDB.setName(personModel.getName());
			personDB.setSurname(personModel.getSurname());
			personDB.setEmail(personModel.getEmail());
			personDB.setRole(personModel.getRole());
			personDB.setDevices(new HashSet<>(selectedDevices));
		}
	}

	@Override
	public void delete(Person p) {
		personRepository.delete(p);
	}
	
	@Override
	public UserDetails getLoggedInUser() {
	    Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    if (userDetails instanceof UserDetails) {
	    	return (UserDetails)userDetails;
	    }
	    return null;
	}
	
}
