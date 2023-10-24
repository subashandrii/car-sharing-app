package project.carsharing.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import project.carsharing.util.PatternUtil;
import project.carsharing.validation.annotation.Name;

public class NameValidator implements ConstraintValidator<Name, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return Pattern.compile(PatternUtil.NAME_PATTERN).matcher(value).matches();
    }
}
