package kr.co.moneybridge.model.board;

import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.dto.board.BoardResponse;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
class ReplyRepositoryTest extends DummyEntity {

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
    private ReplyRepository replyRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        em.createNativeQuery("ALTER TABLE board_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE company_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE branch_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE pb_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        Company c = companyRepository.save(newCompany("미래에셋증권"));
        Branch b = branchRepository.save(newBranch(c, 0));
        PB pb = pbRepository.save(newPB("김피비", b));
        Board board = boardRepository.save(newBoard("게시글", pb));
        User user = userRepository.save(newUser("김회원"));
        Reply reply1 = replyRepository.save(newUserReply(board, user));
        Reply reply2 = replyRepository.save(newPBReply(board, pb));
        em.clear();
    }

    @Test
    void findUserRepliesByBoardId() {
        //when
        List<BoardResponse.ReplyOutDTO> list = replyRepository.findUserRepliesByBoardId(1L);

        //then
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    void findPBRepliesByBoardId() {
        //when
        List<BoardResponse.ReplyOutDTO> list = replyRepository.findPBRepliesByBoardId(1L);

        //then
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    void deleteByBoardId() {
        //when
        replyRepository.deleteByBoardId(1L);

        //then
        List<Reply> list = replyRepository.findAllByBoardId(1L);
        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    void deleteByAuthor() {
        //when
        replyRepository.deleteByAuthor(1L, ReplyAuthorRole.PB);

        //then
        List<Reply> list = replyRepository.findAllByBoardId(1L);
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    void findAllByAuthor() {
        //when
        List<Reply> list = replyRepository.findAllByAuthor(1L, ReplyAuthorRole.USER);

        //then
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    void findAllByBoardId() {
        //when
        List<Reply> list = replyRepository.findAllByBoardId(1L);

        //then
        assertThat(list.size()).isEqualTo(2);
    }
}