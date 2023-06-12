package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.Pair;
import kr.co.moneybridge.core.WithMockUser;
import kr.co.moneybridge.core.advice.MyLogAdvice;
import kr.co.moneybridge.core.advice.MyValidAdvice;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        assertEquals("refreshToken=refreshToken; Path=/; HttpOnly", actualResponse);
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
        assertEquals("refreshToken=newRefreshToken; Path=/; HttpOnly", actualResponse);

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
}
