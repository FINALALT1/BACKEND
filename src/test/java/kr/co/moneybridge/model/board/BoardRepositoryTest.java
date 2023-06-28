package kr.co.moneybridge.model.board;

import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.dto.board.BoardResponse;
import kr.co.moneybridge.dto.user.UserResponse;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
public class BoardRepositoryTest extends DummyEntity {
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private PBRepository pbRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardBookmarkRepository boardBookmarkRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE board_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE company_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE branch_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE pb_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE board_bookmark_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        Company c = companyRepository.save(newCompany("미래에셋증권"));
        Branch b = branchRepository.save(newBranch(c, 0));
        PB pb = pbRepository.save(newPB("김피비", b));
        PB pb2 = pbRepository.save(newPB("김피비2", b));
        Board board = boardRepository.save(newBoard("게시글", pb));
        Board board2 = boardRepository.save(newBoard("게시글2", pb));
        Board board3 = boardRepository.save(newBoard("게시글3", pb2));
        User user = userRepository.save(newUser("lee"));
        boardBookmarkRepository.save(newBoardBookmark(user, board));

        em.clear();
    }

    @Test
    public void findThumbnailByBoardId() {
        // given
        Long id = 1L;

        // when
        Optional<String> thumbnail = boardRepository.findThumbnailByBoardId(id);

        // then
        assertThat(thumbnail.get()).isEqualTo("thumbnail.png");
    }

    @Test
    void deleteByPBId() {
        //given
        Long id = 2L;

        //when
        boardRepository.deleteByPBId(id);

        //then
        List<Board> board = boardRepository.findAllByPBId(id);
        assertThat(board).isEmpty();
    }

    @Test
    void findAllByPBId() {
        //given
        Long id = 1L;

        //when
        List<Board> board = boardRepository.findAllByPBId(id);

        //then
        assertThat(board.size()).isGreaterThan(1);
    }

    @Test
    public void findProfileById() {
        // given
        Long id = 1L;

        // when
        List<String> thumbnail = boardRepository.findThumbnailsByPBId(id);

        // then
        assertThat(thumbnail.get(0)).isEqualTo("thumbnail.png");
    }

    @Test
    public void findTwoByBookMarker() {
        // given
        String name = "김피비";
        String phoneNumber = "01012345678";

        // when
        Pageable topTwo = PageRequest.of(0, 2);
        Page<UserResponse.BookmarkDTO> boardBookmarkTwo = boardRepository.findTwoByBookmarker(BookmarkerRole.USER, 1L, topTwo);

        // then
        assertThat(boardBookmarkTwo.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(boardBookmarkTwo.getContent().get(0).getThumbnail()).isEqualTo("thumbnail.png");
    }

    @Test
    void findByTitle() {
        //when
        Page<BoardResponse.BoardPageDTO> page = boardRepository.findBySearch("게시글", BoardStatus.ACTIVE, PageRequest.of(0, 2));

        //then
        assertThat(page).isNotEmpty();
        assertThat(page.getContent().get(0).getTitle()).contains("게시글");
    }

    @Test
    void findByPbName() {
        //when
        Page<BoardResponse.BoardPageDTO> page = boardRepository.findByPbName("김피비", BoardStatus.ACTIVE, PageRequest.of(0, 2));

        //then
        assertThat(page).isNotEmpty();
        assertThat(page.getContent().get(0).getTitle()).contains("게시글");
    }

    @Test
    void findAll() {
        //when
        Page<BoardResponse.BoardPageDTO> page = boardRepository.findAll(BoardStatus.ACTIVE, PageRequest.of(0, 2));

        //then
        assertThat(page).isNotEmpty();
        assertThat(page.getContent().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void findByIdAndPbId() {
        //when
        Optional<Board> board = boardRepository.findByIdAndPbId(1L, 1L);

        //then
        assertThat(board).isPresent();
    }
}
