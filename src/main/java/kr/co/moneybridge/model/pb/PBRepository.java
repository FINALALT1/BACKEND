package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.dto.pb.PBResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    @Query("select p from PB p where p.name = :name and p.phoneNumber = :phoneNumber")
    List<PB> findByNameAndPhoneNumber(@Param("name") String name, @Param("phoneNumber") String phoneNumber);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) FROM PB pb " +
            "JOIN Branch b ON pb.branch = b " +
            "JOIN Company c ON b.company = c " +
            "WHERE pb.name LIKE CONCAT('%', :name, '%')")
    Page<PBResponse.PBPageDTO> findByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) " +
            "FROM PB pb " +
            "JOIN Branch b ON pb.branch = b " +
            "JOIN Company c ON b.company = c " +
            "WHERE pb.speciality1 = :speciality OR pb.speciality2 = :speciality")
    List<PBResponse.PBPageDTO> findByPBListSpeciality(@Param(("speciality")) PBSpeciality speciality);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) " +
            "FROM PB pb " +
            "JOIN Branch b ON pb.branch = b " +
            "JOIN Company c ON b.company = c " +
            "WHERE c.id = :companyId")
    List<PBResponse.PBPageDTO> findByPBListCompany(@Param("companyId") Long companyId);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) " +
            "FROM PB pb " +
            "JOIN Branch b ON pb.branch = b " +
            "JOIN Company c ON b.company = c ")
    List<PBResponse.PBPageDTO> findAllPB();

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) FROM PB pb " +
            "JOIN pb.branch b " +
            "JOIN b.company c " +
            "WHERE pb.speciality1 = :speciality OR pb.speciality2 = :speciality " +
            "ORDER BY pb.career DESC")
    Page<PBResponse.PBPageDTO> findBySpecialityOrderedByCareer(@Param("speciality") PBSpeciality speciality, Pageable pageable);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) FROM PB pb " +
            "JOIN pb.branch b " +
            "JOIN b.company c " +
            "WHERE c.id = :companyId " +
            "ORDER BY pb.career DESC")
    Page<PBResponse.PBPageDTO> findByCompanyIdOrderedByCareer(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) FROM PB pb " +
            "JOIN pb.branch b " +
            "JOIN b.company c " +
            "ORDER BY pb.career DESC")
    Page<PBResponse.PBPageDTO> findAllPBWithCareer(Pageable pageable);
}
