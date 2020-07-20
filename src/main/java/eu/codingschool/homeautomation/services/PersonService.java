package eu.codingschool.homeautomation.services;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Person;

public interface PersonService {
	
	List<Person> findAll();
	Person findById(Integer id);
	List<Person> findByName(String name);
	List<Person> findBySurname(String surname);
    Person findByEmail(String email);
    List<Person> findByRole(String role);
    Set<Device> findUserDevices(Integer personId);
    
    Person save(Person p);
    void update(Person personModel, List<Device> selectedDevices);
    
	void delete(Integer id);
	void delete(Person p);
	
	UserDetails getLoggedInUser();
}