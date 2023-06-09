package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.dto.pb.PBResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

public interface PBRepository extends JpaRepository<PB, Long> {
    @Query("select p from PB p where p.email = :email")
    Optional<PB> findByEmail(@Param("email") String email);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) FROM PB pb " +
            "JOIN Branch b ON pb.branch = b " +
            "JOIN Company c ON b.company = c " +
            "JOIN UserBookmark ub ON ub.pb = pb " +
            "WHERE ub.user.id = :userId")
    Page<PBResponse.PBPageDTO> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.pb.id = :pbId")
    Integer countReservationsByPbId(@Param("pbId") Long pbId);

    @Query("SELECT COUNT(rv) FROM Review rv JOIN Reservation r ON rv.reservation = r WHERE r.pb.id = :pbId")
    Integer countReviewsByPbId(@Param("pbId") Long pbId);
}
