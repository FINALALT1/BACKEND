package kr.co.moneybridge.model.pb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PBBookmarkRepository extends JpaRepository<PBBookmark, Long> {
    @Modifying
    @Query("delete from PBBookmark p where p.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("delete from PBBookmark p where p.pb.id = :pbId")
    void deleteByPBId(@Param("pbId") Long pbId);
}
