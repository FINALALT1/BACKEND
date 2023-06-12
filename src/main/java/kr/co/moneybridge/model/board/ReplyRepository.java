package kr.co.moneybridge.model.board;

import kr.co.moneybridge.dto.board.BoardResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("SELECT NEW kr.co.moneybridge.dto.board.BoardResponse$ReplyOutDTO(r, u) " +
            "FROM Reply r " +
            "INNER JOIN User u ON r.authorId = u.id " +
            "WHERE r.board.id = :boardId")
    List<BoardResponse.ReplyOutDTO> findUserRepliesByBoardId(@Param("boardId") Long boardId);

    @Query("SELECT NEW kr.co.moneybridge.dto.board.BoardResponse$ReplyOutDTO(r, pb) " +
            "FROM Reply r " +
            "INNER JOIN PB pb ON r.authorId = pb.id " +
            "WHERE r.board.id = :boardId")
    List<BoardResponse.ReplyOutDTO> findPBRepliesByBoardId(@Param("boardId") Long boardId);

    @Modifying
    @Query("DELETE FROM Reply r WHERE r.board.id = :boardId")
    void deleteByBoardId(@Param("boardId") Long boardId);

    @Modifying
    @Query("delete from Reply r where r.authorId = :authorId and r.authorRole = :authorRole")
    void deleteByAuthor(@Param("authorId") Long authorId, @Param("authorRole")ReplyAuthorRole authorRole);

    @Query("select r from Reply r where r.authorId = :authorId and r.authorRole = :authorRole")
    List<Reply> findAllByAuthor(@Param("authorId") Long authorId, @Param("authorRole")ReplyAuthorRole authorRole);

    @Query("select r from Reply r where r.board.id = :boardId")
    List<Reply> findAllByBoardId(@Param("boardId") Long boardId);
}
