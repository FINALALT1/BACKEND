package kr.co.moneybridge.service;

import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception403;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.reservation.ReservationRequest;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
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
import java.util.List;

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
    public void applyReservation(Long pbId,
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

            reservationRepository.save(Reservation.builder()
                    .user(userPS)
                    .pb(pbPS)
                    .type(applyDTO.getReservationType())
                    .locationName(locationName)
                    .locationAddress(locationAddress)
                    .candidateTime1(StringToLocalDateTime(applyDTO.getCandidateTime1()))
                    .candidateTime2(StringToLocalDateTime(applyDTO.getCandidateTime2()))
                    .question(applyDTO.getQuestion())
                    .goal(applyDTO.getGoal())
                    .process(ReservationProcess.APPLY)
                    .investor(applyDTO.getUserName())
                    .phoneNumber(applyDTO.getUserPhoneNumber())
                    .email(applyDTO.getUserEmail())
                    .status(ReservationStatus.ACTIVE)
                    .build());
        } catch (Exception e) {
            throw new Exception500("상담 예약 저장 실패 : " + e.getMessage());
        }
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

    @MyLog
    public ReservationResponse.RecentInfoDTO getRecentReservationInfo(Long pbId) {
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );

        try {
            return new ReservationResponse.RecentInfoDTO(
                    reservationRepository
                            .countReservationByPBIdAndProcess(pbPS.getId(), ReservationProcess.APPLY),
                    reservationRepository
                            .countRecentReservationByPBIdAndProcess(pbPS.getId(), ReservationProcess.APPLY) >= 1,
                    reservationRepository
                            .countReservationByPBIdAndProcess(pbPS.getId(), ReservationProcess.CONFIRM),
                    reservationRepository
                            .countRecentReservationByPBIdAndProcess(pbPS.getId(), ReservationProcess.CONFIRM) >= 1,
                    reservationRepository
                            .countReservationByPBIdAndProcess(pbPS.getId(), ReservationProcess.COMPLETE),
                    reservationRepository
                            .countRecentReservationByPBIdAndProcess(pbPS.getId(), ReservationProcess.COMPLETE) >= 1
            );
        } catch (Exception e) {
            throw new Exception500("상담 현황 조회 실패 : " + e.getMessage());
        }
    }

    @MyLog
    public PageDTO<ReservationResponse.RecentReservationDTO> gerRecentReservations(String type, int page, Long pbId) {
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
    public ReservationResponse.DetailDTO getReservationDetail(Long reservationId, Long pbId) {
        Reservation reservationPS = reservationRepository.findById(reservationId).orElseThrow(
                () -> new Exception404("존재하지 않는 예약입니다.")
        );
        pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );

        try {
            User userPS = reservationPS.getUser();
            return new ReservationResponse.DetailDTO(
                    userPS.getId(),
                    userPS.getProfile(),
                    userPS.getName(),
                    userPS.getPhoneNumber(),
                    userPS.getEmail(),
                    reservationPS.getId(),
                    localDateTimeToStringV2(reservationPS.getCandidateTime1()),
                    localDateTimeToStringV2(reservationPS.getCandidateTime2()),
                    localDateTimeToStringV2(reservationPS.getTime()),
                    reservationPS.getLocationName(),
                    reservationPS.getLocationAddress(),
                    reservationPS.getGoal(),
                    reservationPS.getQuestion()
            );
        } catch (Exception e) {
            throw new Exception500("예약 조회 실패 : " + e.getMessage());
        }
    }

    @MyLog
    @Transactional
    public void updateReservation(Long reservationId,
                                  ReservationRequest.UpdateDTO updateDTO,
                                  MyUserDetails myUserDetails) {
        Reservation reservationPS = reservationRepository.findById(reservationId).orElseThrow(
                () -> new Exception404("존재하지 않는 예약입니다.")
        );
        if (myUserDetails.getMember().getRole().equals(Role.USER)) {
            userRepository.findById(myUserDetails.getMember().getId()).orElseThrow(
                    () -> new Exception404("존재하지 않는 투자자입니다.")
            );
        } else { // PB
            pbRepository.findById(myUserDetails.getMember().getId()).orElseThrow(
                    () -> new Exception404("존재하지 않는 PB입니다.")
            );
        }

        try {
            if (!updateDTO.getTime().isBlank()) {
                reservationPS.updateTime(StringToLocalDateTime(updateDTO.getTime()));
            }
            if (updateDTO.getType() != null) {
                reservationPS.updateType(updateDTO.getType());
            }
            if (!updateDTO.getLocationName().isBlank()) {
                reservationPS.updateLocationName(updateDTO.getLocationName());
            }
            if (!updateDTO.getLocationAddress().isBlank()) {
                reservationPS.updateLocationAddress(updateDTO.getLocationAddress());
            }
        } catch (Exception e) {
            throw new Exception500("예약 변경 실패 : " + e.getMessage());
        }
    }
}
