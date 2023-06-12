package kr.co.moneybridge.service;

import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception403;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.core.util.MyDateUtil;
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

import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReservationService {
    private final PBRepository pbRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    @MyLog
    public ReservationResponse.ReservationBaseOutDTO getReservationBase(Long pbId, MyUserDetails myUserDetails) {
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );
        if (pbPS.getStatus().equals(PBStatus.PENDING)) {
            throw new Exception403("승인 대기 중인 PB입니다.");
        }
        User userPS = userRepository.findById(myUserDetails.getMember().getId()).orElseThrow(
                () -> new Exception404("존재하지 않는 투자자입니다.")
        );

        try {
            return new ReservationResponse.ReservationBaseOutDTO(
                    new ReservationResponse.pbInfoDTO(
                            pbPS.getName(),
                            pbPS.getBranch().getName(),
                            pbPS.getBranch().getRoadAddress(),
                            pbPS.getBranch().getLatitude(),
                            pbPS.getBranch().getLongitude()
                    ),
                    new ReservationResponse.consultInfoDTO(
                            MyDateUtil.localTimeToString(pbPS.getConsultStart()),
                            MyDateUtil.localTimeToString(pbPS.getConsultEnd()),
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
                                 ReservationRequest.ApplyReservationInDTO applyReservationInDTO,
                                 MyUserDetails myUserDetails) {
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );
        if (pbPS.getStatus().equals(PBStatus.PENDING)) {
            throw new Exception403("승인 대기 중인 PB입니다.");
        }
        User userPS = userRepository.findById(myUserDetails.getMember().getId()).orElseThrow(
                () -> new Exception404("존재하지 않는 투자자입니다.")
        );

        try {
            reservationRepository.save(Reservation.builder()
                    .user(userPS)
                    .pb(pbPS)
                    .type(applyReservationInDTO.getReservationType())
                    .locationName(applyReservationInDTO.getLocationName())
                    .locationAddress(applyReservationInDTO.getLocationAddress())
                    .candidateTime1(MyDateUtil.StringToLocalDateTime(applyReservationInDTO.getCandidateTime1()))
                    .candidateTime2(MyDateUtil.StringToLocalDateTime(applyReservationInDTO.getCandidateTime2()))
                    .question(applyReservationInDTO.getQuestion())
                    .goal1(applyReservationInDTO.getGoal1())
                    .goal2(applyReservationInDTO.getGoal2())
                    .process(ReservationProcess.APPLY)
                    .investor(applyReservationInDTO.getUserName())
                    .phoneNumber(applyReservationInDTO.getUserPhoneNumber())
                    .email(applyReservationInDTO.getUserEmail())
                    .status(ReservationStatus.ACTIVE)
                    .build());
        } catch (Exception e) {
            throw new Exception500("상담 예약 저장 실패 : " + e.getMessage());
        }
    }

    @MyLog
    public PageDTO<ReservationResponse.ReviewDTO> getReviews(Long pbId, int page) {
        pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );

        try {
            // 페이징
            Page<ReservationResponse.ReviewDTO> reveiwPG = reviewRepository
                    .findAll(pbId,
                            ReservationProcess.COMPLETE,
                            PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
            // 응답
            return new PageDTO<>(
                    reveiwPG.stream().collect(Collectors.toList()), reveiwPG
            );
        } catch (Exception e) {
            throw new Exception500("상담 후기 목록 조회 실패 : " + e.getMessage());
        }
    }
}
