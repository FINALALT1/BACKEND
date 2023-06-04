package kr.co.moneybridge.model.user;

import kr.co.moneybridge.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {
}
