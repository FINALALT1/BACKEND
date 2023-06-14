package kr.co.moneybridge.model.reservation;

import kr.co.moneybridge.dto.reservation.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Modifying
    @Query("delete from Review r where r.reservation.id = :reservationId")
    void deleteByReservationId(@Param("reservationId") Long reservationId);

    @Query("select r from Review r where r.reservation.id = :reservationId")
    Optional<Review> findByReservationId(@Param("reservationId") Long reservationId);

    @Query("select rev " +
            "from Review rev " +
            "join rev.reservation res " +
            "join res.user u " +
            "where res.pb.id = :pbId and res.process = :process")
    Page<Review> findAllByPbIdAndProcess(@Param("pbId") Long pbId, @Param("process") ReservationProcess process, Pageable pageable);
}
