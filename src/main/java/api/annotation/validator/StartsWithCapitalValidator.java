package api.annotation.validator;

import api.annotation.StartsWithCapital;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StartsWithCapitalValidator implements ConstraintValidator<StartsWithCapital, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || Character.isUpperCase(value.charAt(0));
    }
}
