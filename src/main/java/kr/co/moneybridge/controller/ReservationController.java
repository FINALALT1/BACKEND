package kr.co.moneybridge.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.reservation.ReservationRequest;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
import kr.co.moneybridge.model.reservation.ReservationType;
import kr.co.moneybridge.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static kr.co.moneybridge.core.util.MyEnumUtil.*;

@RequiredArgsConstructor
@RestController
public class ReservationController {
    private final ReservationService reservationService;
    private final String BAD_REQUEST = "{\n" +
            "&nbsp;&nbsp;\"status\": \"badRequest\",\n" +
            "&nbsp;&nbsp;\"msg\": 400,\n" +
            "&nbsp;&nbsp;\"data\": \"에러 메세지\"\n" +
            "}";
    private final String UNAUTHORIZED = "{\n" +
            "&nbsp;&nbsp;\"status\": \"unAuthorized\",\n" +
            "&nbsp;&nbsp;\"msg\": 401,\n" +
            "&nbsp;&nbsp;\"data\": \"에러 메세지\"\n" +
            "}";
    private final String FORBIDDEN = "{\n" +
            "&nbsp;&nbsp;\"status\": \"forbidden\",\n" +
            "&nbsp;&nbsp;\"msg\": 403,\n" +
            "&nbsp;&nbsp;\"data\": \"에러 메세지\"\n" +
            "}";
    private final String NOT_FOUND = "{\n" +
            "&nbsp;&nbsp;\"status\": \"notFound\",\n" +
            "&nbsp;&nbsp;\"msg\": 404,\n" +
            "&nbsp;&nbsp;\"data\": \"에러 메세지\"\n" +
            "}";
    private final String INTERNAL_SERVER_ERROR = "{\n" +
            "&nbsp;&nbsp;\"status\": \"unknownServerError\",\n" +
            "&nbsp;&nbsp;\"msg\": 500,\n" +
            "&nbsp;&nbsp;\"data\": \"에러 메세지\"\n" +
            "}";

    @MyLog
    @ApiOperation(value = "상담 예약 사전 정보 조회")
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
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user/reservation/{pbId}")
    public ResponseDTO<ReservationResponse.BaseDTO> getReservationBase(@PathVariable Long pbId,
                                                                       @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.BaseDTO baseDTO = reservationService.getReservationBase(pbId, myUserDetails.getMember().getId());

        return new ResponseDTO<>(baseDTO);
    }

    @MyLog
    @ApiOperation(value = "상담 예약 신청하기")
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
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/user/reservation/{pbId}")
    public ResponseDTO applyReservation(@PathVariable Long pbId,
                                        @RequestBody ReservationRequest.ApplyDTO applyDTO,
                                        @AuthenticationPrincipal MyUserDetails myUserDetails) {
        if (applyDTO.getGoal() == null
                || !isValidReservationGoal(applyDTO.getGoal())) {
            throw new Exception400(applyDTO.getGoal().toString(), "Enum 형식에 맞춰 요청해주세요.");
        }

        if (!isValidReservationType(applyDTO.getReservationType())) {
            throw new Exception400(applyDTO.getReservationType().toString(), "Enum 형식에 맞춰 요청해주세요.");
        }

        if (applyDTO.getReservationType().equals(ReservationType.VISIT)) {
            if (!isValidLocationType(applyDTO.getLocationType())) {
                throw new Exception400(applyDTO.getLocationType().toString(), "Enum 형식에 맞춰 요청해주세요.");
            }
        }

        if (!applyDTO.getCandidateTime1().matches("\\\\d{1,2}월 \\\\d{1,2}일 (오전|오후) \\\\d{1,2}시 \\\\d{1,2}분")) {
            throw new Exception400(applyDTO.getCandidateTime1(), "형식에 맞춰 입력해주세요.");
        }

        if (!applyDTO.getCandidateTime2().matches("\\\\d{1,2}월 \\\\d{1,2}일 (오전|오후) \\\\d{1,2}시 \\\\d{1,2}분")) {
            throw new Exception400(applyDTO.getCandidateTime2(), "형식에 맞춰 입력해주세요.");
        }

        if (!applyDTO.getQuestion().matches("^.{0,100}$")) {
            throw new Exception400(applyDTO.getQuestion(), "최대 100자까지 입력 가능합니다.");
        }

        if (applyDTO.getUserName().isBlank()) {
            throw new Exception400(applyDTO.getUserName(), "이름을 입력해주세요.");
        }

        if (!applyDTO.getUserPhoneNumber().matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$")) {
            throw new Exception400(applyDTO.getUserPhoneNumber(), "유효하지 않은 휴대폰 번호 형식입니다.");
        }

        if (!applyDTO.getUserEmail().matches("^(?=.{1,30}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new Exception400(applyDTO.getUserEmail(), "유효하지 않은 이메일 형식입니다.");
        }

        reservationService.applyReservation(pbId, applyDTO, myUserDetails.getMember().getId());

        return new ResponseDTO();
    }

    @MyLog
    @ApiOperation(value = "상담 후기 리스트 조회")
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
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/pb/reviews")
    public ResponseDTO<PageDTO<ReservationResponse.ReviewDTO>> getReviews(@RequestParam(defaultValue = "0") int page,
                                                                          @AuthenticationPrincipal MyUserDetails myUserDetails) {
        PageDTO<ReservationResponse.ReviewDTO> reviewsDTO = reservationService.getReviews(myUserDetails.getMember().getId(), page);

        return new ResponseDTO<>(reviewsDTO);
    }

    @MyLog
    @ApiOperation(value = "고객 관리 페이지 상담 현황 조회")
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
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/pb/management/recent")
    public ResponseDTO<ReservationResponse.RecentInfoDTO> getRecentReservationInfo(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.RecentInfoDTO recentInfoDTO = reservationService.getRecentReservationInfo(myUserDetails.getMember().getId());

        return new ResponseDTO<>(recentInfoDTO);
    }

    @MyLog
    @ApiOperation(value = "고객 관리 페이지 상담 목록 조회")
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
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/pb/management/reservations")
    public ResponseDTO<PageDTO<ReservationResponse.RecentReservationDTO>> getRecentReservations(String type,
                                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                                @AuthenticationPrincipal MyUserDetails myUserDetails) {
        switch (type) {
            case "APPLY":
            case "CONFIRM":
            case "COMPLETE":
            case "WITHDRAW":
                break;
            default:
                throw new Exception400(type, "Enum 형식에 맞춰 요청해주세요.");
        }

        PageDTO<ReservationResponse.RecentReservationDTO> recentReservationsDTO = reservationService.gerRecentReservations(type, page, myUserDetails.getMember().getId());

        return new ResponseDTO<>(recentReservationsDTO);
    }

    @MyLog
    @ApiOperation(value = "예약 확인하기")
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
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/pb/reservation/{id}")
    public ResponseDTO<ReservationResponse.DetailDTO> getReservationDetail(@PathVariable("id") Long id,
                                                                            @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.DetailDTO detailDTO = reservationService.getReservationDetail(id, myUserDetails.getMember().getId());

        return new ResponseDTO<>(detailDTO);
    }
}
