package eu.codingschool.homeautomation.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Person;

@Repository("personRepository")
public interface PersonRepository extends JpaRepository<Person, Integer> {
	
	List<Person> findByName(String name);
	List<Person> findBySurname(String surname);
    List<Person> findByEmail(String email);
    List<Person> findByRole(String role);
    
    @Query("select p.devices from Person p where p.id = :id")
    Set<Device> findUserDevices(@Param("id") Integer id);
}