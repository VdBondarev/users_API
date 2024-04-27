package api.annotation.validator;

import api.annotation.BeforeThanYearsFromNow;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;

public class LessThanYearsFromNowValidator
        implements ConstraintValidator<BeforeThanYearsFromNow, LocalDate> {
    private int value;
    @Value("${default.years.ago.value}")
    private int valueFromPropertiesFile;

    @Override
    public void initialize(BeforeThanYearsFromNow constraintAnnotation) {
        // if not initialized - then use default value
        value = constraintAnnotation.value() == -1
                ? valueFromPropertiesFile
                : constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDate valueFromAnnotation,
                           ConstraintValidatorContext constraintValidatorContext) {
        LocalDate requiredValue = LocalDate.now().minusYears(value);
        return valueFromAnnotation == null || !valueFromAnnotation.isAfter(requiredValue);
    }
}
