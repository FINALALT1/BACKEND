package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.Pair;
import kr.co.moneybridge.core.WithMockUser;
import kr.co.moneybridge.core.advice.MyLogAdvice;
import kr.co.moneybridge.core.advice.MyValidAdvice;
import kr.co.moneybridge.core.config.MyFilterRegisterConfig;
import kr.co.moneybridge.core.config.MySecurityConfig;
import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.core.util.MyMemberUtil;
import kr.co.moneybridge.core.util.RedisUtil;
import kr.co.moneybridge.dto.user.UserRequest;
import kr.co.moneybridge.dto.user.UserResponse;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.user.User;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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

    @WithMockUser
    @Test
    public void testPropensity_test() throws Exception {
        // Given
        UserRequest.TestPropensityInDTO testPropensityInDTO = new UserRequest.TestPropensityInDTO();
        testPropensityInDTO.setScore(24);
        String requestBody = om.writeValueAsString(testPropensityInDTO);

        // When
        ResultActions resultActions = mvc.perform(post("/user/propensity")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").doesNotExist());
    }

    @WithMockUser
    @Test
    public void getAccount_test() throws Exception {
        // given
        UserResponse.AccountOutDTO accountOutDTO = new UserResponse.AccountOutDTO(newMockUser(1L, "lee"));

        //stub
        Mockito.when(userService.getAccount(any())).thenReturn(accountOutDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/auth/account"));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.id").value("1"));
        resultActions.andExpect(jsonPath("$.data.role").value("USER"));
        resultActions.andExpect(jsonPath("$.data.name").value("lee"));
    }

    @WithMockUser
    @Test
    public void getMyPage_test() throws Exception {
        // given
        User mockUser = newMockUser(1L, "lee");
        UserResponse.StepDTO mockStep = new UserResponse.StepDTO(mockUser);
        UserResponse.ReservationCountDTO mockReservationCount = new UserResponse.ReservationCountDTO(0L, 0L, 0L);
        UserResponse.BookmarkListDTO mockBoardBookmark = new UserResponse.BookmarkListDTO(new ArrayList<>(), 0L);
        UserResponse.BookmarkListDTO mockPbBookmark = new UserResponse.BookmarkListDTO(new ArrayList<>(), 0L);

        UserResponse.MyPageOutDTO myPageOutDTO = new UserResponse.MyPageOutDTO(mockUser, mockStep, mockReservationCount, mockBoardBookmark, mockPbBookmark);

        //stub
        Mockito.when(userService.getMyPage(any())).thenReturn(myPageOutDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/user/mypage"));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.id").value("1"));
        resultActions.andExpect(jsonPath("$.data.name").value("lee"));
        resultActions.andExpect(jsonPath("$.data.propensity").value("AGGRESSIVE"));
        resultActions.andExpect(jsonPath("$.data.step.hasDonePropensity").value(true));
        resultActions.andExpect(jsonPath("$.data.step.hasDoneBoardBookmark").value(false));
        resultActions.andExpect(jsonPath("$.data.step.hasDoneReservation").value(false));
        resultActions.andExpect(jsonPath("$.data.step.hasDoneReview").value(false));
        resultActions.andExpect(jsonPath("$.data.reservationCount.apply").value(0));
        resultActions.andExpect(jsonPath("$.data.reservationCount.confirm").value(0));
        resultActions.andExpect(jsonPath("$.data.reservationCount.complete").value(0));
        resultActions.andExpect(jsonPath("$.data.boardBookmark.list").isEmpty());
        resultActions.andExpect(jsonPath("$.data.boardBookmark.count").value(0));
        resultActions.andExpect(jsonPath("$.data.pbBookmark.list").isEmpty());
        resultActions.andExpect(jsonPath("$.data.pbBookmark.count").value(0));
    }

    @WithMockUser
    @Test
    public void updateMyInfo_test() throws Exception {
        // given
        UserRequest.UpdateMyInfoInDTO updateMyInfoInDTO = new UserRequest.UpdateMyInfoInDTO();
        updateMyInfoInDTO.setPhoneNumber("01011223344");
        String requestBody = om.writeValueAsString(updateMyInfoInDTO);

        // When
        ResultActions resultActions = mvc.perform(patch("/auth/myinfo")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    public void getMyInfo_test() throws Exception {
        // given
        UserResponse.MyInfoOutDTO myInfoOutDTO = new UserResponse.MyInfoOutDTO(newMockUser(1L, "lee"));

        //stub
        Mockito.when(userService.getMyInfo(any())).thenReturn(myInfoOutDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/auth/myinfo"));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.name").value("lee"));
        resultActions.andExpect(jsonPath("$.data.phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.email").value("lee@nate.com"));
    }

    @WithMockUser
    @Test
    public void checkPassword_test() throws Exception {
        // Given
        UserRequest.CheckPasswordInDTO checkPasswordInDTO = new UserRequest.CheckPasswordInDTO();
        checkPasswordInDTO.setPassword("password1234");
        String requestBody = om.writeValueAsString(checkPasswordInDTO);

        // When
        ResultActions resultActions = mvc.perform(post("/auth/password")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void updatePassword_test() throws Exception {
        // Given
        UserRequest.RePasswordInDTO rePasswordInDTO = new UserRequest.RePasswordInDTO();
        rePasswordInDTO.setId(1L);
        rePasswordInDTO.setRole(Role.USER);
        rePasswordInDTO.setPassword("1111abcd");
        String requestBody = om.writeValueAsString(rePasswordInDTO);

        // When
        ResultActions resultActions = mvc.perform(patch("/password")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void findEmail_test() throws Exception {
        // Given
        UserRequest.EmailFindInDTO emailFindInDTO = new UserRequest.EmailFindInDTO();
        emailFindInDTO.setRole(Role.USER);
        emailFindInDTO.setName("김투자");
        emailFindInDTO.setPhoneNumber("01012345678");
        String requestBody = om.writeValueAsString(emailFindInDTO);

        // stub
        List<UserResponse.EmailFindOutDTO> emailFindOutDTOs = new ArrayList<>();
        emailFindOutDTOs.add(new UserResponse.EmailFindOutDTO(newMockUser(1L, "김투자")));
        Mockito.when(userService.findEmail(any())).thenReturn(emailFindOutDTOs);

        // When
        ResultActions resultActions = mvc.perform(post("/email")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data[0].name").value("김투자"));
        resultActions.andExpect(jsonPath("$.data[0].phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data[0].email").value("김투자@nate.com"));
    }

    @WithMockUser
    @Test
    public void password_test() throws Exception {
        // Given
        UserRequest.PasswordInDTO passwordInDTO = new UserRequest.PasswordInDTO();
        passwordInDTO.setRole(Role.USER);
        passwordInDTO.setName("lee");
        passwordInDTO.setEmail("jisu3148496@naver.com");
        String requestBody = om.writeValueAsString(passwordInDTO);

        // stub
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User mockUser = User.builder()
                .id(1L)
                .name("lee")
                .password(passwordEncoder.encode("password1234"))
                .email("jisu3148496@naver.com")
                .phoneNumber("01012345678")
                .role(Role.USER)
                .profile("profile.png")
                .createdAt(LocalDateTime.now())
                .build();;
        UserResponse.PasswordOutDTO passwordOutDTO = new UserResponse.PasswordOutDTO(mockUser,"J46L4SBJ");
        Mockito.when(userService.password(any())).thenReturn(passwordOutDTO);

        // When
        ResultActions resultActions = mvc.perform(post("/password")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.role").value("USER"));
        resultActions.andExpect(jsonPath("$.data.name").value("lee"));
        resultActions.andExpect(jsonPath("$.data.phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.email").value("jisu3148496@naver.com"));
        resultActions.andExpect(jsonPath("$.data.code").value("J46L4SBJ"));
    }

    @Test
    public void email_test() throws Exception {
        // Given
        UserRequest.EmailInDTO emailInDTO = new UserRequest.EmailInDTO();
        emailInDTO.setEmail("jisu3148496@naver.com");
        String requestBody = om.writeValueAsString(emailInDTO);

        // stub
        UserResponse.EmailOutDTO emailOutDTO = new UserResponse.EmailOutDTO("J46L4SBJ");
        Mockito.when(userService.email(any())).thenReturn(emailOutDTO);

        // When
        ResultActions resultActions = mvc.perform(post("/email/authentication")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.code").value("J46L4SBJ"));
    }

    @WithMockUser
    @Test
    public void withdraw_test() throws Exception { // 보충 필요
        // Given
        UserRequest.WithdrawInDTO withdrawInDTO = new UserRequest.WithdrawInDTO();
        withdrawInDTO.setPassword("password1234");
        String requestBody = om.writeValueAsString(withdrawInDTO);

        // When
        ResultActions resultActions = mvc.perform(delete("/auth/account")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void join_test() throws Exception {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setEmail("investor@naver.com");
        joinInDTO.setPassword("kang1234");
        joinInDTO.setName("강투자");
        joinInDTO.setPhoneNumber("01012345678");
        List<UserRequest.AgreementDTO> agreements = new ArrayList<>();
        UserRequest.AgreementDTO agreement1 = new UserRequest.AgreementDTO();
        agreement1.setTitle("돈줄 이용약관 동의");
        agreement1.setType(UserAgreementType.REQUIRED);
        agreement1.setIsAgreed(true);
        agreements.add(agreement1);
        UserRequest.AgreementDTO agreement2 = new UserRequest.AgreementDTO();
        agreement2.setTitle("마케팅 정보 수신 동의");
        agreement2.setType(UserAgreementType.OPTIONAL);
        agreement2.setIsAgreed(true);
        agreements.add(agreement2);
        joinInDTO.setAgreements(agreements);
        String requestBody = om.writeValueAsString(joinInDTO);

        // stub
        User mockUser = newMockUser(1L,"강투자");
        UserResponse.JoinOutDTO joinOutDTO = new UserResponse.JoinOutDTO(mockUser);
        Mockito.when(userService.joinUser(any())).thenReturn(joinOutDTO);
        Pair<String, String> tokens = Pair.of("accessToken", "refreshToken");
        Mockito.when(userService.issue(any(), any(), any())).thenReturn(tokens);

        // when
        ResultActions resultActions = mvc.perform(post("/join/user")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L));

        // then
        MvcResult mvcResult = resultActions.andReturn();
        String actualResponse = mvcResult.getResponse().getHeader("Set-Cookie");
        assertEquals("refreshToken=refreshToken", actualResponse.substring(0, 25));
    }

    @Test
    public void reissue_test() throws Exception {
        // Given
        String oldRefreshToken = "oldRefreshToken";
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";

        // eq(oldRefreshToken)))는 Mockito를 사용하여 userService의 reissue 메서드를 호출할 때,
        // 특정한 매개변수 값과 함께 호출될 것을 가정하고, oldRefreshToken이라는 값을 가진 매개변수를
        // 가진 메서드 호출을 나타내며, 이 값과 일치하는 경우에만 동작이 일치하는 것으로 가정
        Mockito.when(userService.reissue(any(HttpServletRequest.class), eq(oldRefreshToken)))
                .thenReturn(Pair.of(newAccessToken, newRefreshToken));

        MockHttpServletRequestBuilder requestBuilder = post("/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("refreshToken", oldRefreshToken));

        // When
        ResultActions resultActions = mvc.perform(requestBuilder)
                .andExpect(status().isOk());

        // Then
        MvcResult mvcResult = resultActions.andReturn();
        String actualResponse = mvcResult.getResponse().getHeader("Set-Cookie");
        assertEquals("refreshToken=newRefreshToken", actualResponse.substring(0, 28));

        Mockito.verify(userService).reissue(any(HttpServletRequest.class), eq(oldRefreshToken));  // Verifying that userService.reissue was called
    }

    @WithMockUser
    @Test
    public void logout_test() throws Exception {
        // Given
        String oldRefreshToken = "oldRefreshToken";

        MockHttpServletRequestBuilder requestBuilder = post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("refreshToken", oldRefreshToken));

        // When
        ResultActions resultActions = mvc.perform(requestBuilder)
                .andExpect(status().isOk());

        // Then
        resultActions.andExpect(status().isOk());
        Mockito.verify(userService).logout(any(HttpServletRequest.class), eq(oldRefreshToken));  // Verifying that userService.logout was called
    }

    @WithMockUser
    @Test
    public void login_user_test() throws Exception {
        // Given
        String oldRefreshToken = "oldRefreshToken";

        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setRole(Role.USER);
        loginInDTO.setEmail("로그인@nate.com");
        loginInDTO.setPassword("password1234");
        String requestBody = om.writeValueAsString(loginInDTO);

        // stub
        User mockUser = newMockUser(1L,"강투자");
        UserResponse.LoginOutDTO loginOutDTO = new UserResponse.LoginOutDTO(mockUser, false);
        Mockito.when(userService.login(any())).thenReturn(loginOutDTO);
        Pair<String, String> tokens = Pair.of("accessToken", "refreshToken");
        Mockito.when(userService.issue(any(), any(), any())).thenReturn(tokens);

        // when
        ResultActions resultActions = mvc.perform(post("/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isAdmin").value("false"));

        // then
        MvcResult mvcResult = resultActions.andReturn();
        String actualResponse = mvcResult.getResponse().getHeader("Set-Cookie");
        assertEquals("refreshToken=refreshToken", actualResponse.substring(0, 25));
    }

    @WithMockUser
    @Test
    public void login_admin_test() throws Exception {
        // Given
        String oldRefreshToken = "oldRefreshToken";

        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setRole(Role.USER);
        loginInDTO.setEmail("로그인@nate.com");
        loginInDTO.setPassword("password1234");
        String requestBody = om.writeValueAsString(loginInDTO);

        // stub
        User mockUser = newMockUserADMIN(1L,"강투자");
        UserResponse.LoginOutDTO loginOutDTO = new UserResponse.LoginOutDTO(mockUser, true);
        Mockito.when(userService.login(any())).thenReturn(loginOutDTO);
        Pair<String, String> tokens = Pair.of("accessToken", "refreshToken");
        Mockito.when(userService.issue(any(), any(), any())).thenReturn(tokens);

        // when
        ResultActions resultActions = mvc.perform(post("/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isAdmin").value("true"));

        // then
        MvcResult mvcResult = resultActions.andReturn();
        String actualResponse = mvcResult.getResponse().getHeader("Set-Cookie");
        assertEquals("refreshToken=refreshToken", actualResponse.substring(0, 25));
    }


}
