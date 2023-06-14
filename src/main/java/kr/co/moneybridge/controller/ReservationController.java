package kr.co.moneybridge.controller;

import io.swagger.annotations.ApiOperation;
import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.annotation.SwaggerResponses;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.reservation.ReservationRequest;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
import kr.co.moneybridge.model.reservation.ReservationType;
import kr.co.moneybridge.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static kr.co.moneybridge.core.util.MyEnumUtil.*;

@RequiredArgsConstructor
@RestController
public class ReservationController {
    private final ReservationService reservationService;

    @ApiOperation(value = "상담 예약 사전 정보 조회")
    @SwaggerResponses.GetReservationBase
    @MyLog
    @GetMapping("/user/reservation/{pbId}")
    public ResponseEntity<?> getReservationBase(@PathVariable Long pbId,
                                                @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.BaseOutDTO baseOutDTO = reservationService.getReservationBase(pbId, myUserDetails.getMember().getId());

        ResponseDTO<?> responseDTO = new ResponseDTO<>(baseOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation(value = "상담 예약 신청하기")
    @SwaggerResponses.DefaultApiResponses
    @MyLog
    @PostMapping("/user/reservation/{pbId}")
    public ResponseEntity<?> applyReservation(@PathVariable Long pbId,
                                              @RequestBody ReservationRequest.ApplyInDTO applyInDTO,
                                              @AuthenticationPrincipal MyUserDetails myUserDetails) {
        if (applyInDTO.getGoal() == null
                || !isValidReservationGoal(applyInDTO.getGoal())) {
            throw new Exception400(applyInDTO.getGoal().toString(), "Enum 형식에 맞춰 요청해주세요.");
        }

        if (!isValidReservationType(applyInDTO.getReservationType())) {
            throw new Exception400(applyInDTO.getReservationType().toString(), "Enum 형식에 맞춰 요청해주세요.");
        }

        if (applyInDTO.getReservationType().equals(ReservationType.VISIT)) {
            if (!isValidLocationType(applyInDTO.getLocationType())) {
                throw new Exception400(applyInDTO.getLocationType().toString(), "Enum 형식에 맞춰 요청해주세요.");
            }
        }

        if (!applyInDTO.getCandidateTime1().matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
            throw new Exception400(applyInDTO.getCandidateTime1(), "2023-05-15T09:00:00 형식에 맞춰 입력해주세요.");
        }

        if (!applyInDTO.getCandidateTime2().matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
            throw new Exception400(applyInDTO.getCandidateTime2(), "2023-05-15T09:00:00 형식에 맞춰 입력해주세요.");
        }

        if (!applyInDTO.getQuestion().matches("^.{0,100}$")) {
            throw new Exception400(applyInDTO.getQuestion(), "최대 100자까지 입력 가능합니다.");
        }

        if (applyInDTO.getUserName().isBlank()) {
            throw new Exception400(applyInDTO.getUserName(), "이름을 입력해주세요.");
        }

        if (!applyInDTO.getUserPhoneNumber().matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$")) {
            throw new Exception400(applyInDTO.getUserPhoneNumber(), "유효하지 않은 휴대폰 번호 형식입니다.");
        }

        if (!applyInDTO.getUserEmail().matches("^(?=.{1,30}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new Exception400(applyInDTO.getUserEmail(), "유효하지 않은 이메일 형식입니다.");
        }

        reservationService.applyReservation(pbId, applyInDTO, myUserDetails.getMember().getId());

        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation(value = "상담 후기 리스트 조회")
    @SwaggerResponses.GetReviews
    @MyLog
    @GetMapping("/pb/reviews")
    public ResponseEntity<?> getReviews(@RequestParam(defaultValue = "0") int page,
                                        @AuthenticationPrincipal MyUserDetails myUserDetails) {
        PageDTO<ReservationResponse.ReviewDTO> reviewsOutDTO = reservationService.getReviews(myUserDetails.getMember().getId(), page);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(reviewsOutDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
