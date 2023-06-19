package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.WithMockPB;
import kr.co.moneybridge.core.WithMockUser;
import kr.co.moneybridge.core.advice.MyLogAdvice;
import kr.co.moneybridge.core.advice.MyValidAdvice;
import kr.co.moneybridge.core.config.MyFilterRegisterConfig;
import kr.co.moneybridge.core.config.MySecurityConfig;
import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.core.util.MyMemberUtil;
import kr.co.moneybridge.core.util.RedisUtil;
import kr.co.moneybridge.dto.reservation.ReservationRequest;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.model.pb.Company;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
        RedisUtil.class,
})
@WebMvcTest(
        controllers = {ReservationController.class}
)
public class ReservationControllerUnitTest extends MockDummyEntity {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean
    private ReservationService reservationService;
    @MockBean
    private RedisTemplate redisTemplate;
    @MockBean
    private MyMemberUtil myMemberUtil;

    @WithMockPB
    @Test
    public void get_my_consult_time_test() throws Exception {
        // given
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "이피비", branch);

        ReservationResponse.MyConsultTimeDTO myConsultTimeDTO = new ReservationResponse.MyConsultTimeDTO(pb);

        // stub
        Mockito.when(reservationService.getMyConsultTime(anyLong())).thenReturn(myConsultTimeDTO);

        // when
        ResultActions resultActions = mvc.perform(get("/pb/consultTime"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.consultStart").value("09:00:00"));
        resultActions.andExpect(jsonPath("$.data.consultEnd").value("18:00:00"));
        resultActions.andExpect(jsonPath("$.data.consultNotice").value("월요일 불가능합니다"));
        resultActions.andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    public void get_my_review_test() throws Exception {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "이피비", branch);
        User user = newMockUser(1L, "lee");
        Reservation reservation = newMockCallReservation(id, user, pb, ReservationProcess.COMPLETE);
        Review review = newMockReview(1L, reservation);
        List<ReservationResponse.StyleDTO> styleList = new ArrayList<>();

        ReservationResponse.MyReviewDTO myReviewDTO = new ReservationResponse.MyReviewDTO(
                review, styleList);

        // stub
        Mockito.when(reservationService.getMyReview(anyLong(), any())).thenReturn(myReviewDTO);

        // when
        ResultActions resultActions = mvc.perform(get("/user/review/{id}", id));
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

//    @WithMockUser
//    @Test
//    public void get_reservation_base_test() throws Exception {
//        // given
//        Long pbId = 1L;
//        Company company = newMockCompany(1L, "미래에셋");
//        Branch branch = newMockBranch(1L, company, 1);
//        PB pb = newMockPB(pbId, "이피비", branch);
//        User user = newMockUser(1L, "lee");
//
//        // stub
//        Mockito.when(reservationService.getReservationBase(anyLong(), any()))
//                .thenReturn(new ReservationResponse.BaseDTO(
//                        new ReservationResponse.PBInfoDTO(
//                                pb.getName(),
//                                pb.getBranch().getName(),
//                                pb.getBranch().getRoadAddress(),
//                                pb.getBranch().getLatitude(),
//                                pb.getBranch().getLongitude()
//                        ),
//                        new ReservationResponse.ConsultInfoDTO(
//                                MyDateUtil.localTimeToString(pb.getConsultStart()),
//                                MyDateUtil.localTimeToString(pb.getConsultEnd()),
//                                pb.getConsultNotice()
//                        ),
//                        new ReservationResponse.UserInfoDTO(
//                                user.getName(),
//                                user.getPhoneNumber(),
//                                user.getEmail()
//                        )
//                ));
//
//        // when
//        ResultActions resultActions = mvc.perform(get("/user/reservation/{pbId}", pbId));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // then
//        resultActions.andExpect(jsonPath("$.data.pbInfo.pbName").value(pb.getName()));
//        resultActions.andExpect(jsonPath("$.data.pbInfo.branchName").value(pb.getBranch().getName()));
//        resultActions.andExpect(jsonPath("$.data.pbInfo.branchAddress").value(pb.getBranch().getRoadAddress()));
//        resultActions.andExpect(jsonPath("$.data.pbInfo.branchLatitude").value(pb.getBranch().getLatitude()));
//        resultActions.andExpect(jsonPath("$.data.pbInfo.branchLongitude").value(pb.getBranch().getLongitude()));
//        resultActions.andExpect(jsonPath("$.data.consultInfo.consultStart").value(MyDateUtil.localTimeToString(pb.getConsultStart())));
//        resultActions.andExpect(jsonPath("$.data.consultInfo.consultEnd").value(MyDateUtil.localTimeToString(pb.getConsultEnd())));
//        resultActions.andExpect(jsonPath("$.data.consultInfo.notice").value(pb.getConsultNotice()));
//        resultActions.andExpect(jsonPath("$.data.userInfo.userName").value(user.getName()));
//        resultActions.andExpect(jsonPath("$.data.userInfo.userPhoneNumber").value(user.getPhoneNumber()));
//        resultActions.andExpect(jsonPath("$.data.userInfo.userEmail").value(user.getEmail()));
//
//        resultActions.andExpect(status().isOk());
//    }

    @WithMockUser
    @Test
    public void apply_reservation_test() throws Exception {
        // given
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");
        ReservationRequest.ApplyDTO applyDTO = new ReservationRequest.ApplyDTO();
        applyDTO.setGoal(ReservationGoal.PROFIT);
        applyDTO.setReservationType(ReservationType.VISIT);
        applyDTO.setLocationType(LocationType.BRANCH);
        applyDTO.setCandidateTime1(LocalDateTime.now().plusHours(10));
        applyDTO.setCandidateTime2(LocalDateTime.now().plusHours(10));
        applyDTO.setQuestion("question입니다.");
        applyDTO.setUserName("lee");
        applyDTO.setUserPhoneNumber("01012345678");
        applyDTO.setUserEmail("lee@nate.com");
        String requestBody = om.writeValueAsString(applyDTO);

        // stub

        // when
        ResultActions resultActions = mvc.perform(post("/user/reservation/{pbId}", pbId)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
    }
}
