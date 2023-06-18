package kr.co.moneybridge.core.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidNumbersValidator.class)
public @interface ValidNumbers {
    String message() default "Invalid number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int[] values() default {};
}
