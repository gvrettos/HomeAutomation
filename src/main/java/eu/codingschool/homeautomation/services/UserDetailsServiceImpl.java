package eu.codingschool.homeautomation.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import eu.codingschool.homeautomation.model.Person;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
    private PersonService personService;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        
        Person person = personService.findByEmail(email);
        if (person != null) {
        	grantedAuthorities.add(new SimpleGrantedAuthority(person.getRole().toString()));
        }
        
        return new org.springframework.security.core.userdetails.User(
        		person.getEmail(), person.getPassword(), grantedAuthorities);
	}

}
