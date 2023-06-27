package kr.co.moneybridge.model.reservation;

import kr.co.moneybridge.dto.reservation.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query(value = "select count(r) from Reservation r where r.process = :process and r.status <> 'CANCEL'")
    Long countByProcess(@Param("process") ReservationProcess process);

    @Modifying
    @Query("delete from Reservation r where r.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("delete from Reservation r where r.pb.id = :pbId")
    void deleteByPBId(@Param("pbId") Long pbId);

    @Query("select r from Reservation r where r.user.id = :userId and r.status <> 'CANCEL'")
    List<Reservation> findAllByUserId(@Param("userId") Long userId);

    @Query("select r from Reservation r where r.pb.id = :pbId and r.status <> 'CANCEL'")
    List<Reservation> findAllByPBId(@Param("pbId") Long pbId);

    @Query("select count(r) from Reservation r where r.pb.id = :pbId and r.process = :process and r.status <> 'CANCEL'")
    Long countByPBIdAndProcess(@Param("pbId") Long pbId, @Param("process") ReservationProcess process);

    @Query("select count(r) from Reservation r where r.user.id = :userId and r.process = :process and r.status <> 'CANCEL'")
    Long countByUserIdAndProcess(@Param("userId") Long userId, @Param("process") ReservationProcess process);

    @Query("select count(r) " +
            "from Reservation r " +
            "where r.createdAt >= :yesterday and r.pb.id = :pbId and r.process = :process and r.status <> 'CANCEL'")
    Integer countRecentByPBIdAndProcess(@Param("yesterday") LocalDateTime yesterday, @Param("pbId") Long pbId, @Param("process") ReservationProcess process);

    @Query("select count(r) " +
            "from Reservation r " +
            "where r.createdAt >= :yesterday and r.user.id = :userId and r.process = :process and r.status <> 'CANCEL'")
    Integer countRecentByUserIdAndProcess(@Param("yesterday") LocalDateTime yesterday, @Param("userId") Long userId, @Param("process") ReservationProcess process);

    @Query("select new kr.co.moneybridge.dto.reservation.ReservationResponse$RecentPagingDTO(r, u) " +
            "from Reservation r " +
            "join r.user u " +
            "where r.pb.id = :pbId and r.process = :process and r.status <> 'CANCEL'")
    Page<ReservationResponse.RecentPagingDTO> findAllByPbIdAndProcess(@Param("pbId") Long pbId,
                                                                      @Param("process") ReservationProcess process,
                                                                      Pageable pageable);

    @Query("select new kr.co.moneybridge.dto.reservation.ReservationResponse$RecentPagingByUserDTO(r, p) " +
            "from Reservation r " +
            "join r.pb p " +
            "where r.user.id = :userId and r.process = :process and r.status <> 'CANCEL'")
    Page<ReservationResponse.RecentPagingByUserDTO> findAllByUserIdAndProcess(@Param("userId") Long userId,
                                                                              @Param("process") ReservationProcess process,
                                                                              Pageable pageable);

    @Query("select new kr.co.moneybridge.dto.reservation.ReservationResponse$RecentPagingDTO(r, u) " +
            "from Reservation r " +
            "join r.user u " +
            "where r.pb.id = :pbId and r.status = :status")
    Page<ReservationResponse.RecentPagingDTO> findAllByPbIdAndStatus(@Param("pbId") Long pbId,
                                                                     @Param("status") ReservationStatus status,
                                                                     Pageable pageable);

    @Query("select new kr.co.moneybridge.dto.reservation.ReservationResponse$RecentPagingByUserDTO(r, p) " +
            "from Reservation r " +
            "join r.pb p " +
            "where r.user.id = :userId and r.status = :status")
    Page<ReservationResponse.RecentPagingByUserDTO> findAllByUserIdAndStatus(@Param("userId") Long userId,
                                                                             @Param("status") ReservationStatus status,
                                                                             Pageable pageable);

    // 예약 취소된 경우는 제외한다.
    @Query("select new kr.co.moneybridge.dto.reservation.ReservationResponse$ReservationInfoDTO(r, u) " +
            "from Reservation r " +
            "join r.user u " +
            "where r.pb.id = :pbId " +
            "and r.status <> 'CANCEL'")
    List<ReservationResponse.ReservationInfoDTO> findAllByPbIdWithoutCancel(@Param("pbId") Long pbId);

    @Query("select r " +
            "from Reservation r " +
            "where r.process = :process and r.time <= :time")
    List<Reservation> findAllByTimeBeforeAndProcess(@Param("time") LocalDateTime time,
                                                    @Param("process") ReservationProcess process);
}
