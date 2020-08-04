package eu.codingschool.homeautomation.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import eu.codingschool.homeautomation.model.Person;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PersonRepositoryTest {
	
	@Autowired
    private TestEntityManager entityManager;

	@Autowired
	private PersonRepository personRepository;
	
	@Test
	public void findByEmail_Should_ReturnPerson_When_UserWithEmailExists() {
	    // given
	    Person personGiven = new Person("AdminName", "AdminSurname", "admin@foo.com", "***", "ADMIN");
	    entityManager.persist(personGiven);
	    entityManager.flush();
	 
	    // when
	    Person personFound = personRepository.findByEmail(personGiven.getEmail());
	 
	    // then
	    assertThat(personFound.getName()).isEqualTo(personGiven.getName());
	}
	
	@Test
	public void findByEmail_Should_NotReturnPerson_When_UserWithEmailNotExists() {
	    // given
	    Person personGiven = new Person("AdminName", "AdminSurname", "adminSurname@foo.com", "***", "ADMIN");
	    entityManager.persist(personGiven);
	    entityManager.flush();
	 
	    // when
	    Person personFound = personRepository.findByEmail("admin@foo.com");
	 
	    // then
	    assertNull(personFound);
	}
}