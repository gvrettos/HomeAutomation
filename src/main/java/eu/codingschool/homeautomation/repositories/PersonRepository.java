package eu.codingschool.homeautomation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.codingschool.homeautomation.model.Person;

@Repository("personRepository")
public interface PersonRepository extends JpaRepository<Person, Integer> {
	
    Person findByEmail(String email);
}