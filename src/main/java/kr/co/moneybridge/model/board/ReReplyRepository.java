package kr.co.moneybridge.model.board;

import kr.co.moneybridge.dto.board.BoardResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReReplyRepository extends JpaRepository<ReReply, Long> {
    @Modifying
    @Query("delete from ReReply r where r.reply.id = :replyId")
    void deleteByReplyId(@Param("replyId") Long replyId);

    @Modifying
    @Query("delete from ReReply r where r.authorId = :authorId and r.authorRole = :authorRole")
    void deleteByAuthor(@Param("authorId") Long authorId, @Param("authorRole")ReplyAuthorRole authorRole);

    @Query("SELECT new kr.co.moneybridge.dto.board.BoardResponse$ReReplyOutDTO(rr, u) " +
            "FROM ReReply rr " +
            "JOIN User u ON rr.authorId = u.id " +
            "WHERE rr.authorRole = 'USER' AND rr.reply.id = :replyId")
    List<BoardResponse.ReReplyOutDTO> findUserReReplyByReplyId(@Param("replyId") Long replyId);

    @Query("SELECT new kr.co.moneybridge.dto.board.BoardResponse$ReReplyOutDTO(rr, pb) " +
            "FROM ReReply rr " +
            "JOIN PB pb ON rr.authorId = pb.id " +
            "WHERE rr.authorRole = 'PB' AND rr.reply.id = :replyId")
    List<BoardResponse.ReReplyOutDTO> findPBReReplyByReplyId(@Param("replyId") Long replyId);
}
