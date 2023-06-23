package kr.co.moneybridge.model.user;

import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.model.pb.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

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
        PB pb2 = pbRepository.save(newPB("김피비2", b));
        User user = userRepository.save(newUser("lee"));
        userBookmarkRepository.save(newUserBookmark(user, pb));
        userBookmarkRepository.save(newUserBookmark(user, pb2));
        User user2 = userRepository.save(newUser("lee2"));
        userBookmarkRepository.save(newUserBookmark(user2, pb));
        em.clear();
    }

    @Test
    void deleteByPBId() {
        //given
        Long id = 2L;

        //when
        userBookmarkRepository.deleteByPBId(id);
        em.flush();

        //then
        assertThat(userBookmarkRepository.existsByUserIdAndPBId(1L, id)).isEqualTo(false);
    }

    @Test
    void deleteByUserId() {
        //given
        Long id = 12L;

        //when
        userBookmarkRepository.deleteByUserId(id);
        em.flush();

        //then
        Long count = userBookmarkRepository.countByUserId(id);
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void existsByUserIdAndPBId() {
        // when
        Boolean isBookmark = userBookmarkRepository.existsByUserIdAndPBId(1L, 1L);

        // then
        assertThat(isBookmark).isEqualTo(true);
    }

    @Test
    public void countByUserId() {
        // when
        Long count = userBookmarkRepository.countByUserId(1L);

        // then
        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    @Test
    void findByUserIdWithPbId() {
        //when
        Optional<UserBookmark> userBookmark = userBookmarkRepository.findByUserIdWithPbId(1L, 1L);

        //then
        assertThat(userBookmark).isPresent();
    }
}
