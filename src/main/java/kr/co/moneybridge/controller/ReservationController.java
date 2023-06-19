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
import kr.co.moneybridge.dto.reservation.ReviewResponse;
import kr.co.moneybridge.model.reservation.ReservationType;
import kr.co.moneybridge.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static kr.co.moneybridge.core.util.MyEnumUtil.*;

@RequiredArgsConstructor
@RestController
public class ReservationController {
    private final ReservationService reservationService;

    @MyLog
    @ApiOperation(value = "상담 예약 사전 정보 조회")
    @SwaggerResponses.ApiResponsesWithout400
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user/reservation/{pbId}")
    public ResponseDTO<ReservationResponse.BaseDTO> getReservationBase(@PathVariable Long pbId,
                                                                       @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.BaseDTO baseDTO = reservationService.getReservationBase(pbId, myUserDetails.getMember().getId());

        return new ResponseDTO<>(baseDTO);
    }

    @MyLog
    @ApiOperation(value = "상담 예약 신청하기")
    @SwaggerResponses.DefaultApiResponses
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

        if (applyDTO.getCandidateTime1() == null) {
            throw new Exception400(applyDTO.getCandidateTime1().toString(), "값을 입력해주세요.");
        }
        // 현재 시간보다 이전 날짜인지 확인
        if (applyDTO.getCandidateTime1().isBefore(LocalDateTime.now())) {
            throw new Exception400(applyDTO.getCandidateTime1().toString(), "현재 시간보다 이전 날짜는 선택할 수 없습니다.");
        }

        if (applyDTO.getCandidateTime2() == null) {
            throw new Exception400(applyDTO.getCandidateTime2().toString(), "값을 입력해주세요.");
        }
        // 현재 시간보다 이전 날짜인지 확인
        if (applyDTO.getCandidateTime2().isBefore(LocalDateTime.now())) {
            throw new Exception400(applyDTO.getCandidateTime2().toString(), "현재 시간보다 이전 날짜는 선택할 수 없습니다.");
        }

        if (!applyDTO.getQuestion().matches("^.{0,100}$")) {
            throw new Exception400(applyDTO.getQuestion(), "최대 100자까지 입력 가능합니다.");
        }

        if (applyDTO.getUserName() == null || applyDTO.getUserName().isBlank()) {
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
    @SwaggerResponses.ApiResponsesWithout400
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/pb/reviews")
    public ResponseDTO<PageDTO<ReservationResponse.ReviewDTO>> getReviews(@RequestParam(defaultValue = "0") int page,
                                                                          @AuthenticationPrincipal MyUserDetails myUserDetails) {
        PageDTO<ReservationResponse.ReviewDTO> reviewsDTO = reservationService.getReviews(myUserDetails.getMember().getId(), page);

        return new ResponseDTO<>(reviewsDTO);
    }

    @MyLog
    @ApiOperation(value = "나의 예약 페이지 상담 현황 조회")
    @SwaggerResponses.ApiResponsesWithout400
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user/reservations/recent")
    public ResponseDTO<ReservationResponse.RecentInfoDTO> getRecentReservationInfoByUser(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.RecentInfoDTO recentInfoDTO = reservationService.getRecentReservationInfoByUser(myUserDetails.getMember().getId());

        return new ResponseDTO<>(recentInfoDTO);
    }

    @MyLog
    @ApiOperation(value = "고객 관리 페이지 상담 현황 조회")
    @SwaggerResponses.ApiResponsesWithout400
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/pb/management/recent")
    public ResponseDTO<ReservationResponse.RecentInfoDTO> getRecentReservationInfo(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.RecentInfoDTO recentInfoDTO = reservationService.getRecentReservationInfo(myUserDetails.getMember().getId());

        return new ResponseDTO<>(recentInfoDTO);
    }

    @MyLog
    @ApiOperation(value = "고객 관리 페이지 상담 목록 조회")
    @SwaggerResponses.DefaultApiResponses
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
    @SwaggerResponses.ApiResponsesWithout400
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/pb/reservation/{id}")
    public ResponseDTO<ReservationResponse.DetailByPBDTO> getReservationDetail(@PathVariable Long id,
                                                                               @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.DetailByPBDTO detailByPBDTO = reservationService.getReservationDetailByPB(id, myUserDetails.getMember().getId());

        return new ResponseDTO<>(detailByPBDTO);
    }

    @MyLog
    @ApiOperation(value = "예약 변경하기")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/auth/reservation/{id}")
    public ResponseDTO updateReservation(@PathVariable Long id,
                                         @RequestBody ReservationRequest.UpdateDTO updateDTO,
                                         @AuthenticationPrincipal MyUserDetails myUserDetails) {
        // 현재 시간보다 이전 날짜인지 확인
        if (updateDTO.getTime() != null) {
            if (updateDTO.getTime().isBefore(LocalDateTime.now())) {
                throw new Exception400(updateDTO.getTime().toString(), "현재 시간보다 이전 날짜는 선택할 수 없습니다.");
            }
        }

        if (updateDTO.getType() != null && !isValidReservationType(updateDTO.getType())) {
            throw new Exception400(updateDTO.getType().toString(), "Enum 형식에 맞춰 요청해주세요.");
        }

        if (updateDTO.getType().equals(ReservationType.VISIT)) {
            if (updateDTO.getLocationName() == null || updateDTO.getLocationName().isBlank()) {
                throw new Exception400(updateDTO.getLocationName(), "상담 장소를 입력해주세요.");
            }

            if (updateDTO.getLocationAddress() == null || updateDTO.getLocationAddress().isBlank()) {
                throw new Exception400(updateDTO.getLocationAddress(), "상담 주소를 입력해주세요.");
            }
        }

        reservationService.updateReservation(id, updateDTO, myUserDetails);

        return new ResponseDTO<>();
    }

    @MyLog
    @ApiOperation(value = "예약 취소하기")
    @SwaggerResponses.ApiResponsesWithout400
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/auth/reservation/{id}")
    public ResponseDTO cancelReservation(@PathVariable Long id,
                                         @AuthenticationPrincipal MyUserDetails myUserDetails) {
        reservationService.cancelReservation(id, myUserDetails);

        return new ResponseDTO<>();
    }

    @MyLog
    @ApiOperation(value = "예약 확정하기")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/pb/reservation/{id}/confirmed")
    public ResponseDTO confirmReservation(@PathVariable Long id,
                                          @RequestBody ReservationRequest.ConfirmDTO confirmDTO,
                                          @AuthenticationPrincipal MyUserDetails myUserDetails) {
        reservationService.confirmReservation(id, myUserDetails.getMember().getId(), confirmDTO);

        return new ResponseDTO<>();
    }

    @MyLog
    @ApiOperation(value = "예약 완료하기")
    @SwaggerResponses.ApiResponsesWithout400
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/auth/reservation/{id}/completed")
    public ResponseDTO completeReservation(@PathVariable Long id,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails) {
        reservationService.completeReservation(id, myUserDetails);

        return new ResponseDTO<>();
    }

    @ApiOperation(value = "PB 상담후기 최신 3개 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/reviews/{pbId}")
    public ResponseDTO<List<ReviewResponse.ReviewOutDTO>> getPBReviews(@PathVariable(value = "pbId") Long id) {

        List<ReviewResponse.ReviewOutDTO> reviewDTO = reservationService.getPBReviews(id);
        ReviewResponse.ReviewListOutDTO reviewListOutDTO = new ReviewResponse.ReviewListOutDTO(reviewDTO);
        return new ResponseDTO(reviewListOutDTO);
    }
}
