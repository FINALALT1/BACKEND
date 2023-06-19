package kr.co.moneybridge.core;

import kr.co.moneybridge.model.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockAdminFactory.class)
public @interface WithMockAdmin {
    long id() default 1L;
    String name() default "관리자";
    Role role() default Role.ADMIN;
}