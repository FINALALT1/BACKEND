package kr.co.moneybridge.core.annotation;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class SwaggerResponses {
    private static final String BAD_REQUEST = "{\n" +
        "&nbsp;&nbsp;\"status\": \"badRequest\",\n" +
        "&nbsp;&nbsp;\"msg\": 400,\n" +
        "&nbsp;&nbsp;\"data\": \"에러 메세지\"\n" +
        "}";
    private static final String UNAUTHORIZED = "{\n" +
            "&nbsp;&nbsp;\"status\": \"unAuthorized\",\n" +
            "&nbsp;&nbsp;\"msg\": 401,\n" +
            "&nbsp;&nbsp;\"data\": \"에러 메세지\"\n" +
            "}";
    private static final String UNAUTHORIZED2 = "{\n" +
            "&nbsp;&nbsp;\"status\": \"unAuthorized\",\n" +
            "&nbsp;&nbsp;\"msg\": 401,\n" +
            "&nbsp;&nbsp;\"data\": \"에러 메세지2\"\n" +
            "}";
    private static final String FORBIDDEN = "{\n" +
            "&nbsp;&nbsp;\"status\": \"forbidden\",\n" +
            "&nbsp;&nbsp;\"msg\": 403,\n" +
            "&nbsp;&nbsp;\"data\": \"에러 메세지\"\n" +
            "}";
    private static final String NOT_FOUND = "{\n" +
            "&nbsp;&nbsp;\"status\": \"notFound\",\n" +
            "&nbsp;&nbsp;\"msg\": 404,\n" +
            "&nbsp;&nbsp;\"data\": \"에러 메세지\"\n" +
            "}";
    private static final String INTERNAL_SERVER_ERROR = "{\n" +
            "&nbsp;&nbsp;\"status\": \"unknownServerError\",\n" +
            "&nbsp;&nbsp;\"msg\": 500,\n" +
            "&nbsp;&nbsp;\"data\": \"에러 메세지\"\n" +
            "}";

    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 404,
                    message = NOT_FOUND),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DefaultApiResponses {
    }

    @ApiResponses({
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 404,
                    message = NOT_FOUND),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ApiResponsesWithout400 {
    }
}
