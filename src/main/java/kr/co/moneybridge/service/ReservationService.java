package kr.co.moneybridge.service;

import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.*;
import kr.co.moneybridge.core.util.BizMessageUtil;
import kr.co.moneybridge.core.util.Template;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.reservation.ReservationRequest;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
import kr.co.moneybridge.dto.reservation.ReviewResponse;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBRepository;
import kr.co.moneybridge.model.pb.PBStatus;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static kr.co.moneybridge.core.util.MyDateUtil.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReservationService {
    private final PBRepository pbRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final StyleRepository styleRepository;
    private final BizMessageUtil biz;

    @MyLog
    public ReservationResponse.RecentInfoDTO getRecentReservationInfo(Long pbId) {
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );

        try {
            return new ReservationResponse.RecentInfoDTO(
                    reservationRepository
                            .countByPBIdAndProcess(pbPS.getId(), ReservationProcess.APPLY),
                    reservationRepository
                            .countRecentByPBIdAndProcess(LocalDateTime.now().minusDays(1), pbPS.getId(), ReservationProcess.APPLY) >= 1,
                    reservationRepository
                            .countByPBIdAndProcess(pbPS.getId(), ReservationProcess.CONFIRM),
                    reservationRepository
                            .countRecentByPBIdAndProcess(LocalDateTime.now().minusDays(1), pbPS.getId(), ReservationProcess.CONFIRM) >= 1,
                    reservationRepository
                            .countByPBIdAndProcess(pbPS.getId(), ReservationProcess.COMPLETE),
                    reservationRepository
                            .countRecentByPBIdAndProcess(LocalDateTime.now().minusDays(1), pbPS.getId(), ReservationProcess.COMPLETE) >= 1
            );
        } catch (Exception e) {
            throw new Exception500("상담 현황 조회 실패 : " + e.getMessage());
        }
    }

    @MyLog
    public ReservationResponse.RecentInfoDTO getRecentReservationInfoByUser(Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception404("존재하지 않는 투자자입니다.")
        );

        try {
            return new ReservationResponse.RecentInfoDTO(
                    reservationRepository
                            .countByUserIdAndProcess(userPS.getId(), ReservationProcess.APPLY),
                    reservationRepository
                            .countRecentByUserIdAndProcess(LocalDateTime.now().minusDays(1), userPS.getId(), ReservationProcess.APPLY) >= 1,
                    reservationRepository
                            .countByUserIdAndProcess(userPS.getId(), ReservationProcess.CONFIRM),
                    reservationRepository
                            .countRecentByUserIdAndProcess(LocalDateTime.now().minusDays(1), userPS.getId(), ReservationProcess.CONFIRM) >= 1,
                    reservationRepository
                            .countByUserIdAndProcess(userPS.getId(), ReservationProcess.COMPLETE),
                    reservationRepository
                            .countRecentByUserIdAndProcess(LocalDateTime.now().minusDays(1), userPS.getId(), ReservationProcess.COMPLETE) >= 1
            );
        } catch (Exception e) {
            throw new Exception500("상담 현황 조회 실패 : " + e.getMessage());
        }
    }

    @MyLog
    public PageDTO<ReservationResponse.RecentReservationDTO> getRecentReservations(String type, int page, Long pbId) {
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );

        try {
            // 페이징
            Page<ReservationResponse.RecentPagingDTO> reservations = null;
            Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "createdAt");
            if (type.equals("APPLY")) {
                reservations = reservationRepository
                        .findAllByPbIdAndProcess(pbPS.getId(),
                                ReservationProcess.APPLY,
                                pageable
                        );
            } else if (type.equals("CONFIRM")) {
                reservations = reservationRepository
                        .findAllByPbIdAndProcess(pbPS.getId(),
                                ReservationProcess.CONFIRM,
                                pageable
                        );
            } else if (type.equals("COMPLETE")) {
                reservations = reservationRepository
                        .findAllByPbIdAndProcess(pbPS.getId(),
                                ReservationProcess.COMPLETE,
                                pageable
                        );
            } else { // WITHDRAW
                reservations = reservationRepository
                        .findAllByPbIdAndStatus(pbPS.getId(),
                                ReservationStatus.CANCEL,
                                pageable
                        );
            }

            List<ReservationResponse.RecentReservationDTO> reservationDTOs = new ArrayList<>();
            for (ReservationResponse.RecentPagingDTO reservation : reservations) {
                reservationDTOs.add(
                        new ReservationResponse.RecentReservationDTO(
                                reservation.getReservationId(),
                                Duration.between(LocalDateTime.now().minusHours(24),
                                        reservation.getCreatedAt()).toHours() <= 24,
                                reservation.getUserId(),
                                reservation.getProfileImage(),
                                reservation.getName(),
                                localDateTimeToStringV2(reservation.getCreatedAt()),
                                reservation.getType()
                        )
                );
            }

            return new PageDTO<>(
                    reservationDTOs, reservations, ReservationResponse.RecentPagingDTO.class
            );
        } catch (Exception e) {
            throw new Exception500("상담 목록 조회 실패 : " + e.getMessage());
        }
    }

    @MyLog
    public PageDTO<ReservationResponse.RecentReservationByUserDTO> getRecentReservationsByUser(String type, int page, Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );

        try {
            // 페이징
            Page<ReservationResponse.RecentPagingByUserDTO> reservations = null;
            Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "createdAt");
            if (type.equals("APPLY")) {
                reservations = reservationRepository
                        .findAllByUserIdAndProcess(userPS.getId(),
                                ReservationProcess.APPLY,
                                pageable
                        );
            } else if (type.equals("CONFIRM")) {
                reservations = reservationRepository
                        .findAllByUserIdAndProcess(userPS.getId(),
                                ReservationProcess.CONFIRM,
                                pageable
                        );
            } else if (type.equals("COMPLETE")) {
                reservations = reservationRepository
                        .findAllByUserIdAndProcess(userPS.getId(),
                                ReservationProcess.COMPLETE,
                                pageable
                        );
            } else { // WITHDRAW
                reservations = reservationRepository
                        .findAllByUserIdAndStatus(userPS.getId(),
                                ReservationStatus.CANCEL,
                                pageable
                        );
            }

            List<ReservationResponse.RecentReservationByUserDTO> reservationDTOs = new ArrayList<>();
            for (ReservationResponse.RecentPagingByUserDTO reservation : reservations) {
                reservationDTOs.add(
                        new ReservationResponse.RecentReservationByUserDTO(
                                reservation.getReservationId(),
                                Duration.between(LocalDateTime.now().minusHours(24),
                                        reservation.getCreatedAt()).toHours() <= 24,
                                reservation.getPbId(),
                                reservation.getProfileImage(),
                                reservation.getName(),
                                localDateTimeToStringV2(reservation.getCreatedAt()),
                                reservation.getType()
                        )
                );
            }

            return new PageDTO<>(
                    reservationDTOs, reservations, ReservationResponse.RecentPagingByUserDTO.class
            );
        } catch (Exception e) {
            throw new Exception500("상담 목록 조회 실패 : " + e.getMessage());
        }
    }

    @MyLog
    public ReservationResponse.DetailByPBDTO getReservationDetail(Long reservationId, Long pbId) {
        Reservation reservationPS = reservationRepository.findById(reservationId).orElseThrow(
                () -> new Exception404("존재하지 않는 예약입니다.")
        );
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );

        try {
            User userPS = reservationPS.getUser();
            return new ReservationResponse.DetailByPBDTO(
                    userPS.getId(),
                    userPS.getProfile(),
                    userPS.getName(),
                    userPS.getPhoneNumber(),
                    userPS.getEmail(),
                    reservationPS.getId(),
                    localDateTimeToStringV2(reservationPS.getCandidateTime1()),
                    localDateTimeToStringV2(reservationPS.getCandidateTime2()),
                    localDateTimeToStringV2(reservationPS.getTime()),
                    reservationPS.getType(),
                    reservationPS.getLocationName(),
                    reservationPS.getLocationAddress(),
                    reservationPS.getGoal(),
                    reservationPS.getQuestion(),
                    (pbPS.getConsultStart() == null) ? null : pbPS.getConsultStart().toString(),
                    (pbPS.getConsultEnd() == null) ? null : pbPS.getConsultEnd().toString(),
                    pbPS.getConsultNotice(),
                    reviewRepository.countByReservationId(reservationPS.getId()) >= 1
            );
        } catch (Exception e) {
            throw new Exception500("예약 조회 실패 : " + e.getMessage());
        }
    }

    @MyLog
    public ReservationResponse.DetailByUserDTO getReservationDetailByUser(Long reservationId, Long userId) {
        Reservation reservationPS = reservationRepository.findById(reservationId).orElseThrow(
                () -> new Exception404("존재하지 않는 예약입니다.")
        );
        userRepository.findById(userId).orElseThrow(
                () -> new Exception404("존재하지 않는 투자자입니다.")
        );

        try {
            PB pbPS = reservationPS.getPb();
            return new ReservationResponse.DetailByUserDTO(
                    pbPS.getId(),
                    pbPS.getProfile(),
                    pbPS.getName(),
                    pbPS.getPhoneNumber(),
                    pbPS.getEmail(),
                    reservationPS.getId(),
                    localDateTimeToStringV2(reservationPS.getCandidateTime1()),
                    localDateTimeToStringV2(reservationPS.getCandidateTime2()),
                    localDateTimeToStringV2(reservationPS.getTime()),
                    reservationPS.getType(),
                    reservationPS.getLocationName(),
                    reservationPS.getLocationAddress(),
                    reservationPS.getGoal(),
                    reservationPS.getQuestion(),
                    pbPS.getConsultStart().toString(),
                    pbPS.getConsultEnd().toString(),
                    pbPS.getConsultNotice(),
                    reviewRepository.countByReservationId(reservationPS.getId()) >= 1
            );
        } catch (Exception e) {
            throw new Exception500("예약 조회 실패 : " + e.getMessage());
        }
    }

    @MyLog
    public ReservationResponse.BaseDTO getReservationBase(Long pbId, Long userId) {
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );
        if (pbPS.getStatus().equals(PBStatus.PENDING)) {
            throw new Exception403("승인 대기 중인 PB입니다.");
        }
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception404("존재하지 않는 투자자입니다.")
        );

        try {
            return new ReservationResponse.BaseDTO(
                    new ReservationResponse.PBInfoDTO(
                            pbPS.getName(),
                            pbPS.getBranch().getName(),
                            pbPS.getBranch().getRoadAddress(),
                            pbPS.getBranch().getLatitude(),
                            pbPS.getBranch().getLongitude()
                    ),
                    new ReservationResponse.ConsultInfoDTO(
                            localTimeToString(pbPS.getConsultStart()),
                            localTimeToString(pbPS.getConsultEnd()),
                            pbPS.getConsultNotice()
                    ),
                    new ReservationResponse.UserInfoDTO(
                            userPS.getName(),
                            userPS.getPhoneNumber(),
                            userPS.getEmail()
                    )
            );
        } catch (Exception e) {
            throw new Exception500("지점 조회 실패 : " + e.getMessage());
        }
    }

    @MyLog
    @Transactional
    public void addReservation(Long pbId,
                               ReservationRequest.ApplyDTO applyDTO,
                               Long userId) {
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );
        if (pbPS.getStatus().equals(PBStatus.PENDING)) {
            throw new Exception403("승인 대기 중인 PB입니다.");
        }
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception404("존재하지 않는 투자자입니다.")
        );

        Reservation reservationPS = null;
        try {
            String locationName = null;
            String locationAddress = null;
            // 방문상담
            if (applyDTO.getReservationType().equals(ReservationType.VISIT)) {
                if (applyDTO.getLocationType().equals(LocationType.BRANCH)) {
                    locationName = pbPS.getBranch().getName();
                    locationAddress = pbPS.getBranch().getRoadAddress();
                }
            }

            reservationPS = reservationRepository.save(Reservation.builder()
                    .user(userPS)
                    .pb(pbPS)
                    .type(applyDTO.getReservationType())
                    .locationName(locationName)
                    .locationAddress(locationAddress)
                    .candidateTime1(applyDTO.getCandidateTime1())
                    .candidateTime2(applyDTO.getCandidateTime2())
                    .question(applyDTO.getQuestion())
                    .goal(applyDTO.getGoal())
                    .process(ReservationProcess.APPLY)
                    .investor(applyDTO.getUserName())
                    .phoneNumber(applyDTO.getUserPhoneNumber())
                    .email(applyDTO.getUserEmail())
                    .status(ReservationStatus.ACTIVE)
                    .build());

            if (!userPS.getHasDoneReservation()) {
                userPS.updateHasDoneReservation(true);
            }
        } catch (Exception e) {
            throw new Exception500("상담 예약 저장 실패 : " + e.getMessage());
        }

        // 담당 PB에게 알림톡 발신
        biz.sendWebLinkNotification(
                pbPS.getPhoneNumber(),
                Template.ADD_RESERVATION,
                biz.getTempMsg001(
                        pbPS.getName(),
                        userPS.getName(),
                        reservationPS
                )
        );
    }

    @MyLog
    @Transactional
    public void updateReservation(Long reservationId,
                                  ReservationRequest.UpdateDTO updateDTO,
                                  Long pbId) {
        Reservation reservationPS = reservationRepository.findById(reservationId).orElseThrow(
                () -> new Exception404("존재하지 않는 예약입니다.")
        );
        if (reservationPS.getStatus().equals(ReservationStatus.CANCEL)
                || reservationPS.getProcess().equals(ReservationProcess.COMPLETE)) {
            throw new Exception400(String.valueOf(reservationId), "이미 완료되었거나 취소된 상담입니다.");
        }
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );

        try {
            reservationPS.updateTime(StringToLocalDateTime(updateDTO.getTime()));
            if (updateDTO.getType() != null) {
                reservationPS.updateType(updateDTO.getType());
            }
            if (updateDTO.getCategory() != null) {
                if (updateDTO.getCategory().equals(LocationType.BRANCH)) {
                    reservationPS.updateLocationName(pbPS.getBranch().getName());
                    reservationPS.updateLocationAddress(pbPS.getBranch().getRoadAddress());
                } else { // CALL
                    reservationPS.updateLocationName(null);
                    reservationPS.updateLocationAddress(null);
                }
            }
            reservationPS.updateProcess(ReservationProcess.CONFIRM); // 예약 변경시 확정 처리
            reservationPS.updateCreatedAt(); // isNewApply 등의 변수가 제대로 표시되도록 하기 위해 예약의 상태가 변할 때 createdAt도 변경
        } catch (Exception e) {
            throw new Exception500("예약 변경 실패 : " + e.getMessage());
        }
    }

    @MyLog
    @Transactional
    public void cancelReservation(Long reservationId, MyUserDetails myUserDetails) {
        Reservation reservationPS = reservationRepository.findById(reservationId).orElseThrow(
                () -> new Exception404("존재하지 않는 예약입니다.")
        );
        if (reservationPS.getStatus().equals(ReservationStatus.CANCEL)
                || reservationPS.getProcess().equals(ReservationProcess.COMPLETE)) {
            throw new Exception400(String.valueOf(reservationId), "이미 완료되었거나 취소된 상담입니다.");
        }
        if (myUserDetails.getMember().getRole().equals(Role.PB)) {
            pbRepository.findById(myUserDetails.getMember().getId()).orElseThrow(
                    () -> new Exception404("존재하지 않는 PB입니다.")
            );
        } else { // PB
            userRepository.findById(myUserDetails.getMember().getId()).orElseThrow(
                    () -> new Exception404("존재하지 않는 투자자입니다.")
            );
        }

        try {
            if (reservationPS.getStatus().equals(ReservationStatus.ACTIVE)) {
                reservationPS.updateStatus(ReservationStatus.CANCEL);
            }
        } catch (Exception e) {
            throw new Exception500("예약 취소 실패 : " + e.getMessage());
        }

        // 알림톡 발신
        if (myUserDetails.getMember().getRole().equals(Role.PB)) {
            // 대상 투자자에게 발신
            biz.sendNotification(
                    reservationPS.getUser().getPhoneNumber(),
                    Template.CANCEL_RESERVATION_BY_PB,
                    biz.getTempMsg002(
                            reservationPS.getUser().getName(),
                            reservationPS.getPb().getName(),
                            reservationPS.getCreatedAt()
                    )
            );
        } else {
            // 대상 PB에게 발신
            biz.sendNotification(
                    reservationPS.getPb().getPhoneNumber(),
                    Template.CANCEL_RESERVATION_BY_USER,
                    biz.getTempMsg003(
                            reservationPS.getPb().getName(),
                            reservationPS.getUser().getName(),
                            reservationPS.getCreatedAt()
                    )
            );
        }
    }

    @MyLog
    @Transactional
    public void confirmReservation(Long reservationId, Long pbId, ReservationRequest.ConfirmDTO confirmDTO) {
        Reservation reservationPS = reservationRepository.findById(reservationId).orElseThrow(
                () -> new Exception404("존재하지 않는 예약입니다.")
        );
        if (reservationPS.getStatus().equals(ReservationStatus.CANCEL)
                || !reservationPS.getProcess().equals(ReservationProcess.APPLY)) {
            throw new Exception400(String.valueOf(reservationId), "이미 확정, 혹은 완료되었거나 취소된 상담입니다.");
        }
        pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );

        try {
            reservationPS.updateTime(StringToLocalDateTime(confirmDTO.getTime()));
            reservationPS.updateProcess(ReservationProcess.CONFIRM);
            reservationPS.updateCreatedAt(); // isNewApply 등의 변수가 제대로 표시되도록 하기 위해 예약의 상태가 변할 때 createdAt도 변경
        } catch (Exception e) {
            throw new Exception500("예약 확정 실패 : " + e.getMessage());
        }

        // 대상 투자자에게 알림톡 발신
        biz.sendNotification(
                reservationPS.getUser().getPhoneNumber(),
                Template.CONFIRM_RESERVATION,
                biz.getTempMsg004(
                        reservationPS.getUser().getName(),
                        reservationPS.getPb().getName(),
                        reservationPS
                )
        );
    }

    @MyLog
    @Transactional
    public void completeReservation(Long reservationId, MyUserDetails myUserDetails) {
        Reservation reservationPS = reservationRepository.findById(reservationId).orElseThrow(
                () -> new Exception404("존재하지 않는 예약입니다.")
        );
        if (reservationPS.getStatus().equals(ReservationStatus.CANCEL)
                || reservationPS.getProcess().equals(ReservationProcess.COMPLETE)
                || reservationPS.getProcess().equals(ReservationProcess.APPLY)) {
            throw new Exception400(String.valueOf(reservationId), "아직 확정되지 않았거나 완료 혹은 취소된 상담입니다.");
        }
        if (myUserDetails.getMember().getRole().equals(Role.PB)) {
            pbRepository.findById(myUserDetails.getMember().getId()).orElseThrow(
                    () -> new Exception404("존재하지 않는 PB입니다.")
            );
        } else { // USER, ADMIN
            userRepository.findById(myUserDetails.getMember().getId()).orElseThrow(
                    () -> new Exception404("존재하지 않는 투자자입니다.")
            );
        }

        try {
            reservationPS.updateProcess(ReservationProcess.COMPLETE);
            reservationPS.updateCreatedAt(); // isNewApply 등의 변수가 제대로 표시되도록 하기 위해 예약의 상태가 변할 때 createdAt도 변경
        } catch (Exception e) {
            throw new Exception500("예약 완료 실패 : " + e.getMessage());
        }
    }

    @MyLog
    public List<ReservationResponse.ReservationInfoDTO> getReservationsByDate(int year, int month, Long pbId) {
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );

        try {
            List<ReservationResponse.ReservationInfoDTO> reservations = reservationRepository.findAllByPbIdWithoutCancel(pbPS.getId());

            // 상담 날짜 기준 오름차순 정렬, 날짜가 같다면 시간을 기준으로 오름차순 정렬
            // 자바에서의 정렬은 오름차순이 default이므로(선행 원소가 후행 원소보다 작다.) 음수를 반환할 경우 위치를 바꾸지 않는다.
            Collections.sort(reservations, new Comparator<ReservationResponse.ReservationInfoDTO>() {
                @Override
                public int compare(ReservationResponse.ReservationInfoDTO r1, ReservationResponse.ReservationInfoDTO r2) {
                    if (r1.getDay().isEqual(r2.getDay())) {
                        return r1.getTime().compareTo(r2.getTime());
                    } else {
                        if (r1.getDay().isBefore(r2.getDay())) {
                            return -1;
                        } else if (r1.getDay().isEqual(r2.getDay())) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                }
            });

            // year, month가 요청과 일치하는 개체들만 반환
            return reservations.stream()
                    .filter(dto -> dto.getDay().getYear() == year && dto.getDay().getMonthValue() == month)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception500("예약 정보 조회 실패 : " + e.getMessage());
        }
    }

    @MyLog
    @Transactional
    public void updateConsultTime(ReservationRequest.UpdateTimeDTO updateTimeDTO, Long pbId) {
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );

        try {
            if (updateTimeDTO.getConsultStart() != null && !updateTimeDTO.getConsultStart().isBlank()) {
                pbPS.updateConsultStart(StringToLocalTime(updateTimeDTO.getConsultStart()));
            }
            if (updateTimeDTO.getConsultEnd() != null && !updateTimeDTO.getConsultEnd().isBlank()) {
                pbPS.updateConsultEnd(StringToLocalTime(updateTimeDTO.getConsultEnd()));
            }
            if (updateTimeDTO.getConsultNotice() != null && !updateTimeDTO.getConsultNotice().isBlank()) {
                pbPS.updateConsultNotice(updateTimeDTO.getConsultNotice());
            }
        } catch (Exception e) {
            throw new Exception500("상담 시간 및 메시지 변경 실패 : " + e.getMessage());
        }
    }

    @MyLog
    public ReservationResponse.MyConsultTimeDTO getMyConsultTime(Long pbId) {
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );
        if (pbPS.getStatus().equals(PBStatus.PENDING)) {
            throw new Exception403("승인 대기 중인 PB입니다.");
        }
        return new ReservationResponse.MyConsultTimeDTO(pbPS);
    }

    @MyLog
    public PageDTO<ReservationResponse.ReviewDTO> getReviews(Long pbId, int page) {
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );

        try {
            // 페이징
            Page<Review> reviews = reviewRepository.findAllByPbIdAndProcess(
                    pbPS.getId(),
                    ReservationProcess.COMPLETE,
                    PageRequest.of(page, 10, Sort.Direction.DESC, "createdAt")
            );

            List<ReservationResponse.ReviewDTO> reviewDTOs = new ArrayList<>();
            for (Review review : reviews) {
                User user = userRepository.findUserByReviewId(review.getId());
                List<Style> styles = styleRepository.findAllByReviewId(review.getId());
                reviewDTOs.add(new ReservationResponse.ReviewDTO(review, user, styles));
            }

            // 응답
            return new PageDTO<>(
                    reviewDTOs, reviews, Review.class
            );
        } catch (Exception e) {
            throw new Exception500("상담 후기 목록 조회 실패 : " + e.getMessage());
        }
    }

    // PB 상담 후기 최신 3개 가져오기
    public List<ReviewResponse.ReviewOutDTO> getPBRecentReviews(Long pbId) {

        pbRepository.findById(pbId).orElseThrow(() -> new Exception404("존재하지 않는 PB입니다."));

        List<ReviewResponse.ReviewOutDTO> list = reviewRepository.findReservationsByPBId(pbId, PageRequest.of(0, 3));
        for (ReviewResponse.ReviewOutDTO dto : list) {
            dto.setList(styleRepository.findByReviewId(dto.getReviewId()));
        }

        return list;
    }

    // 특정 PB 상담 후기 리스트 조회
    public PageDTO<ReservationResponse.ReviewDTO> getPBReviews(Long pbId, Pageable pageable) {

        PB pb = pbRepository.findById(pbId).orElseThrow(() -> new Exception404("존재하지 않는 PB입니다."));

        try {
            Page<Review> reviews = reviewRepository.findAllByPbIdAndProcess(pb.getId(), ReservationProcess.COMPLETE, pageable);

            List<ReservationResponse.ReviewDTO> reviewDTOs = new ArrayList<>();
            for (Review review : reviews) {
                User user = userRepository.findUserByReviewId(review.getId());
                List<Style> styles = styleRepository.findAllByReviewId(review.getId());
                reviewDTOs.add(new ReservationResponse.ReviewDTO(review, user, styles));
            }

            return new PageDTO<>(reviewDTOs, reviews, Review.class);
        } catch (Exception e) {
            throw new Exception500("상담 후기 목록 조회 실패 : " + e.getMessage());
        }
    }

    @MyLog
    public ReservationResponse.MyReviewDTO getMyReview(Long reservationId, Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new Exception404("존재하지 않는 투자자입니다.")
        );
        Review reviewPS = reviewRepository.findByReservationId(reservationId).orElseThrow(
                () -> new Exception200("후기가 없습니다.")
        );
        if (!reviewPS.getReservation().getUser().getId().equals(userId)) {
            throw new Exception400("reviewId", "로그인한 투자자가 작성한 리뷰가 아닙니다.");
        }
        List<ReservationResponse.StyleDTO> styleList = styleRepository.findAllByReviewId(reviewPS.getId())
                .stream().map(style -> new ReservationResponse.StyleDTO(style.getStyle()))
                .collect(Collectors.toList());
        return new ReservationResponse.MyReviewDTO(reviewPS, styleList);
    }

    // PB 상담 스타일 TOP 3 가져오기
    public ReviewResponse.PBTopStyleDTO getPBStyles(Long pbId) {

        pbRepository.findById(pbId).orElseThrow(() -> new Exception404("존재하지 않는 PB입니다."));
        ReviewResponse.PBTopStyleDTO styleDTO = new ReviewResponse.PBTopStyleDTO();

        List<StyleStyle> styleList = styleRepository.findStylesByPbId(pbId);

        if (styleList.size() == 0) {
            return styleDTO;
        } else if (styleList.size() == 1) {
            styleDTO.setStyle1(styleList.get(0));
        } else if (styleList.size() == 2) {
            styleDTO.setStyle1(styleList.get(0));
            styleDTO.setStyle2(styleList.get(1));
        } else {
            styleDTO.setStyle1(styleList.get(0));
            styleDTO.setStyle2(styleList.get(1));
            styleDTO.setStyle3(styleList.get(2));
        }

        return styleDTO;
    }

    @MyLog
    @Transactional
    public ReservationResponse.ReviewIdDTO addReview(ReservationRequest.ReviewDTO reviewDTO, Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception404("존재하지 않는 투자자입니다.")
        );
        Reservation reservationPS = reservationRepository.findById(reviewDTO.getReservationId()).orElseThrow(
                () -> new Exception404("존재하지 않는 상담입니다.")
        );
        if (reviewRepository.countByReservationId(reviewDTO.getReservationId()) == 1) {
            throw new Exception400(String.valueOf(reviewDTO.getReservationId()), "해당 상담에 이미 작성된 후기가 존재합니다.");
        }

        try {
            Review reviewPS = reviewRepository.save(
                    Review.builder()
                            .reservation(reservationPS)
                            .adherence(reviewDTO.getAdherence())
                            .content(reviewDTO.getContent())
                            .build()
            );
            for (StyleStyle style : reviewDTO.getStyleList()) {
                styleRepository.save(
                        Style.builder()
                                .review(reviewPS)
                                .style(style)
                                .build()
                );
            }
            userPS.updateHasDoneReview(true);

            return new ReservationResponse.ReviewIdDTO(reviewPS.getId());
        } catch (Exception e) {
            throw new Exception500("상담 후기 저장 실패 : " + e.getMessage());
        }
    }
}
