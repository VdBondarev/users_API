package api.annotation.validator;

import static api.constant.OtherConstantsHolder.ONE;
import static api.constant.OtherConstantsHolder.ZERO;

import api.annotation.FirstParamIsBeforeSecond;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.List;

public class FirstParamIsBeforeSecondValidator
        implements ConstraintValidator<FirstParamIsBeforeSecond, List<LocalDate>> {

    @Override
    public boolean isValid(List<LocalDate> birthDate,
                           ConstraintValidatorContext constraintValidatorContext) {
        return !birthDate.get(ZERO).isAfter(birthDate.get(ONE));
    }
}
