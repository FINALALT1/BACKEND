package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.auth.jwt.MyJwtProvider;
import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.dto.pb.PBRequest;
import kr.co.moneybridge.dto.user.UserRequest;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserAgreementType;
import kr.co.moneybridge.model.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("PB 관련 API")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class PBControllerTest {
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
    private BranchRepository branchRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @BeforeEach
    public void setUp() {
        User user1 = userRepository.save(dummy.newUser("로그인"));
        Company company1 = companyRepository.save(dummy.newCompany("미래에셋증권"));
        Branch branch1 = branchRepository.save(dummy.newBranch(company1, 0));
        em.clear();
    }

    @DisplayName("증권사 리스트 가져오기 - 로고포함")
    @Test
    public void getCompanies_not_including_logos_test() throws Exception {
        //given
        Boolean param = false;

        // when
        ResultActions resultActions = mvc
                .perform(get("/companies?includeLogo="+param));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].logo").doesNotExist());
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("미래에셋증권"));
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("증권사 리스트 가져오기 - 로고포함")
    @Test
    public void getCompanies_including_logos_test() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/companies"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].logo").value("logo.png"));
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("미래에셋증권"));
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("PB 회원가입 성공")
    @Test
    public void join_pb_test() throws Exception {
        // given
        PBRequest.JoinInDTO joinInDTO = new PBRequest.JoinInDTO();
        joinInDTO.setEmail("kangpb@naver.com");
        joinInDTO.setPassword("kang1234");
        joinInDTO.setName("강투자");
        joinInDTO.setPhoneNumber("01012345678");
        joinInDTO.setBranchId(1L);
        joinInDTO.setCareer(1);
        joinInDTO.setSpeciality1(PBSpeciality.BOND);
        joinInDTO.setSpeciality2(PBSpeciality.US_STOCK);
        List<PBRequest.AgreementDTO> agreements = new ArrayList<>();
        PBRequest.AgreementDTO agreement1 = new PBRequest.AgreementDTO();
        agreement1.setTitle("돈줄 이용약관 동의");
        agreement1.setType(PBAgreementType.REQUIRED);
        agreement1.setIsAgreed(true);
        agreements.add(agreement1);
        PBRequest.AgreementDTO agreement2 = new PBRequest.AgreementDTO();
        agreement2.setTitle("마케팅 정보 수신 동의");
        agreement2.setType(PBAgreementType.OPTIONAL);
        agreement2.setIsAgreed(true);
        agreements.add(agreement2);
        joinInDTO.setAgreements(agreements);

        MockMultipartFile businessCard = new MockMultipartFile(
                "businessCard", "businessCard.png", "image/png"
                , new FileInputStream("./src/main/resources/businessCard.png"));
        // joinInJson 객체를 JSON 문자열로 변환
        String joinInJson = om.writeValueAsString(joinInDTO);
        MockMultipartFile json = new MockMultipartFile("joinInDTO", "joinInDTO",
                "application/json", joinInJson.getBytes(StandardCharsets.UTF_8));

        // when
        ResultActions resultActions = mvc
                .perform(multipart("/join/pb")
                        .file(businessCard)
                        .file(json));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(status().isOk());
    }
}
