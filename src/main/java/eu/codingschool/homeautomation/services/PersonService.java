package eu.codingschool.homeautomation.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Person;

public interface PersonService {
	
	List<Person> findAll();
	
	Person findById(Integer id);
	
    Person findByEmail(String email);
    
    Person save(Person p);
    
    void update(Person personModel, List<Device> selectedDevices);
    
	void delete(Person p);
	
	UserDetails getLoggedInUser();
}