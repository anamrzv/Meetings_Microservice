package ifmo.validator;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class BirthdayValidator implements ConstraintValidator<Birthday, LocalDate> {
    @Override
    public boolean isValid(final LocalDate valueToValidate, final ConstraintValidatorContext context) {
        return Period.between(valueToValidate, LocalDate.now()).getYears() >= 16;
    }
}

