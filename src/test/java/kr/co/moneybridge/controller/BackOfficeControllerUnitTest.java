package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.WithMockAdmin;
import kr.co.moneybridge.core.advice.LogAdvice;
import kr.co.moneybridge.core.advice.ValidAdvice;
import kr.co.moneybridge.core.config.FilterRegisterConfig;
import kr.co.moneybridge.core.config.SecurityConfig;
import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.core.util.MemberUtil;
import kr.co.moneybridge.core.util.RedisUtil;
import kr.co.moneybridge.core.util.StibeeUtil;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.backOffice.BackOfficeRequest;
import kr.co.moneybridge.dto.backOffice.BackOfficeResponse;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
import kr.co.moneybridge.model.backoffice.FrequentQuestion;
import kr.co.moneybridge.model.backoffice.Notice;
import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.board.ReReply;
import kr.co.moneybridge.model.board.Reply;
import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.model.pb.Company;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBStatus;
import kr.co.moneybridge.model.reservation.Reservation;
import kr.co.moneybridge.model.reservation.ReservationProcess;
import kr.co.moneybridge.model.reservation.Review;
import kr.co.moneybridge.model.reservation.StyleStyle;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.service.BackOfficeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@EnableAspectJAutoProxy
@Import({
        LogAdvice.class,
        ValidAdvice.class,
        FilterRegisterConfig.class,
        SecurityConfig.class,
        RedisUtil.class
})
@WebMvcTest(
        controllers = {BackOfficeController.class}
)
public class BackOfficeControllerUnitTest extends MockDummyEntity {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean // 껍데기만
    private BackOfficeService backOfficeService;
    @MockBean
    private RedisTemplate redisTemplate;
    @MockBean
    private MemberUtil memberUtil;
    @MockBean
    private StibeeUtil stibeeUtil;

    @WithMockAdmin
    @Test
    public void deleteRereply_test() throws Exception {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "pblee", branch);
        Board board = newMockBoard(1L, "title", pb);
        Reply reply = newMockPBReply(1L, board, pb);
        ReReply reReply = newMockPBReReply(1L, reply, pb);

        // stub
        Mockito.doNothing().when(backOfficeService).deleteReReply(any());

        // When
        ResultActions resultActions = mvc.perform((delete("/admin/reply/{id}", id)));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockAdmin
    @Test
    public void deleteReply_test() throws Exception {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "pblee", branch);
        Board board = newMockBoard(1L, "title", pb);
        Reply reply = newMockPBReply(1L, board, pb);

        // stub
        Mockito.doNothing().when(backOfficeService).deleteReply(any());

        // When
        ResultActions resultActions = mvc.perform((delete("/admin/reply/{id}", id)));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockAdmin
    @Test
    public void deleteBoard_test() throws Exception {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "pblee", branch);
        Board board = newMockBoard(1L, "title", pb);

        // stub
        Mockito.doNothing().when(backOfficeService).deleteBoard(any());

