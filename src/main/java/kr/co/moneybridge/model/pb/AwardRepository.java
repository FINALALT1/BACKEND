package kr.co.moneybridge.model.pb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AwardRepository extends JpaRepository<Award, Long> {
    @Modifying
    @Query("delete from Award a where a.pb.id = :pbId")
    void deleteByPBId(@Param("pbId") Long pbId);
}
