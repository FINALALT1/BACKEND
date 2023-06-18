package kr.co.moneybridge.model.user;

import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.pb.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
public class UserBookmarkRepositoryTest extends DummyEntity {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private PBRepository pbRepository;
    @Autowired
    private UserBookmarkRepository userBookmarkRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE pb_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE company_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE branch_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE user_bookmark_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        Company c = companyRepository.save(newCompany("미래에셋증권"));
        Branch b = branchRepository.save(newBranch(c, 0));
        PB pb = pbRepository.save(newPB("김피비", b));
        User user = userRepository.save(newUser("lee"));
        userBookmarkRepository.save(newUserBookmark(user, pb));
        em.clear();
    }

    @Test
    public void countByUserId() {
        // when
        Boolean isBookmark = userBookmarkRepository.existsByUserIdAndPBId(1L, 1L);

        // then
        Assertions.assertThat(isBookmark).isEqualTo(true);
    }

    @Test
    public void existsByUserIdAndPBId() {
        // when
        Integer count = userBookmarkRepository.countByUserId(1L);

        // then
        Assertions.assertThat(count).isEqualTo(1);
    }
}
