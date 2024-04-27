package api.annotation;

import api.annotation.validator.LessThanYearsFromNowValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = LessThanYearsFromNowValidator.class)
@Documented
public @interface BeforeThanYearsFromNow {
    String message() default "is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int value() default -1;
}
