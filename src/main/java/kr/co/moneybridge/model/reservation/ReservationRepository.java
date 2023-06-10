package kr.co.moneybridge.model.reservation;

import kr.co.moneybridge.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
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
}
