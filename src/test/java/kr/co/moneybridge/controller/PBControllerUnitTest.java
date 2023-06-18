package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.WithMockPB;
import kr.co.moneybridge.core.WithMockUser;
import kr.co.moneybridge.core.advice.MyLogAdvice;
import kr.co.moneybridge.core.advice.MyValidAdvice;
import kr.co.moneybridge.core.config.MyFilterRegisterConfig;
import kr.co.moneybridge.core.config.MySecurityConfig;
import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.core.util.MyMemberUtil;
import kr.co.moneybridge.core.util.RedisUtil;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.pb.PBResponse;
import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.service.PBService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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

    @WithMockUser
    @Test
    public void getMyPropensityPB_test() throws Exception {
        //stub
        PBResponse.MyPageOutDTO myPageOutDTO = new PBResponse.MyPageOutDTO(
                newMockPB(1L, "김pb", newMockBranch(1L, newMockCompany(
                        1L, "미래에셋증권"), 0)), 0,0);
        List<PBResponse.MyPropensityPBDTO> list = Arrays.asList(new PBResponse.MyPropensityPBDTO(
                newMockPB(1L, "김pb", newMockBranch(1L, newMockCompany(1L, "미래에셋증권"), 0)),
                0, 0, false));
        PBResponse.MyPropensityPBOutDTO myPropensityPBOutDTO = new PBResponse.MyPropensityPBOutDTO(
                newMockUser(1L, "lee"), list);
        Mockito.when(pbService.getMyPropensityPB(any())).thenReturn(myPropensityPBOutDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/user/mypage/list/pb"));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.name").value("lee"));
        resultActions.andExpect(jsonPath("$.data.propensity").value("AGGRESSIVE"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].profile").value("profile.png"));
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("김pb"));
        resultActions.andExpect(jsonPath("$.data.list[0].branchName").value("미래에셋증권 여의도점"));
        resultActions.andExpect(jsonPath("$.data.list[0].msg").value("한줄메시지.."));
        resultActions.andExpect(jsonPath("$.data.list[0].career").value("10"));
        resultActions.andExpect(jsonPath("$.data.list[0].specialty1").value("BOND"));
        resultActions.andExpect(jsonPath("$.data.list[0].specialty2").doesNotExist());
        resultActions.andExpect(jsonPath("$.data.list[0].reserveCount").value("0"));
        resultActions.andExpect(jsonPath("$.data.list[0].reviewCount").value("0"));
        resultActions.andExpect(jsonPath("$.data.list[0].isBookmark").value("false"));
    }

    @WithMockPB
    @Test
    public void getMyPage_test() throws Exception {
        //stub
        PBResponse.MyPageOutDTO myPageOutDTO = new PBResponse.MyPageOutDTO(
                newMockPB(1L, "김pb", newMockBranch(1L, newMockCompany(
                        1L, "미래에셋증권"), 0)), 0,0);
                Mockito.when(pbService.getMyPage(any())).thenReturn(myPageOutDTO);
        // When
        ResultActions resultActions = mvc.perform(get("/pb/mypage"));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.profile").value("profile.png"));
        resultActions.andExpect(jsonPath("$.data.name").value("김pb"));
        resultActions.andExpect(jsonPath("$.data.branchName").value("미래에셋증권 여의도점"));
        resultActions.andExpect(jsonPath("$.data.msg").value("한줄메시지.."));
        resultActions.andExpect(jsonPath("$.data.career").value("10"));
        resultActions.andExpect(jsonPath("$.data.specialty1").value("BOND"));
        resultActions.andExpect(jsonPath("$.data.specialty2").doesNotExist());
        resultActions.andExpect(jsonPath("$.data.reserveCount").value("0"));
        resultActions.andExpect(jsonPath("$.data.reviewCount").value("0"));
    }

    @Test
    public void searchBranch_test() throws Exception {
        // given
        Long companyId = 1L;
        String keyword = "지번 주소";
        Branch branch = newMockBranch(1L, newMockCompany(1L, "미래에셋증권"), 0);
        List<PBResponse.BranchDTO> list = Arrays.asList(new PBResponse.BranchDTO(branch));
        Page<Branch> branchPG =  new PageImpl<>(Arrays.asList(branch));
        PageDTO<PBResponse.BranchDTO> pageDTO = new PageDTO<>(list, branchPG, Branch.class);

        //stub
        Mockito.when(pbService.searchBranch(any(), any(), any())).thenReturn(pageDTO);

        // When
        ResultActions resultActions = mvc.perform(get("/branch?companyId="+companyId+"&keyword="+keyword));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("미래에셋증권 여의도점"));
        resultActions.andExpect(jsonPath("$.data.list[0].roadAddress").value("미래에셋증권 도로명주소"));
        resultActions.andExpect(jsonPath("$.data.list[0].streetAddress").value("미래에셋증권 지번주소"));
    }

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
