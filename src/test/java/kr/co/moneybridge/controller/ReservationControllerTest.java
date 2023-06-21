package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.dto.reservation.ReservationRequest;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("예약 관련 API")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ReservationControllerTest {
    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private EntityManager em;
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

    @BeforeEach
    public void setUp() {
        User userPS = userRepository.save(dummy.newUser("lee"));
        Company companyPS = companyRepository.save(dummy.newCompany("미래에셋증권"));
        Branch branchPS = branchRepository.save(dummy.newBranch(companyPS, 1));
        PB pbPS = pbRepository.save(dummy.newPB("pblee", branchPS));

        Reservation reservation = reservationRepository.save(dummy.newVisitReservation(userPS, pbPS, ReservationProcess.COMPLETE));
        Reservation reservation2 = reservationRepository.save(dummy.newVisitReservation(userPS, pbPS, ReservationProcess.COMPLETE));
        Reservation reservation3 = reservationRepository.save(dummy.newVisitReservation(userPS, pbPS, ReservationProcess.COMPLETE));
        Reservation reservation4 = reservationRepository.save(dummy.newVisitReservation(userPS, pbPS, ReservationProcess.COMPLETE));
        Reservation reservation5 = reservationRepository.save(dummy.newVisitReservation(userPS, pbPS, ReservationProcess.COMPLETE));
        reviewRepository.save(dummy.newReview(reservation));
        reviewRepository.save(dummy.newReview(reservation2));
        reviewRepository.save(dummy.newReview(reservation3));
        reviewRepository.save(dummy.newReview(reservation4));
        reviewRepository.save(dummy.newReview(reservation5));

        em.clear();
    }

    @DisplayName("현재 나의 상담 가능 시간 불러오기")
    @WithUserDetails(value = "PB-pblee@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_my_consult_time_test() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/pb/consultTime"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.consultStart").value("09:00"));
        resultActions.andExpect(jsonPath("$.data.consultEnd").value("18:00"));
        resultActions.andExpect(jsonPath("$.data.consultNotice").value("월요일 불가능합니다"));
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("나의 후기 하나 가져오기 성공")
    @WithUserDetails(value = "USER-lee@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_my_review_test() throws Exception {
        // given
        Long id = 1L;

        // when
        ResultActions resultActions = mvc
                .perform(get("/user/review/{id}", id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.adherence").value("EXCELLENT"));
        resultActions.andExpect(jsonPath("$.data.styleList").isEmpty());
        resultActions.andExpect(jsonPath("$.data.content").value("content 입니다"));
        resultActions.andExpect(status().isOk());
    }

//    @DisplayName("상담 예약 사전 정보 조회 성공")
//    @WithUserDetails(value = "USER-lee@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void get_reservation_base_test() throws Exception {
//        // given
//        Long pbId = 1L;
//
//        // when
//        ResultActions resultActions = mvc
//                .perform(get("/user/reservation/{pbId}", pbId));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // then
//        resultActions.andExpect(jsonPath("$.status").value(200));
//        resultActions.andExpect(jsonPath("$.msg").value("ok"));
//        resultActions.andExpect(jsonPath("$.data.pbInfo.pbName").value("pblee"));
//        resultActions.andExpect(jsonPath("$.data.pbInfo.branchName").value("미래에셋증권 여의도점"));
//        resultActions.andExpect(jsonPath("$.data.pbInfo.branchAddress").value("미래에셋증권 도로명주소"));
//        resultActions.andExpect(jsonPath("$.data.pbInfo.branchLatitude").value("37.36671"));
//        resultActions.andExpect(jsonPath("$.data.pbInfo.branchLongitude").value("128.34451"));
//        resultActions.andExpect(jsonPath("$.data.consultInfo.consultStart").value("09:00"));
//        resultActions.andExpect(jsonPath("$.data.consultInfo.consultEnd").value("18:00"));
//        resultActions.andExpect(jsonPath("$.data.consultInfo.notice").value("월요일 불가능합니다"));
//        resultActions.andExpect(jsonPath("$.data.userInfo.userName").value("lee"));
//        resultActions.andExpect(jsonPath("$.data.userInfo.userPhoneNumber").value("01012345678"));
//        resultActions.andExpect(jsonPath("$.data.userInfo.userEmail").value("lee@nate.com"));
//        resultActions.andExpect(status().isOk());
//    }

    @DisplayName("상담 예약 신청하기 성공")
    @WithUserDetails(value = "USER-lee@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void apply_reservation_test() throws Exception {
        // given
        Long pbId = 1L;
        ReservationRequest.ApplyDTO applyDTO = new ReservationRequest.ApplyDTO();
        applyDTO.setGoal(ReservationGoal.PROFIT);
        applyDTO.setReservationType(ReservationType.VISIT);
        applyDTO.setLocationType(LocationType.BRANCH);
        applyDTO.setCandidateTime1(LocalDateTime.now().plusHours(10));
        applyDTO.setCandidateTime2(LocalDateTime.now().plusHours(10));
        applyDTO.setQuestion("잘 부탁드립니다.");
        applyDTO.setUserName("lee");
        applyDTO.setUserPhoneNumber("01012345678");
        applyDTO.setUserEmail("asdf1234@nate.com");

        String requestBody = om.writeValueAsString(applyDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/user/reservation/{pbId}", pbId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("상담 후기 리스트 조회 성공")
    @WithUserDetails(value = "PB-pblee@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_reviews_test() throws Exception {
        // given
        int page = 0;
        MyUserDetails myUserDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // when
        ResultActions resultActions = mvc
                .perform(get("/pb/reviews"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
    }
}
