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
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @BeforeEach
    public void setUp() {
        User user1 = userRepository.save(dummy.newUser("로그인"));
        em.clear();
    }

    @DisplayName("투자자 회원가입 성공")
    @Test
    public void join_user_test() throws Exception {
        // given
        UserRequest.JoinUserInDTO joinUserInDTO = new UserRequest.JoinUserInDTO();
        joinUserInDTO.setEmail("investor@naver.com");
        joinUserInDTO.setPassword("kang1234");
        joinUserInDTO.setCheckPassword("kang1234");
        joinUserInDTO.setName("강투자");
        joinUserInDTO.setPhoneNumber("01012345678");
        List<UserRequest.AgreementDTO> agreements = new ArrayList<>();
        UserRequest.AgreementDTO agreement1 = UserRequest.AgreementDTO.builder()
                .title("돈줄 이용약관 동의")
                .type(UserAgreementType.REQUIRED)
                .isAgreed(true).build();
        agreements.add(agreement1);
        UserRequest.AgreementDTO agreement2 = UserRequest.AgreementDTO.builder()
                .title("마케팅 정보 수신 동의")
                .type(UserAgreementType.OPTIONAL)
                .isAgreed(true).build();
        agreements.add(agreement2);
        joinUserInDTO.setAgreements(agreements);
        String requestBody = om.writeValueAsString(joinUserInDTO);

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

    @DisplayName("로그인 성공")
    @Test
    public void login_test() throws Exception {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setRole(Role.ROLE_USER);
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
    }

    @DisplayName("토큰 재발급 성공")
    @Test
    public void reissue_test() throws Exception {
        // given
        // 로그인을 통해 액세스 토큰과 리프레시 토큰을 얻습니다.
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setRole(Role.ROLE_USER);
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
        loginInDTO.setRole(Role.ROLE_USER);
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
