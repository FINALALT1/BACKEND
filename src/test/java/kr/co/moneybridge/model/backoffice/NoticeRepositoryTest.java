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
public class NoticeRepositoryTest extends DummyEntity {
    @Autowired
    NoticeRepository noticeRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE notice_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        noticeRepository.save(newNotice());

        em.clear();
    }

    @Test
    public void findAll_page() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        // when
        Page<Notice> noticePG = noticeRepository.findAll(pageable);

        // then
        Assertions.assertThat(noticePG.getContent().get(0).getId()).isEqualTo(1L);
        Assertions.assertThat(noticePG.getContent().get(0).getTitle()).isEqualTo("서버 점검 안내");
        Assertions.assertThat(noticePG.getContent().get(0).getContent()).isEqualTo("보다 나은 환경을 제공하기 위해 개발진에서 발견한 문제 복구 및 업데이트 점검을 진행할 예정입니다.\n" +
                "업데이트 점검을 진행하는 동안에는 접속할 수 없으니 불필요한 손해가 발생치 않도록 주의해 주세요.\n" +
                "이로 인해 불편을 끼쳐 드려 죄송합니다.");
    }
}
