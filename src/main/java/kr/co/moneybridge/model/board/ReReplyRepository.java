package kr.co.moneybridge.model.board;

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
}
