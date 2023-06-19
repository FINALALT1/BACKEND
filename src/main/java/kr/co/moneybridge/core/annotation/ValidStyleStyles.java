package kr.co.moneybridge.core.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StyleStylesValidator.class)
public @interface ValidStyleStyles {
    String message() default "Enum 형식에 맞춰 요청해주세요.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
