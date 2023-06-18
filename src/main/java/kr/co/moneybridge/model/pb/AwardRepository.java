package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.dto.pb.PBResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AwardRepository extends JpaRepository<Award, Long> {
    @Modifying
    @Query("delete from Award a where a.pb.id = :pbId")
    void deleteByPBId(@Param("pbId") Long pbId);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$AwardOutDTO(a) FROM Award a WHERE a.pb.id = :pbId")
    List<PBResponse.AwardOutDTO> getAwards(@Param("pbId") Long pbId);
}
