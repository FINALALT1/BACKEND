package kr.co.moneybridge.core.annotation;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.reservation.ReservationResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class SwaggerResponses {
    private static final String OK = "ok"; // 200
    private static final String BAD_REQUEST = "badRequest"; // 400
    private static final String UNAUTHORIZED = "unAuthorized"; // 401
    private static final String FORBIDDEN = "forbidden"; // 403
    private static final String NOT_FOUND = "notFound"; // 404
    private static final String INTERNAL_SERVER_ERROR = "unknownServerError"; // 500

    @ApiResponses({
            @ApiResponse(code = 200,
                    message = OK,
                    response = ResponseDTO.class),
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
            @ApiResponse(code = 200,
                    message = OK,
                    response = ReservationResponse.BaseDTO.class),
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
    public @interface GetReservationBase {
    }

    @ApiResponses({
            @ApiResponse(code = 200,
                    message = OK,
                    response = PageDTO.class),
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
    public @interface GetReviews {
    }
}
