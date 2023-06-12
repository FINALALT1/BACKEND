package kr.co.moneybridge.model.backoffice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Modifying
    @Query("delete from Answer a where a.question.id = :questionId")
    void deleteByQuestionId(@Param("questionId") Long questionId);
}
