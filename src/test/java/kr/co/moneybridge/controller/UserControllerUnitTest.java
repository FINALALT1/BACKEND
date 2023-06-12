package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.Pair;
import kr.co.moneybridge.core.advice.MyLogAdvice;
import kr.co.moneybridge.core.advice.MyValidAdvice;
import kr.co.moneybridge.core.config.MyFilterRegisterConfig;
import kr.co.moneybridge.core.config.MySecurityConfig;
import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.core.util.MyMemberUtil;
import kr.co.moneybridge.core.util.RedisUtil;
import kr.co.moneybridge.dto.user.UserRequest;
import kr.co.moneybridge.dto.user.UserResponse;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserAgreement;
import kr.co.moneybridge.model.user.UserAgreementType;
import kr.co.moneybridge.service.UserService;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
        controllers = {UserController.class}
)
public class UserControllerUnitTest extends MockDummyEntity {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean // 껍데기만
    private UserService userService;
    @MockBean
    private RedisTemplate redisTemplate;
    @MockBean
    private MyMemberUtil myMemberUtil;

//    @Test
//    public void join_test() throws Exception {
//        // given
//        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
//        joinInDTO.setEmail("investor@naver.com");
//        joinInDTO.setPassword("kang1234");
//        joinInDTO.setName("강투자");
//        joinInDTO.setPhoneNumber("01012345678");
//        List<UserRequest.AgreementDTO> agreements = new ArrayList<>();
//        UserRequest.AgreementDTO agreement1 = new UserRequest.AgreementDTO();
//        agreement1.setTitle("돈줄 이용약관 동의");
//        agreement1.setType(UserAgreementType.REQUIRED);
//        agreement1.setIsAgreed(true);
//        agreements.add(agreement1);
//        UserRequest.AgreementDTO agreement2 = new UserRequest.AgreementDTO();
//        agreement2.setTitle("마케팅 정보 수신 동의");
//        agreement2.setType(UserAgreementType.OPTIONAL);
//        agreement2.setIsAgreed(true);
//        agreements.add(agreement2);
//        joinInDTO.setAgreements(agreements);
//        String requestBody = om.writeValueAsString(joinInDTO);
//
//        // stub
//        User mockUser = newMockUser(1L,"강투자");
//
//        UserResponse.JoinOutDTO joinOutDTO = new UserResponse.JoinOutDTO(mockUser);
//        Mockito.when(userService.joinUser(any())).thenReturn(joinOutDTO);
////        Pair<String, String> tokens = new
////        Mockito.when(userService.issue(any())).thenReturn(joinOutDTO);
//
//        // when
//        ResultActions resultActions = mvc
//                .perform(post("/join/user").content(requestBody).contentType(MediaType.APPLICATION_JSON));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // then
//        resultActions.andExpect(jsonPath("$.data.id").value(1L));
//        resultActions.andExpect(status().isOk());
//    }

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
//                .thenReturn(new ReservationResponse.ReservationBaseOutDTO(
//                        new ReservationResponse.pbInfoDTO(
//                                pb.getName(),
//                                pb.getBranch().getName(),
//                                pb.getBranch().getRoadAddress(),
//                                pb.getBranch().getLatitude(),
//                                pb.getBranch().getLongitude()
//                        ),
//                        new ReservationResponse.consultInfoDTO(
//                                MyDateUtil.localTimeToString(pb.getConsultStart()),
//                                MyDateUtil.localTimeToString(pb.getConsultEnd()),
//                                pb.getConsultNotice()
//                        ),
//                        new ReservationResponse.userInfoDTO(
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
//
//    @WithMockUser
//    @Test
//    public void apply_reservation_test() throws Exception {
//        // given
//        Long pbId = 1L;
//        Company company = newMockCompany(1L, "미래에셋");
//        Branch branch = newMockBranch(1L, company, 1);
//        PB pb = newMockPB(pbId, "이피비", branch);
//        User user = newMockUser(1L, "lee");
//        ReservationRequest.ApplyReservationInDTO applyReservationInDTO = new ReservationRequest.ApplyReservationInDTO();
//        applyReservationInDTO.setGoal1(ReservationGoal.PROFIT);
//        applyReservationInDTO.setGoal2(ReservationGoal.RISK);
//        applyReservationInDTO.setReservationType(ReservationType.VISIT);
//        applyReservationInDTO.setLocationType(LocationType.BRANCH);
//        applyReservationInDTO.setLocationName("미래에셋증권 용산wm점");
//        applyReservationInDTO.setLocationAddress("서울특별시 용산구 한강로동 한강대로 92");
//        applyReservationInDTO.setCandidateTime1("2023-05-15T09:00:00");
//        applyReservationInDTO.setCandidateTime2("2023-05-15T10:00:00");
//        applyReservationInDTO.setQuestion("2023-05-15T10:00:00");
//        applyReservationInDTO.setUserName("lee");
//        applyReservationInDTO.setUserPhoneNumber("01012345678");
//        applyReservationInDTO.setUserEmail("lee@nate.com");
//        String requestBody = om.writeValueAsString(applyReservationInDTO);
//
//        // stub
//
//        // when
//        ResultActions resultActions = mvc.perform(post("/user/reservation/{pbId}", pbId)
//                .content(requestBody)
//                .contentType(MediaType.APPLICATION_JSON));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // then
//        resultActions.andExpect(status().isOk());
//    }
}
