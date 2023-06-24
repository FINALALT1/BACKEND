package kr.co.moneybridge.model.pb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PBAgreementRepository extends JpaRepository<PBAgreement, Long> {
    @Modifying
    @Query("delete from PBAgreement p where p.pb.id = :pbId")
    void deleteByPBId(@Param("pbId") Long pbId);
}
