package kr.co.moneybridge.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBookmarkRepository extends JpaRepository<UserBookmark, Long> {
    @Query("select count(u) from UserBookmark u where u.user.id = :userId")
    Integer countByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("delete from UserBookmark u where u.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("delete from UserBookmark u where u.pb.id = :pbId")
    void deleteByPBId(@Param("pbId") Long pbId);
}
