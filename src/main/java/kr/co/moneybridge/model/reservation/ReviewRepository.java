package kr.co.moneybridge.model.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Modifying
    @Query("delete from Review r where r.reservation.id = :reservationId")
    void deleteByReservationId(@Param("reservationId") Long reservationId);

    @Query("select r from Review r where r.reservation.id = :reservationId")
    List<Review> findAllByReservationId(@Param("reservationId") Long reservationId);
}
