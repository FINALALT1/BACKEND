package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.dto.pb.PBResponse;
import kr.co.moneybridge.model.user.UserAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Long> {
    @Modifying
    @Query("delete from Career c where c.pb.id = :pbId")
    void deleteByPBId(@Param("pbId") Long pbId);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$CareerOutDTO(c) FROM Career c WHERE c.pb.id = :pbId")
    List<PBResponse.CareerOutDTO> getCareers(@Param("pbId") Long pbId);
}
