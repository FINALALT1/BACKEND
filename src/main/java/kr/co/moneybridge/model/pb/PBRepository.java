package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.dto.backOffice.BackOfficeResponse;
import kr.co.moneybridge.dto.pb.PBResponse;
import kr.co.moneybridge.dto.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PBRepository extends JpaRepository<PB, Long> {
    @Query("select count(p) from PB p where p.status = :status")
    Long countByStatus(@Param("status") PBStatus status);

    @Query("select p.businessCard from PB p where p.id = :id")
    Optional<String> findBusinessCardById(@Param("id") Long id);

    @Query("select p.profile from PB p where p.id = :id")
    Optional<String> findProfileById(@Param("id") Long id);

    @Query("select p from PB p where p.status = :status")
    Page<PB> findAllByStatus(@Param("status") PBStatus status, Pageable pageable);

    @Query("select new kr.co.moneybridge.dto.backOffice.BackOfficeResponse$PBOutDTO(p, br) from PB p " +
            "join p.branch br " +
            "where p.status = :status")
    Page<BackOfficeResponse.PBOutDTO> findPagesByStatus(@Param("status") PBStatus status, Pageable pageable);

    @Query("select new kr.co.moneybridge.dto.backOffice.BackOfficeResponse$PBOutDTO(p, br) from PB p " +
            "join p.branch br " +
            "where p.status = :status " +
            "and p.email like concat('%', :keyword, '%')")
    Page<BackOfficeResponse.PBOutDTO> findPagesByStatusAndEmail(@Param("keyword") String keyword, @Param("status") PBStatus status, Pageable pageable);

    @Query("select new kr.co.moneybridge.dto.backOffice.BackOfficeResponse$PBOutDTO(p, br) from PB p " +
            "join p.branch br " +
            "where p.status = :status " +
            "and p.phoneNumber like concat('%', :keyword, '%')")
    Page<BackOfficeResponse.PBOutDTO> findPagesByStatusAndPhoneNumber(@Param("keyword") String keyword, @Param("status") PBStatus status, Pageable pageable);

    @Query("select new kr.co.moneybridge.dto.backOffice.BackOfficeResponse$PBOutDTO(p, br) from PB p " +
            "join p.branch br " +
            "where p.status = :status " +
            "and p.name like concat('%', :keyword, '%')")
    Page<BackOfficeResponse.PBOutDTO> findPagesByStatusAndName(@Param("keyword") String keyword, @Param("status") PBStatus status, Pageable pageable);


    @Query("select p from PB p where p.id in :list")
    List<PB> findByIdIn(@Param("list") List<Long> list);

    @Query("select p.id from PB p where p.speciality1 in :list or p.speciality2 in :list")
    List<Long> findIdsBySpecialityIn(@Param("list") List<PBSpeciality> list);

    @Query("select p.id from PB p where (p.speciality1 not in :list or p.speciality1 is null) " +
            "and (p.speciality2 not in :list or p.speciality2 is null)")
    List<Long> findIdsBySpecialityNotIn(@Param("list") List<PBSpeciality> list);


    @Query("select new kr.co.moneybridge.dto.user.UserResponse$BookmarkDTO(pb) from PB pb " +
            "join UserBookmark ub on ub.pb = pb where ub.user.id = :userId")
    Page<UserResponse.BookmarkDTO> findTwoByBookmarker(@Param("userId") Long userId, Pageable pageable);

    @Query("select p from PB p where p.email = :email")
    Optional<PB> findByEmail(@Param("email") String email);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) FROM PB pb " +
            "JOIN Branch b ON pb.branch = b " +
            "JOIN Company c ON b.company = c " +
            "JOIN UserBookmark ub ON ub.pb = pb " +
            "WHERE ub.user.id = :userId")
    Page<PBResponse.PBPageDTO> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.pb.id = :pbId")
    Long countReservationsByPbId(@Param("pbId") Long pbId);

    @Query("SELECT COUNT(rv) FROM Review rv JOIN Reservation r ON rv.reservation = r WHERE r.pb.id = :pbId")
    Long countReviewsByPbId(@Param("pbId") Long pbId);

    @Query("select p from PB p where p.phoneNumber = :phoneNumber")
    List<PB> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) FROM PB pb " +
            "JOIN Branch b ON pb.branch = b " +
            "JOIN Company c ON b.company = c " +
            "WHERE pb.name LIKE CONCAT('%', :name, '%') AND pb.status = 'ACTIVE' ")
    Page<PBResponse.PBPageDTO> findByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) " +
            "FROM PB pb " +
            "JOIN Branch b ON pb.branch = b " +
            "JOIN Company c ON b.company = c " +
            "WHERE (pb.speciality1 = :speciality OR pb.speciality2 = :speciality) AND pb.status = 'ACTIVE' ")
    List<PBResponse.PBPageDTO> findByPBListSpeciality(@Param(("speciality")) PBSpeciality speciality);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) " +
            "FROM PB pb " +
            "JOIN Branch b ON pb.branch = b " +
            "JOIN Company c ON b.company = c " +
            "WHERE c.id = :companyId AND pb.status = 'ACTIVE' ")
    List<PBResponse.PBPageDTO> findByPBListCompany(@Param("companyId") Long companyId);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) " +
            "FROM PB pb " +
            "JOIN Branch b ON pb.branch = b " +
            "JOIN Company c ON b.company = c " +
            "WHERE pb.status = 'ACTIVE' ")
    List<PBResponse.PBPageDTO> findAllPB();

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) FROM PB pb " +
            "JOIN pb.branch b " +
            "JOIN b.company c " +
            "WHERE (pb.speciality1 = :speciality OR pb.speciality2 = :speciality) AND pb.status = 'ACTIVE' " +
            "ORDER BY pb.career DESC")
    Page<PBResponse.PBPageDTO> findBySpecialityOrderedByCareer(@Param("speciality") PBSpeciality speciality, Pageable pageable);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) FROM PB pb " +
            "JOIN pb.branch b " +
            "JOIN b.company c " +
            "WHERE c.id = :companyId AND pb.status = 'ACTIVE' " +
            "ORDER BY pb.career DESC")
    Page<PBResponse.PBPageDTO> findByCompanyIdOrderedByCareer(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) FROM PB pb " +
            "JOIN pb.branch b " +
            "JOIN b.company c " +
            "WHERE pb.status = 'ACTIVE' " +
            "ORDER BY pb.career DESC")
    Page<PBResponse.PBPageDTO> findAllPBWithCareer(Pageable pageable);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) FROM PB pb " +
            "JOIN pb.branch b " +
            "JOIN b.company c " +
            "WHERE (pb.speciality1 IN (:specialities) OR pb.speciality2 IN (:specialities)) AND pb.status = 'ACTIVE' ")
    Page<PBResponse.PBPageDTO> findRecommendedPBList(Pageable pageable, @Param("specialities") PBSpeciality... specialities);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBSimpleProfileDTO(pb, c) FROM PB pb " +
            "JOIN pb.branch b " +
            "JOIN b.company c " +
            "WHERE pb.id = :id AND pb.status = 'ACTIVE' ")
    Optional<PBResponse.PBSimpleProfileDTO> findSimpleProfile(@Param("id") Long id);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBProfileDTO(pb, b, c) FROM PB pb " +
            "JOIN pb.branch b " +
            "JOIN b.company c " +
            "WHERE pb.id = :id")
    Optional<PBResponse.PBProfileDTO> findPBProfile(@Param("id") Long id);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBUpdateOutDTO(pb, b, c) " +
            "FROM PB pb " +
            "JOIN pb.branch b " +
            "JOIN b.company c " +
            "WHERE pb.id = :pbId")
    Optional<PBResponse.PBUpdateOutDTO> findPBDetailByPbId(@Param("pbId") Long pbId);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) FROM PB pb " +
            "JOIN pb.branch b " +
            "JOIN b.company c " +
            "WHERE (pb.speciality1 = :speciality1 OR pb.speciality2 = :speciality1 OR pb.speciality1 = :speciality2 OR pb.speciality2 = :speciality2) AND pb.status = 'ACTIVE' " +
            "ORDER BY pb.id DESC")
    List<PBResponse.PBPageDTO> findBySpeciality1And2(@Param("speciality1") PBSpeciality speciality1, @Param("speciality2") PBSpeciality speciality2, Pageable pageable);

    @Query("SELECT new kr.co.moneybridge.dto.pb.PBResponse$PBPageDTO(pb, b, c) FROM PB pb " +
            "JOIN pb.branch b " +
            "JOIN b.company c " +
            "WHERE (pb.speciality1 = :speciality1 OR pb.speciality2 = :speciality1) AND pb.status = 'ACTIVE' " +
            "ORDER BY pb.id DESC")
    List<PBResponse.PBPageDTO> findBySpeciality1(@Param("speciality1") PBSpeciality speciality1, Pageable pageable);

    @Query("select count(*) from PB p where p.phoneNumber = :phoneNumber")
    int countByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("select count(*) from PB p where p.branch.id = :branchId")
    int countByBranchId(Long branchId);
}
