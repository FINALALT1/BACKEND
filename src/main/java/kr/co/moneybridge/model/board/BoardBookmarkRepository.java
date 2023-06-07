package kr.co.moneybridge.model.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardBookmarkRepository extends JpaRepository<BoardBookmark, Long> {

    @Query("SELECT b FROM BoardBookmark b JOIN b.user u JOIN b.board bd WHERE u.id = :userId AND bd.id = :boardId")
    Optional<BoardBookmark> findWithUserAndBoard(@Param("userId") Long userId, @Param("boardId") Long boardId);
}
