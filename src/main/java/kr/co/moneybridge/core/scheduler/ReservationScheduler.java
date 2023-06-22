package kr.co.moneybridge.core.scheduler;

import kr.co.moneybridge.model.reservation.ReservationProcess;
import kr.co.moneybridge.model.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class ReservationScheduler {
    private final ReservationRepository reservationRepository;

    // 상담일로부터 3일이 지난 예약은 상담완료 처리
    // 1시간 주기로 실행
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void updateReservationProcess() {
        reservationRepository.findAllByTimeBeforeAndProcess(LocalDateTime.now().minusHours(72), ReservationProcess.CONFIRM)
                .forEach(reservation -> {
                    reservation.updateProcess(ReservationProcess.COMPLETE);
                    reservationRepository.save(reservation);
                });
    }
}
