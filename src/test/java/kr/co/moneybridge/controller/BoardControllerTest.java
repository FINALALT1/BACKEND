package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.board.BoardRepository;
import kr.co.moneybridge.model.pb.*;
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

@DisplayName("컨텐츠 리스트 관련 API")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BoardControllerTest {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private EntityManager em;

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private PBRepository pbRepository;

    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    public void setUp() {
        Company companyPS = companyRepository.save(dummy.newCompany("미래에셋증권"));
        Branch branchPS = branchRepository.save(dummy.newBranch(companyPS, 1));
        PB pbPS = pbRepository.save(dummy.newPB("이피비", branchPS));
        Board boardPS1 = boardRepository.save(dummy.newBoard("제목1입니다", pbPS));
        Board boardPS2 = boardRepository.save(dummy.newTempBoard("임시 제목1입니다", pbPS));
        Board boardPS3 = boardRepository.save(dummy.newBoard("컨텐츠 타이들입니다", pbPS));
        Board boardPS4 = boardRepository.save(dummy.newBoard("제목2입니다", pbPS));
        em.clear();
    }

    @DisplayName("컨텐츠 검색하기")
    @Test
    void getBoardsWithTitle() throws Exception {
        //given
        String search = "제목";
        //when
        ResultActions resultActions = mvc.perform(get("/lounge/boards").param("search", search));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.totalElements").value(2));
        resultActions.andExpect(jsonPath("$.data.totalPages").value(1));
        resultActions.andExpect(jsonPath("$.data.curPage").value(0));
    }

    @DisplayName("최신 컨텐츠순 가져오기")
    @Test
    void getBoardsByNew() throws Exception {

        //given

        //when
        ResultActions resultActions = mvc.perform(get("/boards"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.totalElements").value(3));
        resultActions.andExpect(jsonPath("$.data.totalPages").value(1));
        resultActions.andExpect(jsonPath("$.data.curPage").value(0));
    }

    @DisplayName("핫한 컨텐츠순 가져오기")
    @Test
    void getBoardsByHot() throws Exception {

        //given

        //when
        ResultActions resultActions = mvc.perform(get("/boards/hot"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.totalElements").value(3));
        resultActions.andExpect(jsonPath("$.data.totalPages").value(1));
        resultActions.andExpect(jsonPath("$.data.curPage").value(0));
    }

    @DisplayName("최신컨텐츠 2개 + 핫한컨텐츠 2개 가져오기")
    @Test
    void getNewHotBoards() throws Exception {

        //given

        //when
        ResultActions resultActions = mvc.perform(get("/lounge/board"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].title").value("제목2입니다"));
        resultActions.andExpect(jsonPath("$.data.list[1].title").value("컨텐츠 타이들입니다"));
        resultActions.andExpect(jsonPath("$.data.list[0].career").value(10));
        resultActions.andExpect(jsonPath("$.data.list[1].pbName").value("이피비"));
    }
}