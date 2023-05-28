package kr.co.moneybridge.core.config;

import kr.co.moneybridge.core.filter.MyTempFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyFilterRegisterConfig {
    @Bean
    public FilterRegistrationBean<?> filter1() {
        FilterRegistrationBean<MyTempFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new MyTempFilter()); // 서블릿 필터 객체 담기
        registration.addUrlPatterns("/*");
        registration.setOrder(1); // 순서
        return registration;
    }
}
