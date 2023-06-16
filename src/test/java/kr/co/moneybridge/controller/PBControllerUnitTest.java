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
import kr.co.moneybridge.dto.pb.PBResponse;
import kr.co.moneybridge.dto.user.UserRequest;
import kr.co.moneybridge.dto.user.UserResponse;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserAgreementType;
import kr.co.moneybridge.service.PBService;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
        controllers = {PBController.class}
)
public class PBControllerUnitTest extends MockDummyEntity {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean // 껍데기만
    private PBService pbService;
    @MockBean
    private RedisTemplate redisTemplate;
    @MockBean
    private MyMemberUtil myMemberUtil;

    @Test
    public void getCompanies_without_logo_test() throws Exception {
        // given
        PBResponse.CompanyNameOutDTO companyNameOutDTO = new PBResponse.CompanyNameOutDTO(Arrays.asList(
                new PBResponse.CompanyNameDTO(newMockCompany(1L, "미래에셋증권")),
                new PBResponse.CompanyNameDTO(newMockCompany(2L, "키움증권"))
        ));
        Boolean param = false;

        //stub
        Mockito.when(pbService.getCompanyNames()).thenReturn(companyNameOutDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/companies?includeLogo="+param));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].logo").doesNotExist());
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("미래에셋증권"));
        resultActions.andExpect(jsonPath("$.data.list[1].id").value("2"));
        resultActions.andExpect(jsonPath("$.data.list[1].logo").doesNotExist());
        resultActions.andExpect(jsonPath("$.data.list[1].name").value("키움증권"));
    }

    @Test
    public void getCompanies_logo_test() throws Exception {
        // given
        PBResponse.CompanyOutDTO companyOutDTO = new PBResponse.CompanyOutDTO(Arrays.asList(
                new PBResponse.CompanyDTO(newMockCompany(1L, "미래에셋증권")),
                new PBResponse.CompanyDTO(newMockCompany(2L, "키움증권"))
        ));

        //stub
        Mockito.when(pbService.getCompanies()).thenReturn(companyOutDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/companies"));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].logo").value("logo.png"));
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("미래에셋증권"));
        resultActions.andExpect(jsonPath("$.data.list[1].id").value("2"));
        resultActions.andExpect(jsonPath("$.data.list[1].logo").value("logo.png"));
        resultActions.andExpect(jsonPath("$.data.list[1].name").value("키움증권"));
    }
}
