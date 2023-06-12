package kr.co.moneybridge.model.backoffice;

import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.board.Reply;
import kr.co.moneybridge.model.board.ReplyAuthorRole;
import kr.co.moneybridge.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Modifying
    @Query("delete from Question q where q.authorId = :authorId and q.authorRole = :authorRole")
    void deleteByAuthor(@Param("authorId") Long authorId, @Param("authorRole") QuestionAuthorRole authorRole);

    @Query("select q from Question q where q.authorId = :authorId and q.authorRole = :authorRole")
    List<Question> findAllByAuthor(@Param("authorId") Long authorId, @Param("authorRole") QuestionAuthorRole authorRole);

}
