package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.WithMockAdmin;
import kr.co.moneybridge.core.WithMockPB;
import kr.co.moneybridge.core.advice.MyLogAdvice;
import kr.co.moneybridge.core.advice.MyValidAdvice;
import kr.co.moneybridge.core.config.MyFilterRegisterConfig;
import kr.co.moneybridge.core.config.MySecurityConfig;
import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.core.util.MyMemberUtil;
import kr.co.moneybridge.core.util.RedisUtil;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.backOffice.BackOfficeResponse;
import kr.co.moneybridge.model.backoffice.FrequentQuestion;
import kr.co.moneybridge.model.backoffice.Notice;
import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.model.pb.Company;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBStatus;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.service.BackOfficeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@EnableAspectJAutoProxy
@Import({
        MyLogAdvice.class,
        MyValidAdvice.class,
        MyFilterRegisterConfig.class,
        MySecurityConfig.class,
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
    private MyMemberUtil myMemberUtil;

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
    public void getMembers_test() throws Exception {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPBWithStatus(id, "pblee", branch, PBStatus.PENDING);
        List pbList = Arrays.asList(new BackOfficeResponse.PBDTO(pb));
        User user = newMockUserADMIN(1L, "관리자");
        List userList = Arrays.asList( new BackOfficeResponse.UserDTO(user));
        Page<PB> pbPG =  new PageImpl<>(Arrays.asList(pb));
        Page<User> userPG =  new PageImpl<>(Arrays.asList(user));
        BackOfficeResponse.MemberOutDTO memberOutDTO = new BackOfficeResponse.MemberOutDTO(
                new BackOfficeResponse.CountDTO(2,1,1),
                new PageDTO<>(userList, userPG, User.class),
                new PageDTO<>(pbList, pbPG, PB.class));

        // stub
        Mockito.when(backOfficeService.getMembers(any())).thenReturn(memberOutDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/admin/members"));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.memberCount.total").value("2"));
        resultActions.andExpect(jsonPath("$.data.memberCount.user").value("1"));
        resultActions.andExpect(jsonPath("$.data.memberCount.pb").value("1"));
        resultActions.andExpect(jsonPath("$.data.userPage.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.userPage.list[0].email").value("jisu8496@naver.com"));
        resultActions.andExpect(jsonPath("$.data.userPage.list[0].name").value("관리자"));
        resultActions.andExpect(jsonPath("$.data.userPage.list[0].phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.userPage.list[0].isAdmin").value("true"));
        resultActions.andExpect(jsonPath("$.data.userPage.totalElements").isNumber());
        resultActions.andExpect(jsonPath("$.data.userPage.totalPages").value("1"));
        resultActions.andExpect(jsonPath("$.data.userPage.curPage").value("0"));
        resultActions.andExpect(jsonPath("$.data.userPage.first").value("true"));
        resultActions.andExpect(jsonPath("$.data.userPage.last").value("true"));
        resultActions.andExpect(jsonPath("$.data.userPage.empty").value("false"));
        resultActions.andExpect(jsonPath("$.data.pbPage.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.pbPage.list[0].email").value("pblee@nate.com"));
        resultActions.andExpect(jsonPath("$.data.pbPage.list[0].name").value("pblee"));
        resultActions.andExpect(jsonPath("$.data.pbPage.list[0].phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.pbPage.totalElements").isNumber());
        resultActions.andExpect(jsonPath("$.data.pbPage.totalPages").value("1"));
        resultActions.andExpect(jsonPath("$.data.pbPage.curPage").value("0"));
        resultActions.andExpect(jsonPath("$.data.pbPage.first").value("true"));
        resultActions.andExpect(jsonPath("$.data.pbPage.last").value("true"));
        resultActions.andExpect(jsonPath("$.data.pbPage.empty").value("false"));
    }

    @WithMockAdmin
    @Test
    public void approvePB_test() throws Exception {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPBWithStatus(id, "pblee", branch, PBStatus.PENDING);

        // stub
        Mockito.doNothing().when(backOfficeService).approvePB(any(), any());

        // When
        ResultActions resultActions = mvc.perform(post("/admin/pb/{id}?approve=true", id));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockAdmin
    @Test
    public void getPBPending_test() throws Exception {
        // given
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "pblee", branch);
        List<BackOfficeResponse.PBPendingDTO> list = Arrays.asList(new BackOfficeResponse.PBPendingDTO(pb, pb.getBranch().getName()));
        Page<PB> pbPG =  new PageImpl<>(Arrays.asList(pb));
        BackOfficeResponse.PBPendingOutDTO pbPendingPageDTO = new BackOfficeResponse.PBPendingOutDTO(
                pbPG.getContent().size(), new PageDTO<>(list, pbPG, PB.class));
        // stub
        Mockito.when(backOfficeService.getPBPending(any())).thenReturn(pbPendingPageDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/admin/pbs"));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.count").value("1"));
        resultActions.andExpect(jsonPath("$.data.page.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.page.list[0].email").value("pblee@nate.com"));
        resultActions.andExpect(jsonPath("$.data.page.list[0].name").value("pblee"));
        resultActions.andExpect(jsonPath("$.data.page.list[0].phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.page.list[0].branchName").value("미래에셋증권 여의도점"));
        resultActions.andExpect(jsonPath("$.data.page.list[0].career").value("10"));
        resultActions.andExpect(jsonPath("$.data.page.list[0].speciality1").value("BOND"));
        resultActions.andExpect(jsonPath("$.data.page.list[0].speciality2").isEmpty());
        resultActions.andExpect(jsonPath("$.data.page.list[0].businessCard").value("card.png"));
        resultActions.andExpect(jsonPath("$.data.page.totalElements").value("1"));
        resultActions.andExpect(jsonPath("$.data.page.totalPages").value("1"));
        resultActions.andExpect(jsonPath("$.data.page.curPage").value("0"));
        resultActions.andExpect(jsonPath("$.data.page.first").value("true"));
        resultActions.andExpect(jsonPath("$.data.page.last").value("true"));
        resultActions.andExpect(jsonPath("$.data.page.empty").value("false"));
    }

    @Test
    public void getNotice_test() throws Exception {
        // given
        Notice notice = newMockNotice(1L);
        List<BackOfficeResponse.NoticeDTO> list = Arrays.asList(new BackOfficeResponse.NoticeDTO(notice));
        Page<Notice> noticePG =  new PageImpl<>(Arrays.asList(notice));
        PageDTO<BackOfficeResponse.NoticeDTO> noticeDTO = new PageDTO<>(list, noticePG, Notice.class);
        // stub
        Mockito.when(backOfficeService.getNotice(any())).thenReturn(noticeDTO);

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
    public void getFAQ_test() throws Exception {
        // given
        FrequentQuestion faq = newMockFrequentQuestion(1L);
        List<BackOfficeResponse.FAQDTO> list = Arrays.asList(new BackOfficeResponse.FAQDTO(faq));
        Page<FrequentQuestion> faqPG =  new PageImpl<>(Arrays.asList(faq));
        PageDTO<BackOfficeResponse.FAQDTO> faqDTO = new PageDTO<>(list, faqPG, FrequentQuestion.class);
        // stub
        Mockito.when(backOfficeService.getFAQ(any())).thenReturn(faqDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/FAQ"));

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
}
