package kr.co.moneybridge.core.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@EnableSwagger2
@Configuration
public class MySwaggerConfig {
    @Bean
    public Docket commonApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("common API")
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("kr.co.moneybridge.controller"))
                .paths(Predicates.not(PathSelectors.ant("/auth/**")))
                .paths(Predicates.not(PathSelectors.ant("/admin/**")))
                .paths(Predicates.not(PathSelectors.ant("/user/**")))
                .paths(Predicates.not(PathSelectors.ant("/pb/**")))
                .build();
    }

    @Bean
    public Docket authApi() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
                .name("Authorization")
                .parameterType("header")
                .required(true)
                .modelRef(new ModelRef("string"))
                .description("JWT Access Token")
                .build());

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("auth API")
                .globalOperationParameters(parameters)
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .useDefaultResponseMessages(false)
                .apiInfo(new ApiInfoBuilder()
                        .title("MoneyBridge API Documentation")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("kr.co.moneybridge.controller"))
                .paths(PathSelectors.ant("/auth/**"))
                .build();
    }

    @Bean
    public Docket adminApi() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
                .name("Authorization")
                .parameterType("header")
                .required(true)
                .modelRef(new ModelRef("string"))
                .description("JWT Access Token")
                .build());

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("admin API")
                .globalOperationParameters(parameters)
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .useDefaultResponseMessages(false)
                .apiInfo(new ApiInfoBuilder()
                        .title("MoneyBridge API Documentation")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("kr.co.moneybridge.controller"))
                .paths(PathSelectors.ant("/admin/**"))
                .build();
    }

    @Bean
    public Docket userApi() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
                .name("Authorization")
                .parameterType("header")
                .required(true)
                .modelRef(new ModelRef("string"))
                .description("JWT Access Token")
                .build());

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("user API")
                .globalOperationParameters(parameters)
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .useDefaultResponseMessages(false)
                .apiInfo(new ApiInfoBuilder()
                        .title("MoneyBridge API Documentation")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("kr.co.moneybridge.controller"))
                .paths(PathSelectors.ant("/user/**"))
                .build();
    }

    @Bean
    public Docket pbApi() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
                .name("Authorization")
                .parameterType("header")
                .required(true)
                .modelRef(new ModelRef("string"))
                .description("JWT Access Token")
                .build());

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("pb API")
                .globalOperationParameters(parameters)
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .useDefaultResponseMessages(false)
                .apiInfo(new ApiInfoBuilder()
                        .title("MoneyBridge API Documentation")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("kr.co.moneybridge.controller"))
                .paths(PathSelectors.ant("/pb/**"))
                .build();
    }
}
