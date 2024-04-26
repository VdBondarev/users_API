package api.annotation.validator;

import static api.constant.ConstantsHolder.PHONE_VALIDATION_REGEX;

import api.annotation.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String phoneNumber,
                           ConstraintValidatorContext constraintValidatorContext) {
        return phoneNumber == null || phoneNumber.matches(PHONE_VALIDATION_REGEX);
    }
}
