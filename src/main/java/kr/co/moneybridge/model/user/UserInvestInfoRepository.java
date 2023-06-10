package kr.co.moneybridge.model.user;

import kr.co.moneybridge.model.reservation.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserInvestInfoRepository extends JpaRepository<UserInvestInfo, Long> {
    @Modifying
    @Query("delete from UserInvestInfo u where u.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
