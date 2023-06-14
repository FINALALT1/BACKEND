package kr.co.moneybridge.model.user;

import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.reservation.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);


    @Query("select u from User u where u.name = :name and u.phoneNumber = :phoneNumber")
    List<User> findByNameAndPhoneNumber(@Param("name") String name, @Param("phoneNumber") String phoneNumber);

    @Query("select u " +
            "from User u " +
            "join Reservation res on res.user.id = u.id " +
            "join Review rev on rev.reservation.id = res.id " +
            "where rev.id = :reviewId")
    User findUserByReviewId(@Param("reviewId") Long reviewId);
}