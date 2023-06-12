package kr.co.moneybridge.model.pb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    @Modifying
    @Query("delete from Portfolio p where p.pb.id = :pbId")
    void deleteByPBId(@Param("pbId") Long pbId);
}
