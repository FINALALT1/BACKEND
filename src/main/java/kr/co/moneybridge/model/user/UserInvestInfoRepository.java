package kr.co.moneybridge.model.user;

import kr.co.moneybridge.model.reservation.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInvestInfoRepository extends JpaRepository<UserInvestInfo, Long> {
}
