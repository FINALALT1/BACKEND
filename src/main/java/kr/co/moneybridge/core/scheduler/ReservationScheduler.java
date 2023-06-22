package kr.co.moneybridge.core.scheduler;

import kr.co.moneybridge.model.reservation.ReservationProcess;
import kr.co.moneybridge.model.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReservationScheduler {
    private final ReservationRepository reservationRepository;

    // 상담일로부터 3일이 지난 예약은 상담완료 처리
    // 매일 0시에 실행
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateReservationProcess() {
        try {
            reservationRepository.findAllByTimeBeforeAndProcess(LocalDateTime.now().minusHours(72), ReservationProcess.CONFIRM)
                    .forEach(reservation -> {
                        reservation.updateProcess(ReservationProcess.COMPLETE);
                        reservationRepository.save(reservation);
                    });
        } catch (Exception e) {
            log.error("스케쥴러 작업 실패 : " + e.getMessage());
        }
    }
}
