package kr.co.moneybridge.model.reservation;

import kr.co.moneybridge.dto.reservation.ReservationResponse;
import kr.co.moneybridge.dto.reservation.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(value = "select count(r) from Review r where r.reservation.pb.id = :pbId")
    Long countByPBId(@Param("pbId") Long pbId);

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

    @Query("select count(r) " +
            "from Review r " +
            "where r.reservation.id = :reservationId")
    Integer countByReservationId(@Param("reservationId") Long reservationId);

    @Query("SELECT new kr.co.moneybridge.dto.reservation.ReviewResponse$ReviewOutDTO(u, r) " +
            "FROM Review r " +
            "JOIN r.reservation res " +
            "JOIN res.user u " +
            "WHERE res.pb.id = :pbId " +
            "ORDER BY res.id DESC")
    List<ReviewResponse.ReviewOutDTO> findReservationsByPBId(@Param("pbId") Long pbId, Pageable pageable);
}
