package kr.co.moneybridge.controller;

import io.swagger.annotations.ApiOperation;
import kr.co.moneybridge.core.annotation.Log;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static kr.co.moneybridge.core.util.DateUtil.StringToLocalDateTime;
import static kr.co.moneybridge.core.util.DateUtil.StringToLocalTime;
import static kr.co.moneybridge.core.util.EnumUtil.*;

@RequiredArgsConstructor
@RestController
public class ReservationController {
    private final ReservationService reservationService;

    @Log
    @ApiOperation(value = "고객 관리 페이지 상담 현황 조회")
    @SwaggerResponses.ApiResponsesWithout400
    @GetMapping("/pb/management/recent")
    public ResponseDTO<ReservationResponse.RecentInfoDTO> getRecentReservationInfo(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.RecentInfoDTO recentInfoDTO = reservationService.getRecentReservationInfo(myUserDetails.getMember().getId());

        return new ResponseDTO<>(recentInfoDTO);
    }

    @Log
    @ApiOperation(value = "나의 예약 페이지 상담 현황 조회")
    @SwaggerResponses.ApiResponsesWithout400
    @GetMapping("/user/reservations/recent")
    public ResponseDTO<ReservationResponse.RecentInfoDTO> getRecentReservationInfoByUser(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.RecentInfoDTO recentInfoDTO = reservationService.getRecentReservationInfoByUser(myUserDetails.getMember().getId());

        return new ResponseDTO<>(recentInfoDTO);
    }

    @Log
    @ApiOperation(value = "고객 관리 페이지 상담 목록 조회")
    @SwaggerResponses.DefaultApiResponses
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

        PageDTO<ReservationResponse.RecentReservationDTO> recentReservationsDTO = reservationService.getRecentReservations(type, page, myUserDetails.getMember().getId());

