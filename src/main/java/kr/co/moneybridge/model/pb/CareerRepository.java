package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.model.user.UserAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CareerRepository extends JpaRepository<Career, Long> {
    @Modifying
    @Query("delete from Career c where c.pb.id = :pbId")
    void deleteByPBId(@Param("pbId") Long pbId);
}
