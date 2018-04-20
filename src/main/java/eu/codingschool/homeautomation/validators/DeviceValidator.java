package eu.codingschool.homeautomation.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import eu.codingschool.homeautomation.model.Device;

@Component
public class DeviceValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> aClass) {
		return Device.class.equals(aClass);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "deviceType", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "room", "NotEmpty");
	}
}
