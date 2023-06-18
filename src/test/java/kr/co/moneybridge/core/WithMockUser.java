package kr.co.moneybridge.core;

import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.user.UserPropensity;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserFactory.class)
public @interface WithMockUser {
    long id() default 1L;
    String name() default "lee";
    Role role() default Role.USER;
}