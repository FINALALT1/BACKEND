package kr.co.moneybridge.core.config;

import kr.co.moneybridge.core.filter.TempFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterRegisterConfig {
    @Bean
    public FilterRegistrationBean<?> filter1() {
        FilterRegistrationBean<TempFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TempFilter()); // 서블릿 필터 객체 담기
        registration.addUrlPatterns("/*");
        registration.setOrder(1); // 순서
        return registration;
    }
}
