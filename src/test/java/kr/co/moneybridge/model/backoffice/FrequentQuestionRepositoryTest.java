package kr.co.moneybridge.model.backoffice;

import kr.co.moneybridge.core.dummy.DummyEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
public class FrequentQuestionRepositoryTest extends DummyEntity {
    @Autowired
    FrequentQuestionRepository frequentQuestionRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE frequent_question_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        frequentQuestionRepository.save(newFrequentQuestion());

        em.clear();
    }

    @Test
    public void findAll_page() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        // when
        Page<FrequentQuestion> faqPG = frequentQuestionRepository.findAll(pageable);

        // then
        Assertions.assertThat(faqPG.getContent().get(0).getId()).isEqualTo(1L);
        Assertions.assertThat(faqPG.getContent().get(0).getLabel()).isEqualTo("회원");
        Assertions.assertThat(faqPG.getContent().get(0).getTitle()).isEqualTo("이메일이 주소가 변경되었어요.");
        Assertions.assertThat(faqPG.getContent().get(0).getContent()).isEqualTo("가입 이메일은 회원 식별 고유 키로 " +
                "가입 후 변경이 불가능 하므로 개인 이메일로 가입하기를 권유드립니다.");
    }
}
