package kr.co.moneybridge.model.reservation;

import kr.co.moneybridge.model.reservation.Style;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StyleRepository extends JpaRepository<Style, Long> {
    @Modifying
    @Query("delete from Style s where s.review.id = :reviewId")
    void deleteByReviewId(@Param("reviewId") Long reviewId);

    @Query("select s " +
            "from Style s " +
            "where s.review.id = :reviewId")
    List<Style> findAllByReviewId(@Param("reviewId") Long reviewId);
}
