package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.model.backoffice.FrequentQuestionRepository;
import kr.co.moneybridge.model.backoffice.NoticeRepository;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("백오피스 관련 API")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BackOfficeControllerTest {
    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private EntityManager em;
    @Autowired
    private FrequentQuestionRepository frequentQuestionRepository;
    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private PBRepository pbRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private StyleRepository styleRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private ReReplyRepository reReplyRepository;

    @BeforeEach
    public void setUp() {
        User admin = userRepository.save(dummy.newAdmin("admin"));
        User user = userRepository.save(dummy.newAdmin("user"));
        User admin2 = userRepository.save(dummy.newAdmin("admin2"));
        User user4 = userRepository.save(dummy.newAdmin("user4"));
        frequentQuestionRepository.save(dummy.newFrequentQuestion());
        noticeRepository.save(dummy.newNotice());
        Company companyPS = companyRepository.save(dummy.newCompany("미래에셋증권"));
        Branch branchPS = branchRepository.save(dummy.newBranch(companyPS, 1));
        PB pbPS = pbRepository.save(dummy.newPBwithStatus("pblee", branchPS, PBStatus.PENDING));
        PB pb2 = pbRepository.save(dummy.newPBwithStatus("false", branchPS, PBStatus.PENDING));
        PB pb3 = pbRepository.save(dummy.newPB("pb3", branchPS));
        PB pb4 = pbRepository.save(dummy.newPB("pb4", branchPS));
        Reservation reservation1 = reservationRepository.save(dummy.newCallReservation(user, pb3, ReservationProcess.APPLY));
        Reservation reservation2 = reservationRepository.save(dummy.newCallReservation(user, pb4, ReservationProcess.CONFIRM));
        Reservation reservation3 = reservationRepository.save(dummy.newCallReservation(user4, pb4, ReservationProcess.COMPLETE));
        Review review = reviewRepository.save(dummy.newReview(reservation1));
        Style style = styleRepository.save(dummy.newStyle(review, StyleStyle.FAST));
        boardRepository.save(dummy.newBoard("title", pbPS));
        Board board2 = boardRepository.save(dummy.newBoard("title2", pbPS));
        replyRepository.save(dummy.newUserReply(board2, user));
        Reply reply2 = replyRepository.save(dummy.newUserReply(board2, user));
        reReplyRepository.save(dummy.newPBReReply(reply2, pb2));
        em.clear();
    }

    @WithUserDetails(value = "ADMIN-admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("대댓글 강제 삭제")
    @Test
    public void deleteReReply() throws Exception {
        // given
        Long id = 1L;

        // when
        ResultActions resultActions = mvc
                .perform(delete("/admin/rereply/{id}", id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        resultActions.andExpect(status().isOk());
    }

    @WithUserDetails(value = "ADMIN-admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("댓글 강제 삭제")
    @Test
    public void deleteReply() throws Exception {
        // given
        Long id = 1L;

        // when
        ResultActions resultActions = mvc
                .perform(delete("/admin/reply/{id}", id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        resultActions.andExpect(status().isOk());
    }

    @WithUserDetails(value = "ADMIN-admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("콘텐츠 강제 삭제")
    @Test
    public void deleteBoard() throws Exception {
        // given
        Long id = 1L;

        // when
        ResultActions resultActions = mvc
                .perform(delete("/admin/board/{id}", id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        resultActions.andExpect(status().isOk());
    }

    @WithUserDetails(value = "ADMIN-admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("상담 현황 페이지 전체 가져오기 성공")
    @Test
    public void getReservations() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/admin/reservations"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("3"));
        resultActions.andExpect(jsonPath("$.data.list[0].process").value("COMPLETE"));
        resultActions.andExpect(jsonPath("$.data.list[0].status").value("ACTIVE"));
        resultActions.andExpect(jsonPath("$.data.list[0].time").value(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분"))));
        resultActions.andExpect(jsonPath("$.data.list[0].type").value("CALL"));
        resultActions.andExpect(jsonPath("$.data.list[0].locationName").value("kb증권 강남중앙점"));
        resultActions.andExpect(jsonPath("$.data.list[0].goal").value("PROFIT"));
        resultActions.andExpect(jsonPath("$.data.list[0].question").value("질문입니다..."));
        resultActions.andExpect(jsonPath("$.data.list[0].user.id").value("4"));
        resultActions.andExpect(jsonPath("$.data.list[0].user.email").value("user4@nate.com"));
        resultActions.andExpect(jsonPath("$.data.list[0].user.name").value("user4"));
        resultActions.andExpect(jsonPath("$.data.list[0].user.phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.list[0].user.isAdmin").value("true"));
        resultActions.andExpect(jsonPath("$.data.list[0].pb.id").value("4"));
        resultActions.andExpect(jsonPath("$.data.list[0].pb.email").value("pb4@nate.com"));
        resultActions.andExpect(jsonPath("$.data.list[0].pb.name").value("pb4"));
        resultActions.andExpect(jsonPath("$.data.list[0].pb.phoneNumber").value("01012345678"));
        resultActions.andExpect(status().isOk());
    }

    @WithUserDetails(value = "ADMIN-admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("해당 투자자 강제 탈퇴")
    @Test
    public void forceWithdrawUser() throws Exception {
        // given
        Long id = 4L;

        // when
        ResultActions resultActions = mvc
                .perform(delete("/admin/user/{id}", id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        resultActions.andExpect(status().isOk());
    }

    @WithUserDetails(value = "ADMIN-admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("해당 PB 강제 탈퇴")
    @Test
    public void forceWithdrawPB() throws Exception {
        // given
        Long id = 4L;

        // when
        ResultActions resultActions = mvc
                .perform(delete("/admin/pb/{id}", id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        resultActions.andExpect(status().isOk());
    }

    @WithUserDetails(value = "ADMIN-admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("해당 투자자를 관리자로 등록 취소 성공")
    @Test
    public void deAuthorizeAdmin() throws Exception {
        // given
        Long id = 3L;

        // when
        ResultActions resultActions = mvc
                .perform(post("/admin/user/{id}?admin=false", id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        resultActions.andExpect(status().isOk());
    }

    @WithUserDetails(value = "ADMIN-admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("해당 투자자를 관리자로 등록 성공")
    @Test
    public void authorizeAdmin() throws Exception {
        // given
        Long id = 2L;

        // when
        ResultActions resultActions = mvc
                .perform(post("/admin/user/{id}?admin=true", id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        resultActions.andExpect(status().isOk());
    }

    @WithUserDetails(value = "ADMIN-admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("회원 리스트 가져오기 성공")
    @Test
    public void getMembers() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/admin/members"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("4"));
        resultActions.andExpect(jsonPath("$.data.list[0].email").value("user4@nate.com"));
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("user4"));
        resultActions.andExpect(jsonPath("$.data.list[0].phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.list[0].isAdmin").value("true"));
        resultActions.andExpect(jsonPath("$.data.totalElements").isNumber());
        resultActions.andExpect(jsonPath("$.data.totalPages").value("1"));
        resultActions.andExpect(jsonPath("$.data.curPage").value("0"));
        resultActions.andExpect(jsonPath("$.data.first").value("true"));
        resultActions.andExpect(jsonPath("$.data.last").value("true"));
        resultActions.andExpect(jsonPath("$.data.empty").value("false"));
        resultActions.andExpect(status().isOk());
    }

    @WithUserDetails(value = "ADMIN-admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("해당 PB 승인/승인 거부 성공")
    @Test
    public void approve_no_PB() throws Exception {
        // given
        Long id = 2L;

        // when
        ResultActions resultActions = mvc
                .perform(post("/admin/pb/{id}?approve=false", id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        resultActions.andExpect(status().isOk());
    }

    @WithUserDetails(value = "ADMIN-admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("해당 PB 승인/승인 거부 성공")
    @Test
    public void approvePB() throws Exception {
        // given
        Long id = 1L;

        // when
        ResultActions resultActions = mvc
                .perform(post("/admin/pb/{id}?approve=true", id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        resultActions.andExpect(status().isOk());
    }

    @WithUserDetails(value = "ADMIN-admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("PB 회원 가입 승인 대기 리스트 가져오기 성공")
    @Test
    public void getPBPending() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/admin/pbs"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("2"));
        resultActions.andExpect(jsonPath("$.data.list[0].email").value("false@nate.com"));
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("false"));
        resultActions.andExpect(jsonPath("$.data.list[0].phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.list[0].branchName").value("미래에셋증권 여의도점"));
        resultActions.andExpect(jsonPath("$.data.list[0].career").value("10"));
        resultActions.andExpect(jsonPath("$.data.list[0].speciality1").value("BOND"));
        resultActions.andExpect(jsonPath("$.data.list[0].speciality2").isEmpty());
        resultActions.andExpect(jsonPath("$.data.list[0].businessCard").value("card.png"));
        resultActions.andExpect(jsonPath("$.data.totalElements").isNumber());
        resultActions.andExpect(jsonPath("$.data.totalPages").value("1"));
        resultActions.andExpect(jsonPath("$.data.curPage").value("0"));
        resultActions.andExpect(jsonPath("$.data.first").value("true"));
        resultActions.andExpect(jsonPath("$.data.last").value("true"));
        resultActions.andExpect(jsonPath("$.data.empty").value("false"));
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("공지사항 목록 가져오기 성공")
    @Test
    public void getNotice() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/notices"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].title").value("서버 점검 안내"));
        resultActions.andExpect(jsonPath("$.data.list[0].content").value("보다 나은 환경을 제공하기 위해 개발진에서 발견한 문제 복구 및 업데이트 점검을 진행할 예정입니다.\n" +
                "업데이트 점검을 진행하는 동안에는 접속할 수 없으니 불필요한 손해가 발생치 않도록 주의해 주세요.\n" +
                "이로 인해 불편을 끼쳐 드려 죄송합니다."));
        resultActions.andExpect(jsonPath("$.data.list[0].date").value(LocalDate.now().toString()));
        resultActions.andExpect(jsonPath("$.data.totalElements").value("1"));
        resultActions.andExpect(jsonPath("$.data.totalPages").value("1"));
        resultActions.andExpect(jsonPath("$.data.curPage").value("0"));
        resultActions.andExpect(jsonPath("$.data.first").value("true"));
        resultActions.andExpect(jsonPath("$.data.last").value("true"));
        resultActions.andExpect(jsonPath("$.data.empty").value("false"));
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("자주 묻는 질문 목록 가져오기 성공")
    @Test
    public void getFAQ() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/faqs"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].label").value("회원"));
        resultActions.andExpect(jsonPath("$.data.list[0].title").value("이메일이 주소가 변경되었어요."));
        resultActions.andExpect(jsonPath("$.data.list[0].content").value("가입 이메일은 회원 식별 고유 키로 " +
                "가입 후 변경이 불가능 하므로 개인 이메일로 가입하기를 권유드립니다."));
        resultActions.andExpect(jsonPath("$.data.totalElements").value("1"));
        resultActions.andExpect(jsonPath("$.data.totalPages").value("1"));
        resultActions.andExpect(jsonPath("$.data.curPage").value("0"));
        resultActions.andExpect(jsonPath("$.data.first").value("true"));
        resultActions.andExpect(jsonPath("$.data.last").value("true"));
        resultActions.andExpect(jsonPath("$.data.empty").value("false"));
        resultActions.andExpect(status().isOk());
    }
}
