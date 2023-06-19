package kr.co.moneybridge.model.reservation;

import kr.co.moneybridge.dto.reservation.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query(value = "select count(r) from Reservation r where r.process = :process")
    Integer countByProcess(@Param("process") ReservationProcess process);

    @Modifying
    @Query("delete from Reservation r where r.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("delete from Reservation r where r.pb.id = :pbId")
    void deleteByPBId(@Param("pbId") Long pbId);

    @Query("select r from Reservation r where r.user.id = :userId")
    List<Reservation> findAllByUserId(@Param("userId") Long userId);

    @Query("select r from Reservation r where r.pb.id = :pbId")
    List<Reservation> findAllByPBId(@Param("pbId") Long pbId);

    @Query("select count(r) from Reservation r where r.pb.id = :pbId and r.process = :process")
    Integer countByPBIdAndProcess(@Param("pbId") Long pbId, @Param("process") ReservationProcess process);

    @Query("select count(r) from Reservation r where r.user.id = :userId and r.process = :process")
    Integer countByUserIdAndProcess(@Param("userId") Long userId, @Param("process") ReservationProcess process);

    @Query("select count(r) " +
            "from Reservation r " +
            "where r.createdAt >= current_timestamp - 1 and r.pb.id = :pbId and r.process = :process")
    Integer countRecentByPBIdAndProcess(@Param("pbId") Long pbId, @Param("process") ReservationProcess process);

    @Query("select count(r) " +
            "from Reservation r " +
            "where r.createdAt >= current_timestamp - 1 and r.user.id = :userId and r.process = :process")
    Integer countRecentByUserIdAndProcess(@Param("userId") Long userId, @Param("process") ReservationProcess process);

    @Query("select new kr.co.moneybridge.dto.reservation.ReservationResponse$RecentPagingDTO(r, u) " +
            "from Reservation r " +
            "join r.user u " +
            "where r.pb.id = :pbId and r.process = :process")
    Page<ReservationResponse.RecentPagingDTO> findAllByPbIdAndProcess(@Param("pbId") Long pbId,
                                                                      @Param("process") ReservationProcess process,
                                                                      Pageable pageable);

    @Query("select new kr.co.moneybridge.dto.reservation.ReservationResponse$RecentPagingDTO(r, u) " +
            "from Reservation r " +
            "join r.user u " +
            "where r.pb.id = :pbId and r.status = :status")
    Page<ReservationResponse.RecentPagingDTO> findAllByPbIdAndStatus(@Param("pbId") Long pbId,
                                                                     @Param("status") ReservationStatus status,
                                                                     Pageable pageable);
}
