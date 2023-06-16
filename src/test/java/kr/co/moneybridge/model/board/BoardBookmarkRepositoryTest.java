package kr.co.moneybridge.model.board;

import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
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

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
public class BoardBookmarkRepositoryTest extends DummyEntity {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private PBRepository pbRepository;
    @Autowired
    private BoardBookmarkRepository boardBookmarkRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE pb_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE company_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE branch_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE board_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE board_bookmark_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        Company c = companyRepository.save(newCompany("미래에셋증권"));
        Branch b = branchRepository.save(newBranch(c, 0));
        PB pb = pbRepository.save(newPB("김피비", b));
        Board board = boardRepository.save(newBoard("board", pb));
        User user = userRepository.save(newUser("lee"));
        boardBookmarkRepository.save(newBoardBookmark(user, board));
        em.clear();
    }

    @Test
    public void countByBookMarker() {
        // when
        Integer count = boardBookmarkRepository.countByBookmarker(BookmarkerRole.USER, 1L);

        // then
        Assertions.assertThat(count).isEqualTo(1);
    }
}
