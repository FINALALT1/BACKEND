package kr.co.moneybridge.model.board;

import kr.co.moneybridge.dto.board.BoardResponse;
import kr.co.moneybridge.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

//    @Query("SELECT NEW kr.co.moneybridge.dto.board.BoardResponse$ReplyOutDTO(r, u) " +
//            "FROM Reply r " +
//            "INNER JOIN User u ON r.user.id = u.id " +
//            "WHERE r.board.id = :boardId")
//    List<BoardResponse.ReplyOutDTO> findRepliesByBoardId(@Param("boardId") Long boardId);

    @Modifying
    @Query("DELETE FROM Reply r WHERE r.board.id = :boardId")
    void deleteByBoardId(@Param("boardId") Long boardId);

    @Modifying
    @Query("delete from Reply r WHERE r.parentId = :parentId")
    void deleteByParentId(@Param("parentId") Long parentId);

    @Modifying
    @Query("delete from Reply r where r.authorId = :authorId and r.authorRole = :authorRole")
    void deleteByAuthor(@Param("authorId") Long authorId, @Param("authorRole")ReplyAuthorRole authorRole);

    @Query("select r from Reply r where r.authorId = :authorId and r.authorRole = :authorRole")
    List<Reply> findAllByAuthor(@Param("authorId") Long authorId, @Param("authorRole")ReplyAuthorRole authorRole);

}
