package api.annotation;

import api.annotation.validator.FirstParamIsBeforeSecondValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = FirstParamIsBeforeSecondValidator.class)
@Documented
public @interface FirstParamIsBeforeSecond {
    String message() default "is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
