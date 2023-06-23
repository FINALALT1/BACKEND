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
class ReReplyRepositoryTest extends DummyEntity {

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
    private ReReplyRepository reReplyRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        em.createNativeQuery("ALTER TABLE board_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE company_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE branch_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE pb_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE reply_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE rereply_tb ALTER COLUMN  `id` RESTART WITH 1").executeUpdate();
        Company c = companyRepository.save(newCompany("미래에셋증권"));
        Branch b = branchRepository.save(newBranch(c, 0));
        PB pb = pbRepository.save(newPB("김피비", b));
        Board board = boardRepository.save(newBoard("게시글", pb));
        User user = userRepository.save(newUser("김회원"));
        Reply reply = replyRepository.save(newUserReply(board, user));
        ReReply reReply1 = reReplyRepository.save(newPBReReply(reply, pb));
        ReReply reReply2 = reReplyRepository.save(newUserReReply(reply, user));
        em.clear();
    }

    @Test
    void deleteByReplyId() {
        //when
        reReplyRepository.deleteByReplyId(1L);

        //then
        List<ReReply> list = reReplyRepository.findAll();
        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    void deleteByAuthor() {
        //when
        reReplyRepository.deleteByAuthor(1L, ReplyAuthorRole.USER);

        //then
        List<ReReply> list = reReplyRepository.findAll();
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    void findUserReReplyByReplyId() {
        //when
        List<BoardResponse.ReReplyOutDTO> list = reReplyRepository.findUserReReplyByReplyId(1L);

        //then
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    void findPBReReplyByReplyId() {
        //when
        List<BoardResponse.ReReplyOutDTO> list = reReplyRepository.findPBReReplyByReplyId(1L);

        //then
        assertThat(list.size()).isEqualTo(1);
    }
}