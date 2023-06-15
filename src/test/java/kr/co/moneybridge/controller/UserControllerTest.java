package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.auth.jwt.MyJwtProvider;
import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.dto.user.UserRequest;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("투자자 관련 API")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserControllerTest {
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
    private PBRepository pbRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchRepository branchRepository;

    @BeforeEach
    public void setUp() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user1 = userRepository.save(dummy.newUser("로그인"));
        Company company1 = companyRepository.save(dummy.newCompany("미래에셋증권"));
        Branch branch1 = branchRepository.save(dummy.newBranch(company1, 0));
        PB pb1 = pbRepository.save(dummy.newPB("김피비", branch1));
        User user2 = userRepository.save(User.builder()
                .name("김비밀")
                .password(passwordEncoder.encode("password1234"))
                .email("jisu3148496@naver.com")
                .phoneNumber("01012345678")
                .role(Role.ADMIN)
                .profile("프로필.png")
                .build());
        em.clear();
    }

    @DisplayName("개인 정보 수정 성공")
    @WithUserDetails(value = "USER-jisu3148496@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void updateMyInfo_test() throws Exception {
        // given
        UserRequest.UpdateMyInfoInDTO updateMyInfoInDTO = new UserRequest.UpdateMyInfoInDTO();
        updateMyInfoInDTO.setName("안비밀");
        String requestBody = om.writeValueAsString(updateMyInfoInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(patch("/auth/myInfo").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
    }

    @DisplayName("개인 정보 가져오기 성공")
    @WithUserDetails(value = "USER-jisu3148496@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void getMyInfo_test() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/auth/myInfo"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.name").value("김비밀"));
        resultActions.andExpect(jsonPath("$.data.phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.email").value("jisu3148496@naver.com"));
    }

    @DisplayName("비밀번호 확인 성공")
    @WithUserDetails(value = "USER-jisu3148496@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void checkPassword_test() throws Exception {
        // given
        UserRequest.CheckPasswordInDTO checkPasswordInDTO = new UserRequest.CheckPasswordInDTO();
        checkPasswordInDTO.setPassword("password1234");
        String requestBody = om.writeValueAsString(checkPasswordInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/password").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
    }

    @DisplayName("비밀번호 재설정 성공")
    @Test
    public void rePassword_test() throws Exception {
        // given
        UserRequest.RePasswordInDTO rePasswordInDTO = new UserRequest.RePasswordInDTO();
        rePasswordInDTO.setId(1L);
        rePasswordInDTO.setRole(Role.USER);
        rePasswordInDTO.setPassword("1111abcd");
        String requestBody = om.writeValueAsString(rePasswordInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(patch("/password").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
    }

    @DisplayName("이메일 찾기 성공")
    @Test
    public void emailFind_test() throws Exception {
        // given
        UserRequest.EmailFindInDTO emailFindInDTO = new UserRequest.EmailFindInDTO();
        emailFindInDTO.setRole(Role.USER);
        emailFindInDTO.setName("김비밀");
        emailFindInDTO.setPhoneNumber("01012345678");
        String requestBody = om.writeValueAsString(emailFindInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/email").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data[0].name").value("김비밀"));
        resultActions.andExpect(jsonPath("$.data[0].phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data[0].email").value("jisu3148496@naver.com"));
    }

    @DisplayName("비밀번호 찾기시 이메일 인증 성공")
    @WithUserDetails(value = "USER-jisu3148496@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void password_test() throws Exception {
        // given
        UserRequest.PasswordInDTO passwordInDTO = new UserRequest.PasswordInDTO();
        passwordInDTO.setRole(Role.USER);
        passwordInDTO.setName("김비밀");
        passwordInDTO.setEmail("jisu3148496@naver.com");
        String requestBody = om.writeValueAsString(passwordInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/password").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.role").value("ADMIN"));
        resultActions.andExpect(jsonPath("$.data.name").value("김비밀"));
        resultActions.andExpect(jsonPath("$.data.phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.email").value("jisu3148496@naver.com"));
        String regex = "^[0-9A-Z]{8}$";
        resultActions.andExpect(jsonPath("$.data.code").value(matchesPattern(regex)));
    }

    @DisplayName("이메일 인증 성공")
    @Test
    public void email_test() throws Exception {
        // given
        UserRequest.EmailInDTO emailInDTO = new UserRequest.EmailInDTO();
        emailInDTO.setEmail("jisu3148496@naver.com");
        String requestBody = om.writeValueAsString(emailInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/email/authentication").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        String regex = "^[0-9A-Z]{8}$"; // 이는 8자리 숫자와 대문자를 예상하는 정규식입니다.
        resultActions.andExpect(jsonPath("$.data.code").value(matchesPattern(regex)));
    }

    @DisplayName("탈퇴 성공")
    @WithUserDetails(value = "USER-로그인@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void withdraw_test() throws Exception {
        // given
        UserRequest.WithdrawInDTO withdrawInDTO = new UserRequest.WithdrawInDTO();
        withdrawInDTO.setPassword("password1234");
        String requestBody = om.writeValueAsString(withdrawInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(delete("/auth/account").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
    }

    @DisplayName("투자자 회원가입 성공")
    @Test
    public void join_user_test() throws Exception {
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

        // when
        ResultActions resultActions = mvc
                .perform(post("/join/user").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("투자자 로그인 성공")
    @Test
    public void login_user_test() throws Exception {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setRole(Role.USER);
        loginInDTO.setEmail("로그인@nate.com");
        loginInDTO.setPassword("password1234");
        String requestBody = om.writeValueAsString(loginInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(header().exists(MyJwtProvider.HEADER_ACCESS)); // Access 토큰이 헤더에 존재하는지 확인
        resultActions.andExpect(cookie().exists("refreshToken")); // Refresh 토큰이 쿠키에 존재하는지 확인
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.code").isEmpty());
    }

    @DisplayName("PB 로그인 성공")
    @Test
    public void login_pb_test() throws Exception {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setRole(Role.PB);
        loginInDTO.setEmail("김피비@nate.com");
        loginInDTO.setPassword("password1234");
        String requestBody = om.writeValueAsString(loginInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(header().exists(MyJwtProvider.HEADER_ACCESS)); // Access 토큰이 헤더에 존재하는지 확인
        resultActions.andExpect(cookie().exists("refreshToken")); // Refresh 토큰이 쿠키에 존재하는지 확인
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.code").isEmpty());
    }

    @DisplayName("관리자 로그인 성공")
    @Test
    public void login_admin_test() throws Exception {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setRole(Role.USER);
        loginInDTO.setEmail("jisu3148496@naver.com");
        loginInDTO.setPassword("password1234");
        String requestBody = om.writeValueAsString(loginInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(header().exists(MyJwtProvider.HEADER_ACCESS)); // Access 토큰이 헤더에 존재하는지 확인
        resultActions.andExpect(cookie().exists("refreshToken")); // Refresh 토큰이 쿠키에 존재하는지 확인
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        String regex = "^[0-9A-Z]{8}$"; // 이는 8자리 숫자와 대문자를 예상하는 정규식입니다.
        resultActions.andExpect(jsonPath("$.data.code").value(matchesPattern(regex)));
    }

    @DisplayName("토큰 재발급 성공")
    @Test
    public void reissue_test() throws Exception {
        // given
        // 로그인을 통해 액세스 토큰과 리프레시 토큰을 얻습니다.
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setRole(Role.USER);
        loginInDTO.setEmail("로그인@nate.com");
        loginInDTO.setPassword("password1234");
        String loginRequestBody = om.writeValueAsString(loginInDTO);

        MvcResult loginResult = mvc
                .perform(post("/login")
                        .content(loginRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String accessToken = loginResult.getResponse().getHeader(MyJwtProvider.HEADER_ACCESS);
        String refreshToken = loginResult.getResponse().getCookie("refreshToken").getValue();

        // when
        ResultActions resultActions = mvc
                .perform(post("/reissue")
                        .header(MyJwtProvider.HEADER_ACCESS, accessToken)
                        .cookie(new Cookie("refreshToken", refreshToken))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(header().exists(MyJwtProvider.HEADER_ACCESS)); // Access 토큰이 헤더에 존재하는지 확인
        resultActions.andExpect(cookie().exists("refreshToken")); // Refresh 토큰이 쿠키에 존재하는지 확인
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
    }

    @DisplayName("로그아웃 성공")
    @Test
    public void logout_test() throws Exception {
        // given
        // 로그인을 통해 액세스 토큰과 리프레시 토큰을 얻습니다.
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setRole(Role.USER);
        loginInDTO.setEmail("로그인@nate.com");
        loginInDTO.setPassword("password1234");
        String loginRequestBody = om.writeValueAsString(loginInDTO);

        MvcResult loginResult = mvc
                .perform(post("/login")
                        .content(loginRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String accessToken = loginResult.getResponse().getHeader(MyJwtProvider.HEADER_ACCESS);
        String refreshToken = loginResult.getResponse().getCookie("refreshToken").getValue();

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/logout")
                        .header(MyJwtProvider.HEADER_ACCESS, accessToken)
                        .cookie(new Cookie("refreshToken", refreshToken))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
    }
}
