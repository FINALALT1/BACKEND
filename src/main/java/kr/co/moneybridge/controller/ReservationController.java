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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static kr.co.moneybridge.core.util.MyDateUtil.StringToLocalTime;
import static kr.co.moneybridge.core.util.MyEnumUtil.*;

@RequiredArgsConstructor
@RestController
public class ReservationController {
    private final ReservationService reservationService;

    // 현재 나의 상담 가능 시간 불러오기
    @MyLog
    @SwaggerResponses.GetMyConsultTime
    @GetMapping("/pb/consultTime")
    public ResponseDTO<ReservationResponse.MyConsultTimeDTO> getMyConsultTime(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.MyConsultTimeDTO myConsultTimeDTO = reservationService.getMyConsultTime(myUserDetails.getMember().getId());
        return new ResponseDTO<>(myConsultTimeDTO);
    }

    // 나의 후기 하나 가져오기
    @MyLog
    @SwaggerResponses.GetMyReview
    @GetMapping("/user/review/{id}")
    public ResponseDTO<ReservationResponse.MyReviewDTO> getMyReview(@PathVariable Long id,
                                                                    @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.MyReviewDTO myReviewDTO = reservationService.getMyReview(id, myUserDetails.getMember().getId());
        return new ResponseDTO<>(myReviewDTO);
    }

    @MyLog
    @ApiOperation(value = "상담 예약 사전 정보 조회")
    @SwaggerResponses.ApiResponsesWithout400
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user/reservation/base/{pbId}")
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

        PageDTO<ReservationResponse.RecentReservationDTO> recentReservationsDTO = reservationService.getRecentReservations(type, page, myUserDetails.getMember().getId());

        return new ResponseDTO<>(recentReservationsDTO);
    }

    @MyLog
    @ApiOperation(value = "나의 예약 페이지 상담 목록 조회")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
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

    @MyLog
    @ApiOperation(value = "예약 확인하기(PB)")
    @SwaggerResponses.ApiResponsesWithout400
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/pb/reservation/{id}")
    public ResponseDTO<ReservationResponse.DetailByPBDTO> getReservationDetail(@PathVariable Long id,
                                                                               @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.DetailByPBDTO detailByPBDTO = reservationService.getReservationDetailByPB(id, myUserDetails.getMember().getId());

        return new ResponseDTO<>(detailByPBDTO);
    }

    @MyLog
    @ApiOperation(value = "예약 확인하기(투자자)")
    @SwaggerResponses.ApiResponsesWithout400
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user/reservation/{id}")
    public ResponseDTO<ReservationResponse.DetailByUserDTO> getReservationDetailByUser(@PathVariable Long id,
                                                                                       @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.DetailByUserDTO detailByUserDTO = reservationService.getReservationDetailByUser(id, myUserDetails.getMember().getId());

        return new ResponseDTO<>(detailByUserDTO);
    }

    @MyLog
    @ApiOperation(value = "예약 변경하기")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/pb/reservation/{id}")
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
            if (!isValidLocationType(updateDTO.getCategory())) {
                throw new Exception400(updateDTO.getCategory().toString(), "Enum 형식에 맞춰 요청해주세요.");
            }
        }

        reservationService.updateReservation(id, updateDTO, myUserDetails.getMember().getId());

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
                                          @Valid @RequestBody ReservationRequest.ConfirmDTO confirmDTO, Errors errors,
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

    @MyLog
    @ApiOperation(value = "후기 작성하기")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/user/review")
    public ResponseDTO<ReservationResponse.ReviewIdDTO> writeReview(@Valid @RequestBody ReservationRequest.ReviewDTO reviewDTO, Errors errors,
                                                                    @AuthenticationPrincipal MyUserDetails myUserDetails) {
        ReservationResponse.ReviewIdDTO reviewIdDTO = reservationService.writeReview(reviewDTO, myUserDetails.getMember().getId());

        return new ResponseDTO<>(reviewIdDTO);
    }

    @ApiOperation(value = "PB 상담후기 최신 3개 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/reviews/{pbId}")
    public ResponseDTO<List<ReviewResponse.ReviewOutDTO>> getPBReviews(@PathVariable(value = "pbId") Long id) {

        List<ReviewResponse.ReviewOutDTO> reviewDTO = reservationService.getPBReviews(id);
        ReviewResponse.ReviewListOutDTO reviewListOutDTO = new ReviewResponse.ReviewListOutDTO(reviewDTO);
        return new ResponseDTO(reviewListOutDTO);
    }


    @ApiOperation(value = "PB 상담스타일 탑3 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/review/style/{pbId}")
    public ResponseDTO<ReviewResponse.PBTopStyleDTO> getPBStyles(@PathVariable(value = "pbId") Long pbId) {

        ReviewResponse.PBTopStyleDTO styleDTO = reservationService.getPBStyles(pbId);

        return new ResponseDTO(styleDTO);
    }

    @MyLog
    @ApiOperation(value = "월별/일별 예약 정보 조회")
    @SwaggerResponses.ApiResponsesWithout400
    @ResponseStatus(HttpStatus.OK)
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

    @MyLog
    @ApiOperation(value = "PB 상담시간 및 메시지 변경하기")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
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

    @MyLog
    @ApiOperation(value = "특정 PB 상담 후기 리스트 조회")
    @SwaggerResponses.ApiResponsesWithout400
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/auth/reviews/{pbId}")
    public ResponseDTO<PageDTO<ReservationResponse.ReviewDTO>> getPbReviews(@RequestParam(defaultValue = "0") int page,
                                                                            @PathVariable(value = "pbId") Long pbId) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<ReservationResponse.ReviewDTO> reviewsDTO = reservationService.getPbReviewList(pbId, pageable);

        return new ResponseDTO<>(reviewsDTO);
    }
}