        // When
        ResultActions resultActions = mvc.perform((delete("/admin/board/{id}", id)));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockAdmin
    @Test
    public void getMembers_test() throws Exception {
        // given
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPBWithStatus(1L, "pblee", branch, PBStatus.PENDING);
        User user = newMockUserADMIN(1L, "관리자");
        Reservation reservation = newMockCallReservation(1L, user, pb, ReservationProcess.APPLY);
        Review review = newMockReview(1L, reservation);
        List reviewList = Arrays.asList(new BackOfficeResponse.ReservationTotalDTO(reservation,
                new BackOfficeResponse.UserDTO(reservation.getUser()),
                new BackOfficeResponse.PBDTO(reservation.getPb()),
                new BackOfficeResponse.ReviewTotalDTO(review,
                        Arrays.asList(new ReservationResponse.StyleDTO(StyleStyle.FAST)))));
        Page<Reservation> reservationPG = new PageImpl<>(Arrays.asList(reservation));
        PageDTO<BackOfficeResponse.ReservationTotalDTO> pageDTO = new PageDTO<>(reviewList, reservationPG, Reservation.class);

        // stub
        Mockito.when(backOfficeService.getReservations(any())).thenReturn(pageDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/admin/reservations"));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].process").value("APPLY"));
        resultActions.andExpect(jsonPath("$.data.list[0].status").value("ACTIVE"));
        resultActions.andExpect(jsonPath("$.data.list[0].time").value(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분"))));
        resultActions.andExpect(jsonPath("$.data.list[0].type").value("CALL"));
        resultActions.andExpect(jsonPath("$.data.list[0].locationName").value("kb증권 강남중앙점"));
        resultActions.andExpect(jsonPath("$.data.list[0].goal").value("PROFIT"));
        resultActions.andExpect(jsonPath("$.data.list[0].question").value("질문입니다..."));
        resultActions.andExpect(jsonPath("$.data.list[0].user.id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].user.email").value("jisu8496@naver.com"));
        resultActions.andExpect(jsonPath("$.data.list[0].user.name").value("관리자"));
        resultActions.andExpect(jsonPath("$.data.list[0].user.phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.list[0].user.isAdmin").value("true"));
        resultActions.andExpect(jsonPath("$.data.list[0].pb.id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].pb.email").value("pblee@nate.com"));
        resultActions.andExpect(jsonPath("$.data.list[0].pb.name").value("pblee"));
        resultActions.andExpect(jsonPath("$.data.list[0].pb.phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.list[0].review.content").value("content 입니다"));
        resultActions.andExpect(jsonPath("$.data.list[0].review.adherence").value("EXCELLENT"));
        resultActions.andExpect(jsonPath("$.data.list[0].review.styles[0].style").value("FAST"));
    }

    @WithMockAdmin
    @Test
    public void forceWithdrawUser_test() throws Exception {
        // given
        Long id = 1L;
        User user = newMockUser(1L, "user");

        // stub
        Mockito.doNothing().when(backOfficeService).forceWithdraw(any(), any());

        // When
        ResultActions resultActions = mvc.perform((delete("/admin/user/{id}", id)));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockAdmin
    @Test
    public void forceWithdrawPB_test() throws Exception {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "pblee", branch);

        // stub
        Mockito.doNothing().when(backOfficeService).forceWithdraw(any(), any());

        // When
        ResultActions resultActions = mvc.perform((delete("/admin/pb/{id}", id)));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockAdmin
    @Test
    public void authorizeAdmin_test() throws Exception {
        // given
        Long id = 1L;
        User user = newMockUser(1L, "user");

        // stub
        Mockito.doNothing().when(backOfficeService).authorizeAdmin(any(), any());

        // When
        ResultActions resultActions = mvc.perform((post("/admin/user/{id}?admin=true", id)));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockAdmin
    @Test
    public void getReservations_test() throws Exception {
        // given
        User user = newMockUserADMIN(1L, "관리자");
        List userList = Arrays.asList(new BackOfficeResponse.UserDTO(user));
        Page<User> userPG = new PageImpl<>(Arrays.asList(user));

        // stub
        Mockito.when(backOfficeService.getUsers(any(), any(), any())).thenReturn(new PageDTO<>(userList, userPG, User.class));

        // When
        ResultActions resultActions = mvc.perform(get("/admin/users"));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].email").value("jisu8496@naver.com"));
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("관리자"));
        resultActions.andExpect(jsonPath("$.data.list[0].phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.list[0].isAdmin").value("true"));
        resultActions.andExpect(jsonPath("$.data.totalElements").isNumber());
        resultActions.andExpect(jsonPath("$.data.totalPages").value("1"));
        resultActions.andExpect(jsonPath("$.data.curPage").value("0"));
        resultActions.andExpect(jsonPath("$.data.first").value("true"));
        resultActions.andExpect(jsonPath("$.data.last").value("true"));
        resultActions.andExpect(jsonPath("$.data.empty").value("false"));
    }

//    @WithMockAdmin
//    @Test
//    public void approvePB_test() throws Exception {
//        // given
//        Long id = 1L;
//        Company company = newMockCompany(1L, "미래에셋증권");
//        Branch branch = newMockBranch(1L, company, 1);
//        PB pb = newMockPBWithStatus(id, "pblee", branch, PBStatus.PENDING);
//
//        // stub
//        Mockito.doNothing().when(backOfficeService).approvePB(any(), any());
//
//        // When
//        ResultActions resultActions = mvc.perform(post("/admin/pb/{id}?approve=true", id));
//
//        // Then
//        resultActions.andExpect(status().isOk());
//        resultActions.andExpect(jsonPath("$.status").value(200));
//        resultActions.andExpect(jsonPath("$.msg").value("ok"));
//        resultActions.andExpect(jsonPath("$.data").isEmpty());
//    }

    @WithMockAdmin
    @Test
    public void getPBPending_test() throws Exception {
        // given
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "pblee", branch);
        List<BackOfficeResponse.PBPendingDTO> list = Arrays.asList(new BackOfficeResponse.PBPendingDTO(pb, pb.getBranch().getName()));
        Page<PB> pbPG = new PageImpl<>(Arrays.asList(pb));
        PageDTO<BackOfficeResponse.PBPendingDTO> pageDTO = new PageDTO<>(list, pbPG, PB.class);
        // stub
        Mockito.when(backOfficeService.getPBPending(any())).thenReturn(pageDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/admin/pendings"));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].email").value("pblee@nate.com"));
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("pblee"));
        resultActions.andExpect(jsonPath("$.data.list[0].phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.list[0].branchName").value("미래에셋증권 여의도점"));
        resultActions.andExpect(jsonPath("$.data.list[0].career").value("10"));
        resultActions.andExpect(jsonPath("$.data.list[0].speciality1").value("BOND"));
        resultActions.andExpect(jsonPath("$.data.list[0].speciality2").isEmpty());
        resultActions.andExpect(jsonPath("$.data.list[0].businessCard").value("card.png"));
        resultActions.andExpect(jsonPath("$.data.totalElements").value("1"));
        resultActions.andExpect(jsonPath("$.data.totalPages").value("1"));
        resultActions.andExpect(jsonPath("$.data.curPage").value("0"));
        resultActions.andExpect(jsonPath("$.data.first").value("true"));
        resultActions.andExpect(jsonPath("$.data.last").value("true"));
        resultActions.andExpect(jsonPath("$.data.empty").value("false"));
    }

    @Test
    public void getNotices_test() throws Exception {
        // given
        Notice notice = newMockNotice(1L);
        List<BackOfficeResponse.NoticeDTO> list = Arrays.asList(new BackOfficeResponse.NoticeDTO(notice));
        Page<Notice> noticePG = new PageImpl<>(Arrays.asList(notice));
        PageDTO<BackOfficeResponse.NoticeDTO> noticeDTO = new PageDTO<>(list, noticePG, Notice.class);
        // stub
        Mockito.when(backOfficeService.getNotices(any())).thenReturn(noticeDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/notices"));

        // Then
        resultActions.andExpect(status().isOk());
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
    }

    @Test
    public void getNotice_test() throws Exception {
        // given
        Notice notice = newMockNotice(1L);
        BackOfficeResponse.NoticeDTO noticeDTO = new BackOfficeResponse.NoticeDTO(notice);

        // stub
        Mockito.when(backOfficeService.getNotice(any())).thenReturn(noticeDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/notice/{id}", notice.getId()));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.id").value("1"));
        resultActions.andExpect(jsonPath("$.data.title").value("서버 점검 안내"));
        resultActions.andExpect(jsonPath("$.data.content").value("보다 나은 환경을 제공하기 위해 개발진에서 발견한 문제 복구 및 업데이트 점검을 진행할 예정입니다.\n" +
                "업데이트 점검을 진행하는 동안에는 접속할 수 없으니 불필요한 손해가 발생치 않도록 주의해 주세요.\n" +
                "이로 인해 불편을 끼쳐 드려 죄송합니다."));
    }

    @Test
    public void getFAQs_test() throws Exception {
        // given
        FrequentQuestion faq = newMockFrequentQuestion(1L);
        List<BackOfficeResponse.FAQDTO> list = Arrays.asList(new BackOfficeResponse.FAQDTO(faq));
        Page<FrequentQuestion> faqPG = new PageImpl<>(Arrays.asList(faq));
        PageDTO<BackOfficeResponse.FAQDTO> faqDTO = new PageDTO<>(list, faqPG, FrequentQuestion.class);
        // stub
        Mockito.when(backOfficeService.getFAQs(any())).thenReturn(faqDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/faqs"));

        // Then
        resultActions.andExpect(status().isOk());
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
    }

    @Test
    public void getFAQ_test() throws Exception {
        // given
        FrequentQuestion faq = newMockFrequentQuestion(1L);
        BackOfficeResponse.FAQDTO faqDTO = new BackOfficeResponse.FAQDTO(faq);

        // stub
        Mockito.when(backOfficeService.getFAQ(anyLong())).thenReturn(faqDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/faq/{id}", faq.getId()));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.id").value("1"));
        resultActions.andExpect(jsonPath("$.data.label").value("회원"));
        resultActions.andExpect(jsonPath("$.data.title").value("이메일이 주소가 변경되었어요."));
        resultActions.andExpect(jsonPath("$.data.content").value("가입 이메일은 회원 식별 고유 키로 " +
                "가입 후 변경이 불가능 하므로 개인 이메일로 가입하기를 권유드립니다."));
    }

    @WithMockAdmin
    @Test
    public void update_notice_test() throws Exception {
        // given
        Long noticeId = 1L;
        BackOfficeRequest.UpdateNoticeDTO updateNoticeDTO = new BackOfficeRequest.UpdateNoticeDTO();
        updateNoticeDTO.setTitle("제목 수정");
        updateNoticeDTO.setContent("내용 수정");
        String requestBody = om.writeValueAsString(updateNoticeDTO);

        // stub
        Mockito.doNothing().when(backOfficeService).updateNotice(anyLong(), any());

        // when
        ResultActions resultActions = mvc.perform(
                patch("/admin/notice/{id}", noticeId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockAdmin
    @Test
    public void delete_notice_test() throws Exception {
        // given
        Long noticeId = 1L;

        // stub
        Mockito.doNothing().when(backOfficeService).deleteNotice(anyLong());

        // when
        ResultActions resultActions = mvc.perform(delete("/admin/notice/{id}", noticeId));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockAdmin
    @Test
    public void update_faq_test() throws Exception {
        // given
        Long faqId = 1L;
        BackOfficeRequest.UpdateFAQDTO updateFAQDTO = new BackOfficeRequest.UpdateFAQDTO();
        updateFAQDTO.setLabel("라벨");
        updateFAQDTO.setTitle("제목 수정");
        updateFAQDTO.setContent("내용 수정");
        String requestBody = om.writeValueAsString(updateFAQDTO);

        // stub
        Mockito.doNothing().when(backOfficeService).updateFAQ(anyLong(), any());

        // when
        ResultActions resultActions = mvc.perform(
                patch("/admin/faq/{id}", faqId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockAdmin
    @Test
    public void delete_faq_test() throws Exception {
        // given
        Long faqId = 1L;

        // stub
        Mockito.doNothing().when(backOfficeService).deleteFAQ(anyLong());

        // when
        ResultActions resultActions = mvc.perform(delete("/admin/faq/{id}", faqId));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }
}
