package kr.co.moneybridge.service;

import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.exception.Exception403;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.reservation.ReservationRequest;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
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

import java.util.ArrayList;
import java.util.List;

import static kr.co.moneybridge.core.util.MyDateUtil.StringToLocalDateTime;
import static kr.co.moneybridge.core.util.MyDateUtil.localTimeToString;

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
    public ReservationResponse.BaseOutDTO getReservationBase(Long pbId, Long userId) {
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
            return new ReservationResponse.BaseOutDTO(
                    new ReservationResponse.pbInfoDTO(
                            pbPS.getName(),
                            pbPS.getBranch().getName(),
                            pbPS.getBranch().getRoadAddress(),
                            pbPS.getBranch().getLatitude(),
                            pbPS.getBranch().getLongitude()
                    ),
                    new ReservationResponse.consultInfoDTO(
                            localTimeToString(pbPS.getConsultStart()),
                            localTimeToString(pbPS.getConsultEnd()),
                            pbPS.getConsultNotice()
                    ),
                    new ReservationResponse.userInfoDTO(
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
                                 ReservationRequest.ApplyInDTO applyInDTO,
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
            if (applyInDTO.getReservationType().equals(ReservationType.VISIT)) {
                if (applyInDTO.getLocationType().equals(LocationType.BRANCH)) {
                    locationName = pbPS.getBranch().getName();
                    locationAddress = pbPS.getBranch().getRoadAddress();
                }
            }
            reservationRepository.save(Reservation.builder()
                    .user(userPS)
                    .pb(pbPS)
                    .type(applyInDTO.getReservationType())
                    .locationName(locationName)
                    .locationAddress(locationAddress)
                    .candidateTime1(StringToLocalDateTime(applyInDTO.getCandidateTime1()))
                    .candidateTime2(StringToLocalDateTime(applyInDTO.getCandidateTime2()))
                    .question(applyInDTO.getQuestion())
                    .goal(applyInDTO.getGoal())
                    .process(ReservationProcess.APPLY)
                    .investor(applyInDTO.getUserName())
                    .phoneNumber(applyInDTO.getUserPhoneNumber())
                    .email(applyInDTO.getUserEmail())
                    .status(ReservationStatus.ACTIVE)
                    .build());
        } catch (Exception e) {
            throw new Exception500("상담 예약 저장 실패 : " + e.getMessage());
        }
    }
}
