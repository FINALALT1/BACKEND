package kr.co.moneybridge.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.dto.board.BoardRequest;
import kr.co.moneybridge.dto.board.ReplyRequest;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.persistence.EntityManager;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private ReReplyRepository reReplyRepository;

    @BeforeEach
    public void setUp() {
        Company companyPS = companyRepository.save(dummy.newCompany("미래에셋증권"));
        Branch branchPS = branchRepository.save(dummy.newBranch(companyPS, 1));
        PB pbPS = pbRepository.save(dummy.newPB("이피비", branchPS));
        User user = userRepository.save(dummy.newUser("김테스트"));
        Board boardPS1 = boardRepository.save(dummy.newBoard("제목1입니다", pbPS));
        Board boardPS2 = boardRepository.save(dummy.newTempBoard("임시 제목1입니다", pbPS));
        Board boardPS3 = boardRepository.save(dummy.newBoard("컨텐츠 타이들입니다", pbPS));
        Board boardPS4 = boardRepository.save(dummy.newBoard("제목2입니다", pbPS));
        Reply reply1 = replyRepository.save(dummy.newPBReply(boardPS1, pbPS));
        ReReply reReply1 = reReplyRepository.save(dummy.newPBReReply(reply1, pbPS));
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

    @DisplayName("컨텐츠 상세 가져오기(로그인)")
    @WithUserDetails(value = "PB-이피비@nate.com",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getBoardDetail() throws Exception {

        //given

        //when
        ResultActions resultActions = mvc.perform(get("/auth/board/1"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.data.id").value(1));
        resultActions.andExpect(jsonPath("$.data.thumbnail").value("thumbnail.png"));
        resultActions.andExpect(jsonPath("$.data.tag1").value("시장정보"));
        resultActions.andExpect(jsonPath("$.data.tag2").value("쉽게읽혀요"));
        resultActions.andExpect(jsonPath("$.data.title").value("제목1입니다"));
    }

    @DisplayName("컨텐츠 북마크하기")
    @WithUserDetails(value = "PB-이피비@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void addBoardBookmark() throws Exception {
        //given
        Long boardId = 1L;

        //when
        ResultActions resultActions = mvc.perform(post("/auth/bookmark/board/{id}", boardId));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("컨텐츠 댓글달기")
    @WithUserDetails(value = "PB-이피비@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void postReply() throws Exception {
        //given
        Long boardId = 1L;
        ReplyRequest.ReplyInDTO replyInDTO = new ReplyRequest.ReplyInDTO();
        replyInDTO.setContent("This is a test reply.");

        String replyJson = om.writeValueAsString(replyInDTO);

        //when
        ResultActions resultActions = mvc.perform(post("/auth/board/{id}/reply", boardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(replyJson));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("댓글 수정하기")
    @WithUserDetails(value = "PB-이피비@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateReply() throws Exception {
        //given
        Long replyId = 1L;
        ReplyRequest.ReplyInDTO replyInDTO = new ReplyRequest.ReplyInDTO();
        replyInDTO.setContent("This is a modified test reply.");

        String replyJson = om.writeValueAsString(replyInDTO);

        //when
        ResultActions resultActions = mvc.perform(patch("/auth/board/reply/{replyId}", replyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(replyJson));

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("댓글 삭제하기")
    @WithUserDetails(value = "PB-이피비@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void deleteReply() throws Exception {
        //given
        Long replyId = 1L;

        //when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.delete("/auth/board/reply/{replyId}", replyId));

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("대댓글 수정하기")
    @WithUserDetails(value = "PB-이피비@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateReReply() throws Exception {
        //given
        Long reReplyId = 1L;
        ReplyRequest.ReReplyInDTO reReplyInDTO = new ReplyRequest.ReReplyInDTO();
        reReplyInDTO.setContent("This is a modified test re-reply.");

        String reReplyJson = om.writeValueAsString(reReplyInDTO);

        //when
        ResultActions resultActions = mvc.perform(patch("/auth/board/rereply/{rereplyId}", reReplyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(reReplyJson));

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("컨텐츠 등록하기")
    @WithUserDetails(value = "PB-이피비@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void saveBoard() throws Exception {
        //given
        String title = "타이틀";
        String content = "내용";
        String tag1 = "태그1";
        String tag2 = "태그2";

        MockMultipartFile thumbnailFile = new MockMultipartFile("thumbnail", "test.jpg", "image/png", new FileInputStream("./src/main/resources/businessCard.png"));

        BoardRequest.BoardInDTO boardInDTO = new BoardRequest.BoardInDTO();
        boardInDTO.setTitle(title);
        boardInDTO.setContent(content);
        boardInDTO.setTag1(tag1);
        boardInDTO.setTag2(tag2);

        String boardJson = om.writeValueAsString(boardInDTO);
        MockMultipartFile json = new MockMultipartFile("boardInDTO", "", "application/json", boardJson.getBytes(StandardCharsets.UTF_8));

        //when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.multipart("/pb/board")
                .file(thumbnailFile)
                .file(json));

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
    }
}