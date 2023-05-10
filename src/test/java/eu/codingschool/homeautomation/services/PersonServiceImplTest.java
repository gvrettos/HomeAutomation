package eu.codingschool.homeautomation.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import eu.codingschool.homeautomation.model.Device;
import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.repositories.PersonRepository;

@RunWith(SpringRunner.class)
public class PersonServiceImplTest {

	@TestConfiguration
	static class PersonServiceImplTestContextConfiguration {
		@Bean
		public PersonService personService() {
			return new PersonServiceImpl();
		}
	}

	@Autowired
	private PersonService personService;

	@MockBean
	private PersonRepository personRepository;
	
	@MockBean
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private static final String PERSON1_EMAIL = "person1@foo.com";
	private static final String PERSON1_EMAIL_UPDATED = "person1Updated@foo.com";
	private static final String PERSON2_EMAIL = "person2@foo.com";
	
	private Person person1;
	private Person person2;

	@Before
	public void setUp() {
		person1 = new Person();
		person1.setId(1);
		person1.setEmail(PERSON1_EMAIL);
		
		person2 = new Person();
		person2.setId(2);
		person2.setEmail(PERSON2_EMAIL);
		
		Mockito.when(personRepository.findById(person1.getId())).thenReturn(Optional.of(person1));
		Mockito.when(personRepository.findById(person2.getId())).thenReturn(Optional.of(person2));
		
		Mockito.when(personRepository.findByEmail(person1.getEmail())).thenReturn(person1);
		Mockito.when(personRepository.findByEmail(person2.getEmail())).thenReturn(person2);
	}
	
	@Test
	public void findAll_shouldReturnPersons_whenPersonsExist() {
		// given
		List<Person> personsSaved = Arrays.asList(person1, person2);
		Mockito.when(personRepository.findAll()).thenReturn(personsSaved);
		
		// when
		List<Person> personsFound = personService.findAll();
		
		// then
		assertNotNull(personsFound);
		assertThat(personsFound.size()).isEqualTo(personsSaved.size());
	}
	
	@Test
	public void findAll_shouldNotReturnPersons_whenPersonsNotExist() {
		// given
		Mockito.when(personRepository.findAll()).thenReturn(Arrays.asList());
		
		// when
		List<Person> persons = personService.findAll();
		
		// then
		assertNotNull(persons);
		assertThat(persons).isEmpty();
	}
	
	@Test
	public void findById_shouldReturnPerson_whenPersonWithIdExists() {
		// when
		Person person = personService.findById(person1.getId());
		
		//then
		assertNotNull(person);
		assertThat(person.getId()).isEqualTo(person1.getId());
	}
	
	@Test
	public void findById_shouldNotReturnPerson_whenPersonWithIdNotExists() {
		// when
		Person person = personService.findById(3);
		
		// then
		assertNull(person);
	}

	@Test
	public void findByEmail_shouldReturnPerson_whenPersonWithEmailExists() {
		// when
		Person person = personService.findByEmail(PERSON1_EMAIL);
		
		// then
		assertNotNull(person);
		assertThat(person.getEmail()).isEqualTo(PERSON1_EMAIL);
	}
	
	@Test
	public void findByEmail_shouldNotReturnPerson_whenPersonWithEmailNotExists() {
		// when
		Person person = personService.findByEmail("email@foo.com");
		
		// then
		assertNull(person);
	}
	
	@Test
	public void save_shouldPersistUserWithSimpleUserRole_whenPasswordExists() {
		// given
		Person personToPersist = new Person();
		personToPersist.setPassword("***");
		Mockito.when(personRepository.save(personToPersist)).thenReturn(personToPersist);
		
		// when
		Person personPersisted = personService.save(personToPersist);
		
		// then
		assertNotNull(personPersisted);
		assertThat(personPersisted.getRole()).isEqualTo("USER");
	}
	
	@Test
	public void save_shouldPersistUserWithoutRole_whenPasswordNotExists() {
		// given
		Person personToPersist = new Person();
		Mockito.when(personRepository.save(personToPersist)).thenReturn(personToPersist);
		
		// when
		Person personPersisted = personService.save(personToPersist);
		
		// then
		assertNotNull(personPersisted);
		assertNull(personPersisted.getRole());
	}
	
	@Test
	public void update_shouldUpdatePersonDetails_whenPersonModelProvided() {
		// given
		Person person = new Person();
		person.setId(1);
		person.setEmail(PERSON1_EMAIL_UPDATED);
		List<Device> devices = Arrays.asList(new Device(), new Device(), new Device());
		
		// when 
		personService.update(person, devices);
		Person personUpdated = personService.findById(person.getId());
		
		// then
		assertNotNull(personUpdated.getDevices());
		assertThat(personUpdated.getDevices().size()).isEqualTo(devices.size());
		assertThat(personUpdated.getEmail()).isEqualTo(PERSON1_EMAIL_UPDATED);
	}
	
	@Test
	public void update_shouldNotUpdatePersonDetails_whenPersonModelNotProvided() {
		// given
		Person person = new Person();
		person.setId(1);
		person.setEmail(PERSON1_EMAIL_UPDATED);
		List<Device> devices = Arrays.asList(new Device(), new Device(), new Device());
		
		// when
		personService.update(null, devices);
		Person personUpdated = personService.findById(person.getId());
		
		// then
		assertNotNull(personUpdated.getDevices());
		assertThat(personUpdated.getDevices()).isEmpty();
		assertThat(personUpdated.getEmail()).isEqualTo(PERSON1_EMAIL);
	}
	
	@Test
	public void delete_shouldCallRepositoryDeleteOnce_whenCalled() {
		// given
		Person person = new Person();
		
		// when
		personService.delete(person);
		
		// then
		verify(personRepository).delete(any());
	}
	
	@Test
	@WithMockUser // given
	public void getLoggedInUser_shouldExist_whenMockUserExists() {
		// when - then
		assertNotNull(personService.getLoggedInUser());
	}
	
	@Test(expected = NullPointerException.class) // then
	public void getLoggedInUser_shouldThrowException_whenUserNotExists() {
		// when
		personService.getLoggedInUser();
	}
}