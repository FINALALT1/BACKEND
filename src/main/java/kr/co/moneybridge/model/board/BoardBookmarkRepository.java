package kr.co.moneybridge.model.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardBookmarkRepository extends JpaRepository<BoardBookmark, Long> {
    @Modifying
    @Query("delete from BoardBookmark b where b.bookmarkerId = :bookmarkerId and b.bookmarkerRole = :bookmarkerRole")
    void deleteByBookmarker(@Param("bookmarkerId") Long bookmarkerId, @Param("bookmarkerRole")BookmarkerRole bookmarkerRole);

    @Query("SELECT bm FROM BoardBookmark bm JOIN bm.board bd WHERE bm.bookmarkerId = :userId AND bd.id = :boardId AND bm.bookmarkerRole = 'USER'")
    Optional<BoardBookmark> findWithUserAndBoard(@Param("userId") Long userId, @Param("boardId") Long boardId);

    @Query("SELECT bm FROM BoardBookmark bm JOIN bm.board bd WHERE bm.bookmarkerId = :pbId AND bd.id = :boardId AND bm.bookmarkerRole = 'PB'")
    Optional<BoardBookmark> findWithPBAndBoard(@Param("pbId") Long pbId, @Param("boardId") Long boardId);

    @Modifying
    @Query("DELETE FROM BoardBookmark b WHERE b.board.id = :boardId")
    void deleteByBoardId(@Param("boardId") Long boardId);

    @Modifying
    void deleteById(Long id);

//    @Modifying
//    @Query("delete from BoardBookmark b where b.user.id = :userId")
//    void deleteByUserId(@Param("userId") Long userId);

    @Query("SELECT bm FROM BoardBookmark bm WHERE bm.bookmarkerId = :bookmarkerId AND bm.board.id = :boardId")
    Optional<BoardBookmark> findByMemberAndBoardId(@Param("bookmarkerId") Long bookmarkerId, @Param("boardId") Long boardId);
}
