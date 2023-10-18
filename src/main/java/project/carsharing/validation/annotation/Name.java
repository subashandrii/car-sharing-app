package project.carsharing.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import project.carsharing.validation.validator.NameValidator;

@Constraint(validatedBy = NameValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {
    String message() default "format is not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
