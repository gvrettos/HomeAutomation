package eu.codingschool.homeautomation.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import eu.codingschool.homeautomation.model.Person;

@RunWith(SpringRunner.class)
public class UserDetailsServiceImplTest {

	@TestConfiguration
	static class UserDetailsServiceImplTestContextConfiguration {
		@Bean
		public UserDetailsServiceImpl userDetailsService() {
			return new UserDetailsServiceImpl();
		}
	}
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@MockBean
	private PersonService personService;
	
	@Test
	public void loadUserByUsername_Should_LoadUser_When_UserExists() {
		// given
		Person person = new Person("Admin", "Surname", "administrator@foo.com", "***", "ADMIN");
		Mockito.when(personService.findByEmail(person.getEmail())).thenReturn(person);
        Object[] person1Authorities = new GrantedAuthority[] {
        		new SimpleGrantedAuthority(person.getRole())
        };
		
		// when 
		UserDetails userDetails = userDetailsService.loadUserByUsername(person.getEmail());
		
		// then
		assertNotNull(userDetails);
		assertThat(userDetails.getUsername()).isEqualTo(person.getEmail());
		assertThat(userDetails.getAuthorities().toArray()).contains(person1Authorities);
	}
	
	@Test(expected = UsernameNotFoundException.class) // then
	public void loadUserByUsername_Should_ThrowException_When_UserNotExists() {
		// when 
		userDetailsService.loadUserByUsername("mail@foo.com");
	}
}