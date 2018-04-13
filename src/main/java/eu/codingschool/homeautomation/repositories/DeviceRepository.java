package eu.codingschool.homeautomation.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Person;

@Repository("deviceRepository")
public interface DeviceRepository extends JpaRepository<Device, Integer> {
	
	List<Device> findByName(String name);
	List<Device> findByStatus(String surname);
    
    @Query("select d.persons from Device d where d.id = :id")
    Set<Person> findUsersAssigned(@Param("id") Integer id);
}
