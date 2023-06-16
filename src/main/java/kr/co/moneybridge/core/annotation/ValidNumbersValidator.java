package kr.co.moneybridge.core.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidNumbersValidator implements ConstraintValidator<ValidNumbers, Integer> {
    private Set<Integer> validNumbers;

    @Override
    public void initialize(ValidNumbers constraint) {
        validNumbers = Arrays.stream(constraint.values()).boxed().collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && validNumbers.contains(value);
    }
}

