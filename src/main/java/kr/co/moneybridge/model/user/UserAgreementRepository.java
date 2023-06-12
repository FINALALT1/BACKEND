package kr.co.moneybridge.model.user;

import kr.co.moneybridge.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {
    @Modifying
    @Query("delete from UserAgreement u where u.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
