package project.carsharing.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import project.carsharing.validation.annotation.Name;

public class NameValidator implements ConstraintValidator<Name, String> {
    private static final String PASSWORD_PATTERN = "^[A-Za-z\\-]{3,25}$";
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return Pattern.compile(PASSWORD_PATTERN).matcher(value).matches();
    }
}
