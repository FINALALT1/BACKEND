package kr.co.moneybridge.model.board;

import kr.co.moneybridge.dto.board.BoardResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("SELECT NEW kr.co.moneybridge.dto.board.BoardResponse$ReplyOutDTO(r, u) " +
            "FROM Reply r " +
            "INNER JOIN User u ON r.user.id = u.id " +
            "WHERE r.board.id = :boardId AND r.status = :status")
    List<BoardResponse.ReplyOutDTO> findRepliesByBoardId(@Param("boardId") Long boardId, @Param("status") Boolean status);

}