        return new ResponseDTO<>(recentReservationsDTO);
    }

    @Log
    @ApiOperation(value = "나의 예약 페이지 상담 목록 조회")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/user/reservations")
    public ResponseDTO<PageDTO<ReservationResponse.RecentReservationByUserDTO>> getRecentReservationsByUser(String type,
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

        PageDTO<ReservationResponse.RecentReservationByUserDTO> recentReservationsDTO = reservationService.getRecentReservationsByUser(type, page, myUserDetails.getMember().getId());

        return new ResponseDTO<>(recentReservationsDTO);
    }

    @Log
    @ApiOperation(value = "예약 확인하기(PB)")
    @SwaggerResponses.ApiResponsesWithout400
    @GetMapping("/pb/reservation/{id}")
    public ResponseDTO<ReservationResponse.DetailByPBDTO> getReservationDetail(@PathVariable Long id,
                                                                               @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.DetailByPBDTO detailByPBDTO = reservationService.getReservationDetail(id, myUserDetails.getMember().getId());

        return new ResponseDTO<>(detailByPBDTO);
    }

    @Log
    @ApiOperation(value = "예약 확인하기(투자자)")
    @SwaggerResponses.ApiResponsesWithout400
    @GetMapping("/user/reservation/{id}")
    public ResponseDTO<ReservationResponse.DetailByUserDTO> getReservationDetailByUser(@PathVariable Long id,
                                                                                       @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.DetailByUserDTO detailByUserDTO = reservationService.getReservationDetailByUser(id, myUserDetails.getMember().getId());

        return new ResponseDTO<>(detailByUserDTO);
    }

    @Log
    @ApiOperation(value = "상담 예약 사전 정보 조회")
    @SwaggerResponses.ApiResponsesWithout400
    @GetMapping("/user/reservation/base/{pbId}")
    public ResponseDTO<ReservationResponse.BaseDTO> getReservationBase(@PathVariable Long pbId,
                                                                       @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.BaseDTO baseDTO = reservationService.getReservationBase(pbId, myUserDetails.getMember().getId());

        return new ResponseDTO<>(baseDTO);
    }

    @Log
    @ApiOperation(value = "상담 예약 신청하기")
    @SwaggerResponses.DefaultApiResponses
    @PostMapping("/user/reservation/{pbId}")
    public ResponseDTO addReservation(@PathVariable Long pbId,
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

        if (applyDTO.getQuestion() != null && !applyDTO.getQuestion().matches("^.{0,100}$")) {
            throw new Exception400(applyDTO.getQuestion(), "최대 100자까지 입력 가능합니다.");
        }

        if (applyDTO.getUserName() == null || applyDTO.getUserName().isBlank()) {
            throw new Exception400(applyDTO.getUserName(), "이름을 입력해주세요.");
        }

        if (applyDTO.getUserPhoneNumber() != null && !applyDTO.getUserPhoneNumber().matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$")) {
            throw new Exception400(applyDTO.getUserPhoneNumber(), "유효하지 않은 휴대폰 번호 형식입니다.");
        }

        if (applyDTO.getUserEmail() != null && !applyDTO.getUserEmail().matches("^(?=.{1,30}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new Exception400(applyDTO.getUserEmail(), "유효하지 않은 이메일 형식입니다.");
        }

        reservationService.addReservation(pbId, applyDTO, myUserDetails.getMember().getId());

        return new ResponseDTO();
    }

    @Log
    @ApiOperation(value = "예약 변경하기")
    @SwaggerResponses.DefaultApiResponses
    @PatchMapping("/pb/reservation/{id}")
    public ResponseDTO updateReservation(@PathVariable Long id,
                                         @RequestBody ReservationRequest.UpdateDTO updateDTO,
                                         @AuthenticationPrincipal MyUserDetails myUserDetails) {
        if (updateDTO.getTime() == null || updateDTO.getTime().isBlank()) {
            throw new Exception400(null, "상담 시간을 확정한 뒤 요청해주세요.");
        }

        // 현재 시간보다 이전 날짜인지 확인
        if (!updateDTO.getTime().matches("^\\d{4}년 \\d{1,2}월 \\d{1,2}일 (오전|오후) \\d{1,2}시 \\d{1,2}분$")) {
            throw new Exception400(updateDTO.getTime(), "형식에 맞춰 입력해주세요.");
        }

        if (StringToLocalDateTime(updateDTO.getTime()).isBefore(LocalDateTime.now())) {
            throw new Exception400(updateDTO.getTime(), "현재 시간보다 이전 날짜는 선택할 수 없습니다.");
        }

        if (updateDTO.getType() != null && !isValidReservationType(updateDTO.getType())) {
            throw new Exception400(updateDTO.getType().toString(), "Enum 형식에 맞춰 요청해주세요.");
        }

        if (updateDTO.getType().equals(ReservationType.VISIT)) {
            if (!isValidLocationType(updateDTO.getCategory())) {
                throw new Exception400(updateDTO.getCategory().toString(), "Enum 형식에 맞춰 요청해주세요.");
            }
        }

        reservationService.updateReservation(id, updateDTO, myUserDetails.getMember().getId());

        return new ResponseDTO<>();
    }

    @Log
    @ApiOperation(value = "예약 취소하기")
    @SwaggerResponses.ApiResponsesWithout400
    @DeleteMapping("/auth/reservation/{id}")
    public ResponseDTO cancelReservation(@PathVariable Long id,
                                         @AuthenticationPrincipal MyUserDetails myUserDetails) {
        reservationService.cancelReservation(id, myUserDetails);

        return new ResponseDTO<>();
    }

    @Log
    @ApiOperation(value = "예약 확정하기")
    @SwaggerResponses.DefaultApiResponses
    @PatchMapping("/pb/reservation/{id}/confirmed")
    public ResponseDTO confirmReservation(@PathVariable Long id,
                                          @Valid @RequestBody ReservationRequest.ConfirmDTO confirmDTO, Errors errors,
                                          @AuthenticationPrincipal MyUserDetails myUserDetails) {
        reservationService.confirmReservation(id, myUserDetails.getMember().getId(), confirmDTO);

        return new ResponseDTO<>();
    }

    @Log
    @ApiOperation(value = "예약 완료하기")
    @SwaggerResponses.ApiResponsesWithout400
    @PatchMapping("/auth/reservation/{id}/completed")
    public ResponseDTO completeReservation(@PathVariable Long id,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails) {
        reservationService.completeReservation(id, myUserDetails);

        return new ResponseDTO<>();
    }

    @Log
    @ApiOperation(value = "월별/일별 예약 정보 조회")
    @SwaggerResponses.ApiResponsesWithout400
    @GetMapping("/pb/reservation")
    public ResponseDTO<List<ReservationResponse.ReservationInfoDTO>> getReservationsByDate(@RequestParam(defaultValue = "0") int year,
                                                                                           @RequestParam(defaultValue = "0") int month,
                                                                                           @AuthenticationPrincipal MyUserDetails myUserDetails) {
        if (year == 0) {
            year = LocalDateTime.now().getYear();
        } else if (year < LocalDateTime.now().getYear() - 5 || year > LocalDateTime.now().getYear() + 5) { // 현재 연도를 기준으로 +-5 이내의 값만 허용
            throw new Exception400(String.valueOf(year), "현재 연도를 기준으로 +-5 사이의 값만 입력해주세요.");
        }
        if (month == 0) {
            month = LocalDateTime.now().getMonth().getValue();
        } else if (month < 1 || month > 12) { // 1 ~ 12 사이의 값만 허용
            throw new Exception400(String.valueOf(month), "1 ~ 12 사이의 값만 입력해주세요.");
        }

        List<ReservationResponse.ReservationInfoDTO> reservations = reservationService.getReservationsByDate(year, month, myUserDetails.getMember().getId());

        return new ResponseDTO<>(reservations);
    }

    @Log
    @ApiOperation(value = "PB 상담시간 및 메시지 변경하기")
    @SwaggerResponses.DefaultApiResponses
    @PostMapping("/pb/consultTime")
    public ResponseDTO updateConsultTime(@RequestBody ReservationRequest.UpdateTimeDTO updateTimeDTO,
                                         @AuthenticationPrincipal MyUserDetails myUserDetails) {
        if (updateTimeDTO.getConsultStart() != null
                && !updateTimeDTO.getConsultStart().isBlank()
                && updateTimeDTO.getConsultEnd() != null
                && !updateTimeDTO.getConsultEnd().isBlank()) {
            if (StringToLocalTime(updateTimeDTO.getConsultStart()).isAfter((StringToLocalTime(updateTimeDTO.getConsultEnd())))) {
                throw new Exception400(updateTimeDTO.getConsultStart(), "상담 시작 시간이 종료 시간보다 이전이어야 합니다.");
            }
        }

        if (updateTimeDTO.getConsultNotice() != null && !updateTimeDTO.getConsultNotice().isBlank()) {
            if (updateTimeDTO.getConsultNotice().length() > 100) {
                throw new Exception400(updateTimeDTO.getConsultNotice(), "최대 100자까지 입력 가능합니다.");
            }
        }

        reservationService.updateConsultTime(updateTimeDTO, myUserDetails.getMember().getId());

        return new ResponseDTO<>();
    }

    // 현재 나의 상담 가능 시간 불러오기
    @Log
    @SwaggerResponses.GetMyConsultTime
    @GetMapping("/pb/consultTime")
    public ResponseDTO<ReservationResponse.MyConsultTimeDTO> getMyConsultTime(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.MyConsultTimeDTO myConsultTimeDTO = reservationService.getMyConsultTime(myUserDetails.getMember().getId());
        return new ResponseDTO<>(myConsultTimeDTO);
    }

    @Log
    @ApiOperation(value = "상담 후기 리스트 조회")
    @SwaggerResponses.ApiResponsesWithout400
    @GetMapping("/pb/reviews")
    public ResponseDTO<PageDTO<ReservationResponse.ReviewDTO>> getReviews(@RequestParam(defaultValue = "0") int page,
                                                                          @AuthenticationPrincipal MyUserDetails myUserDetails) {
        PageDTO<ReservationResponse.ReviewDTO> reviewsDTO = reservationService.getReviews(myUserDetails.getMember().getId(), page);

        return new ResponseDTO<>(reviewsDTO);
    }

    @ApiOperation(value = "PB 상담 후기 최신 3개 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/reviews/{pbId}")
    public ResponseDTO<List<ReviewResponse.ReviewOutDTO>> getPBRecentReviews(@PathVariable(value = "pbId") Long id) {

        List<ReviewResponse.ReviewOutDTO> reviewDTO = reservationService.getPBRecentReviews(id);
        ReviewResponse.ReviewListOutDTO reviewListOutDTO = new ReviewResponse.ReviewListOutDTO(reviewDTO);
        return new ResponseDTO(reviewListOutDTO);
    }

    @Log
    @ApiOperation(value = "특정 PB 상담 후기 리스트 조회")
    @SwaggerResponses.ApiResponsesWithout400
    @GetMapping("/auth/reviews/{pbId}")
    public ResponseDTO<PageDTO<ReservationResponse.ReviewDTO>> getPBReviews(@RequestParam(defaultValue = "0") int page,
                                                                            @PathVariable(value = "pbId") Long pbId) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<ReservationResponse.ReviewDTO> reviewsDTO = reservationService.getPBReviews(pbId, pageable);

        return new ResponseDTO<>(reviewsDTO);
    }

    // 나의 후기 하나 가져오기
    @Log
    @SwaggerResponses.GetMyReview
    @GetMapping("/user/review/{reservationId}")
    public ResponseDTO<ReservationResponse.MyReviewDTO> getMyReview(@PathVariable Long reservationId,
                                                                    @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.MyReviewDTO myReviewDTO = reservationService.getMyReview(reservationId, myUserDetails.getMember().getId());
        return new ResponseDTO<>(myReviewDTO);
    }

    @ApiOperation(value = "PB 상담 스타일 TOP 3 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/review/style/{pbId}")
    public ResponseDTO<ReviewResponse.PBTopStyleDTO> getPBStyles(@PathVariable(value = "pbId") Long pbId) {

        ReviewResponse.PBTopStyleDTO styleDTO = reservationService.getPBStyles(pbId);

        return new ResponseDTO(styleDTO);
    }

    @Log
    @ApiOperation(value = "후기 작성하기")
    @SwaggerResponses.DefaultApiResponses
    @PostMapping("/user/review")
    public ResponseDTO<ReservationResponse.ReviewIdDTO> addReview(@Valid @RequestBody ReservationRequest.ReviewDTO reviewDTO, Errors errors,
                                                                  @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.ReviewIdDTO reviewIdDTO = reservationService.addReview(reviewDTO, myUserDetails.getMember().getId());

        return new ResponseDTO<>(reviewIdDTO);
    }
}
