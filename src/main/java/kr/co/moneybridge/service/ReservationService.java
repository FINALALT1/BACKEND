package kr.co.moneybridge.service;

import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReservationService {
    private final PBRepository pbRepository;

    @MyLog
    public ReservationResponse.ReservationBaseOutDTO getReservationBase(Long pbId) {
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );

        try {
            return new ReservationResponse.ReservationBaseOutDTO(
                    pbPS.getBranch().getName(),
                    pbPS.getBranch().getRoadAddress(),
                    pbPS.getBranch().getLatitude(),
                    pbPS.getBranch().getLongitude(),
                    MyDateUtil.localTimeToString(pbPS.getConsultStart()),
                    MyDateUtil.localTimeToString(pbPS.getConsultEnd()),
                    pbPS.getConsultNotice()
            );
        } catch (Exception e) {
            throw new Exception500("지점 조회 실패 : " + e.getMessage());
        }
    }
}
