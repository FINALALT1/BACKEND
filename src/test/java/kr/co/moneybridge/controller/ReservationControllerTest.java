package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.dto.reservation.ReservationRequest;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.reservation.LocationType;
import kr.co.moneybridge.model.reservation.ReservationGoal;
import kr.co.moneybridge.model.reservation.ReservationType;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;

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

    @BeforeEach
    public void setUp() {
        User userPS = userRepository.save(dummy.newUser("lee"));
        Company companyPS = companyRepository.save(dummy.newCompany("미래에셋증권"));
        Branch branchPS = branchRepository.save(dummy.newBranch(companyPS, 1));
        PB pbPS = pbRepository.save(dummy.newPB("이피비", branchPS));

        em.clear();
    }

    @DisplayName("상담 예약 사전 정보 조회 성공")
    @WithUserDetails(value = "USER-lee@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_reservation_base_test() throws Exception {
        // given
        Long pbId = 1L;

        // when
        ResultActions resultActions = mvc
                .perform(get("/user/reservation/{pbId}", pbId));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.pbInfo.pbName").value("이피비"));
        resultActions.andExpect(jsonPath("$.data.pbInfo.branchName").value("미래에셋증권 여의도점"));
        resultActions.andExpect(jsonPath("$.data.pbInfo.branchAddress").value("미래에셋증권 도로명주소"));
        resultActions.andExpect(jsonPath("$.data.pbInfo.branchLatitude").value("37.36671"));
        resultActions.andExpect(jsonPath("$.data.pbInfo.branchLongitude").value("128.34451"));
        resultActions.andExpect(jsonPath("$.data.consultInfo.consultStart").value("09:00"));
        resultActions.andExpect(jsonPath("$.data.consultInfo.consultEnd").value("18:00"));
        resultActions.andExpect(jsonPath("$.data.consultInfo.notice").value("월요일 불가능합니다"));
        resultActions.andExpect(jsonPath("$.data.userInfo.userName").value("lee"));
        resultActions.andExpect(jsonPath("$.data.userInfo.userPhoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.userInfo.userEmail").value("lee@nate.com"));
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("상담 예약 신청하기 성공")
    @WithUserDetails(value = "USER-lee@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void apply_reservation_test() throws Exception {
        // given
        Long pbId = 1L;
        ReservationRequest.ApplyReservationInDTO applyReservationInDTO = new ReservationRequest.ApplyReservationInDTO();
        applyReservationInDTO.setGoal1(ReservationGoal.PROFIT);
        applyReservationInDTO.setGoal2(ReservationGoal.RISK);
        applyReservationInDTO.setReservationType(ReservationType.VISIT);
        applyReservationInDTO.setLocationType(LocationType.BRANCH);
        applyReservationInDTO.setLocationName("미래에셋증권 용산wm점");
        applyReservationInDTO.setLocationAddress("서울특별시 용산구 한강로동 한강대로 92");
        applyReservationInDTO.setCandidateTime1("2023-05-15T09:00:00");
        applyReservationInDTO.setCandidateTime2("2023-05-15T12:00:00");
        applyReservationInDTO.setQuestion("잘 부탁드립니다.");
        applyReservationInDTO.setUserName("lee");
        applyReservationInDTO.setUserPhoneNumber("01012345678");
        applyReservationInDTO.setUserEmail("asdf1234@nate.com");

        String requestBody = om.writeValueAsString(applyReservationInDTO);

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
}
