package eu.codingschool.homeautomation.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eu.codingschool.homeautomation.app.HomeAutomationApplication;
import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Person;

/**
 * FIXME
 * 
 * Execution order of tests is not guaranteed. So, results from a test must NOT rely on the results of another one.
 * For now the tests are quite dummy and do not fulfill this condition. 
 * Each test should be totally independent from the rest.
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HomeAutomationApplication.class)
public class PersonServiceTests {
	
	@Autowired
	private PersonService personService;

	@Test
	public void testSaveAdmin() {
		Person personSaved = personService.save(
				new Person("AdminName", "AdminSurname", "aSurname@foo.com", "foo", "ADMIN"));
		assertNotNull("Admin was not saved", personSaved);
	}
	
	@Test
	public void testSaveUser1() {
		Person personSaved = personService.save(
				new Person("UserName1", "UserSurname1", "uSurname1@foo.com", "foo", "USER"));
		assertNotNull("User1 was not saved", personSaved);
	}
	
	@Test
	public void testSaveUser2() {
		Person personSaved = personService.save(
				new Person("UserName2", "UserSurname2", "uSurname2@foo.com", "foo", "USER"));
		assertNotNull("User2 was not saved", personSaved);
	}
	
	@Test
	public void testFindAll() {
		List<Person> persons = personService.findAll();
		assertNotNull("No persons exist", persons);
		assertFalse("No persons exist", persons.isEmpty());
	}
	
	/** 
	 * Get a random person and try finding them by id 
	 */
	@Test
	public void testFindById() {
		List<Person> persons = personService.findAll();
		if (!persons.isEmpty()) {
			Integer id = persons.get(0).getId();
			Person person = personService.findById(id);
			assertNotNull("Requested person does not exist", person);
		}
	}
	
	/** 
	 * Get a random person and try finding them by name 
	 */
	@Test
	public void testFindByName() {
		List<Person> persons = personService.findAll();
		if (!persons.isEmpty()) {
			String name = persons.get(0).getName();
			List<Person> personsFoundByCriteria = personService.findByName(name);
			assertNotNull("Requested person does not exist", personsFoundByCriteria);
			assertFalse("Requested person does not exist", personsFoundByCriteria.isEmpty());
		}
	}
	
	/** 
	 * Get a random person and try finding them by surname
	 */
	@Test
	public void testFindBySurname() {
		List<Person> persons = personService.findAll();
		if (!persons.isEmpty()) {
			String surname = persons.get(0).getSurname();
			List<Person> personsFoundByCriteria = personService.findBySurname(surname);
			assertNotNull("Requested person does not exist", personsFoundByCriteria);
			assertFalse("Requested person does not exist", personsFoundByCriteria.isEmpty());
		}
	}
	
	/** 
	 * Get a random person and try finding them by email
	 */
	@Test
	public void testFindByEmail() {
		List<Person> persons = personService.findAll();
		if (!persons.isEmpty()) {
			String email = persons.get(0).getEmail();
			List<Person> personsFoundByCriteria = personService.findByEmail(email);
			assertNotNull("Requested person does not exist", personsFoundByCriteria);
			assertFalse("Requested person does not exist", personsFoundByCriteria.isEmpty());
		}
	}
	
	/** 
	 * Get a random person and try finding them by role
	 */
	@Test
	public void testFindByRole() {
		List<Person> persons = personService.findAll();
		if (!persons.isEmpty()) {
			String role = persons.get(0).getRole();
			List<Person> personsFoundByCriteria = personService.findByRole(role);
			assertNotNull("Requested person does not exist", personsFoundByCriteria);
			assertFalse("Requested person does not exist", personsFoundByCriteria.isEmpty());
		}
	}
	
	@Test
	public void testFindUserDevices() {
		// TODO This is a dummy test: No devices are actually inserted in the database
		Set<Device> devices = personService.findUserDevices(1);
		System.err.println("devices: " + devices != null ? devices.size() : "-1");
	}
	
	/*
	 * FIXME
	 * It throws java.util.NoSuchElementException: No value present while calling findById().
	 * This is correct if the deletion succeeds. Handle that more gracefully.
	 */
	@Test
	public void testDeleteById() {
		List<Person> persons = personService.findAll();
		if (!persons.isEmpty()) {
			Integer id = persons.get(0).getId();
			personService.delete(id);
			Person person = personService.findById(id);
			assertNull("Requested person was not deleted", person);
		}
	}
	
	/*
	 * FIXME
	 * It throws java.util.NoSuchElementException: No value present while calling findById().
	 * This is correct if the deletion succeeds. Handle that more gracefully.
	 */
	@Test
	public void testDeleteByPerson() {
		List<Person> persons = personService.findAll();
		if (!persons.isEmpty()) {
			Person personToBeDeleted = persons.get(0);
			personService.delete(personToBeDeleted);
			Person person = personService.findById(personToBeDeleted.getId());
			assertNull("Requested person was not deleted", person);
		}
	}
}