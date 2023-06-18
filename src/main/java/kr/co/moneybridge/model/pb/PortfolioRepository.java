package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.dto.pb.PBResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    @Modifying
    @Query("delete from Portfolio p where p.pb.id = :pbId")
    void deleteByPBId(@Param("pbId") Long pbId);

    @Query("SELECT p FROM Portfolio p WHERE p.pb.id = :pbId")
    Optional<Portfolio> findByPbId(@Param("pbId") Long pbId);
}
