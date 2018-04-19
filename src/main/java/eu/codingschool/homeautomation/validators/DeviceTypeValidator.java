package eu.codingschool.homeautomation.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import eu.codingschool.homeautomation.model.DeviceType;

@Component
public class DeviceTypeValidator implements Validator {

	@Override
	public boolean supports(Class<?> aClass) {
		return DeviceType.class.equals(aClass);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "informationType", "NotEmpty");
	}

}
