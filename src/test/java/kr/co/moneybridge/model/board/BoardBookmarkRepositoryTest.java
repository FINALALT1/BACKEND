package kr.co.moneybridge.model.board;

import kr.co.moneybridge.core.dummy.DummyEntity;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

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
        User user2 = userRepository.save(newUser("lee2"));
        boardBookmarkRepository.save(newBoardBookmark(user2, board));
        em.clear();
    }

    @Test
    void deleteByBookmarker() {
        //when
        boardBookmarkRepository.deleteByBookmarker(2L, BookmarkerRole.USER);
        em.flush();

        //then
        Integer count = boardBookmarkRepository.countByBookmarker(BookmarkerRole.USER, 2L);
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void countByBookMarker() {
        // when
        Integer count = boardBookmarkRepository.countByBookmarker(BookmarkerRole.USER, 1L);

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    void findWithUserAndBoard() {
        //when
        Optional<BoardBookmark> boardBookmark = boardBookmarkRepository.findWithUserAndBoard(1L, 1L);

        //then
        assertThat(boardBookmark).isPresent();
    }

    @Test
    void deleteByBoardId() {
        //when
        boardBookmarkRepository.deleteByBoardId(1L);
        em.flush();

        //then
        Integer count = boardBookmarkRepository.countByBookmarker(BookmarkerRole.USER, 1L);
        assertThat(count).isEqualTo(0);
    }

    @Test
    void findByMemberAndBoardId() {
        //when
        Optional<BoardBookmark> boardBookmark = boardBookmarkRepository.findByMemberAndBoardId(1L, 1L);

        //then
        assertThat(boardBookmark).isPresent();
    }
}
