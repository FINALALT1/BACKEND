package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.model.backoffice.FrequentQuestionRepository;
import kr.co.moneybridge.model.backoffice.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @BeforeEach
    public void setUp() {
        frequentQuestionRepository.save(dummy.newFrequentQuestion());
        noticeRepository.save(dummy.newNotice());
        em.clear();
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
        resultActions.andExpect(jsonPath("$.data.list[0].date").value("2023-06-18"));
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
                .perform(get("/FAQ"));
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
