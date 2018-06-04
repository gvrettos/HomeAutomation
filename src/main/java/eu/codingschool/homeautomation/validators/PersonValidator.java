package eu.codingschool.homeautomation.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import eu.codingschool.homeautomation.model.Person;



	@Component
	public class PersonValidator implements Validator {

	    @Override
	    public boolean supports(Class<?> aClass) {
	        return Person.class.equals(aClass);
	    }

	    @Override
	    public void validate(Object o, Errors errors) {
	        Person person = (Person) o;

	        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty");
	        if (person.getName().length() < 3 || person.getName().length() > 32) {
	            errors.rejectValue("name", "Size");
	        }
	        
	        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "surname", "NotEmpty");
	        if (person.getSurname().length() < 3 || person.getSurname().length() > 32) {
	            errors.rejectValue("surname", "Size");
	        }

	        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "Wrong email format");
	        if (!person.getEmail().contains("@") ) {
	            errors.rejectValue("email", "Size");
	        }
	    }
	}
	
	
	
