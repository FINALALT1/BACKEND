package kr.co.moneybridge.model.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);


    @Query("select u from User u where u.phoneNumber = :phoneNumber")
    List<User> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("select u " +
            "from User u " +
            "join Reservation res on res.user.id = u.id " +
            "join Review rev on rev.reservation.id = res.id " +
            "where rev.id = :reviewId")
    User findUserByReviewId(@Param("reviewId") Long reviewId);

    @Query("select count(*) from User u where u.phoneNumber = :phoneNumber")
    int countByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("select u from User u where u.email like concat('%', :keyword, '%')")
    Page<User> findAllByEmail(Pageable pageable, @Param("keyword") String keyword);

    @Query("select u from User u where u.phoneNumber like concat('%', :keyword, '%')")
    Page<User> findAllByPhoneNumber(Pageable pageable, @Param("keyword") String keyword);

    @Query("select u from User u where u.name like concat('%', :keyword, '%')")
    Page<User> findAllByName(Pageable pageable, @Param("keyword") String keyword);
}