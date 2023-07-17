package kr.co.moneybridge.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserBookmarkRepository extends JpaRepository<UserBookmark, Long> {
    @Query("select case when count(u) > 0 then true else false end from UserBookmark u where u.user.id = :userId and u.pb.id =:pbId")
    Boolean existsByUserIdAndPBId(@Param("userId") Long userId, @Param("pbId") Long pbId);

    @Query("select count(u) from UserBookmark u where u.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("delete from UserBookmark u where u.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("delete from UserBookmark u where u.pb.id = :pbId")
    void deleteByPBId(@Param("pbId") Long pbId);

    @Query("SELECT ub FROM UserBookmark ub where ub.user.id = :userId AND ub.pb.id = :pbId")
    Optional<UserBookmark> findByUserIdWithPbId(@Param("userId") Long userId, @Param("pbId") Long pbId);

    @Query("select u from UserBookmark ub join ub.user u where ub.pb.id = :pbId")
    List<User> findByPBId(@Param("pbId") Long pbId);
}
