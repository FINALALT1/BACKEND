package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.core.util.S3Util;
import kr.co.moneybridge.dto.pb.PBRequest;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;
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
    @Autowired
    private PBRepository pbRepository;
    @Autowired
    private S3Util s3Util;

    @BeforeEach
    public void setUp() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user1 = userRepository.save(dummy.newUser("로그인"));
        User user2 = userRepository.save(dummy.newUserWithPropensity("김투자"));
        Company company = companyRepository.save(dummy.newCompany("미래에셋증권"));
        Branch branch = branchRepository.save(dummy.newBranch(company, 0));
        PB pb = pbRepository.save(PB.builder()
                .name("김pb")
                .password(passwordEncoder.encode("password1234"))
                .email("jisu3148496@naver.com")
                .phoneNumber("01012345678")
                .branch(branch)
                .profile("profile.png")
                .businessCard("card.png")
                .career(10)
                .speciality1(PBSpeciality.BOND)
                .intro("김pb 입니다")
                .msg("한줄메시지..")
                .reservationInfo("10분 미리 도착해주세요")
                .consultStart(MyDateUtil.StringToLocalTime("09:00"))
                .consultEnd(MyDateUtil.StringToLocalTime("18:00"))
                .consultNotice("월요일 불가능합니다")
                .role(Role.PB)
                .status(PBStatus.ACTIVE)
                .build());
        em.clear();
    }

    @DisplayName("나의 투자 성향 분석페이지 하단의 맞춤 PB리스트 3개 성공")
    @WithUserDetails(value = "USER-김투자@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void getMyPropensityPB() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/user/mypage/list/pb"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.name").value("김투자"));
        resultActions.andExpect(jsonPath("$.data.propensity").value("SPECULATIVE"));
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
        resultActions.andExpect(status().isOk());
    }
    @DisplayName("PB 마이페이지 가져오기 성공")
    @WithUserDetails(value = "PB-jisu3148496@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void getMyPage() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/pb/mypage"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
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
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("지점 검색 성공 -  지번 주소")
    @Test
    public void searchBranchStreet() throws Exception {
        //given
        Long companyId = 1L;
        String keyword = "지번 주소";

        // when
        ResultActions resultActions = mvc
                .perform(get("/branch?companyId="+companyId+"&keyword="+keyword));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("미래에셋증권 여의도점"));
        resultActions.andExpect(jsonPath("$.data.list[0].roadAddress").value("미래에셋증권 도로명주소"));
        resultActions.andExpect(jsonPath("$.data.list[0].streetAddress").value("미래에셋증권 지번주소"));
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("지점 검색 성공 - 도로명 주소")
    @Test
    public void searchBranchRoad() throws Exception {
        //given
        Long companyId = 1L;
        String keyword = "도로명 주소";

        // when
        ResultActions resultActions = mvc
                .perform(get("/branch?companyId="+companyId+"&keyword="+keyword));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("미래에셋증권 여의도점"));
        resultActions.andExpect(jsonPath("$.data.list[0].roadAddress").value("미래에셋증권 도로명주소"));
        resultActions.andExpect(jsonPath("$.data.list[0].streetAddress").value("미래에셋증권 지번주소"));
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("지점 검색 성공 - 빈칸있을 때")
    @Test
    public void searchBranchBlank() throws Exception {
        //given
        Long companyId = 1L;
        String keyword = " 미래 에셋 증권여의도 ";

        // when
        ResultActions resultActions = mvc
                .perform(get("/branch?companyId="+companyId+"&keyword="+keyword));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("미래에셋증권 여의도점"));
        resultActions.andExpect(jsonPath("$.data.list[0].roadAddress").value("미래에셋증권 도로명주소"));
        resultActions.andExpect(jsonPath("$.data.list[0].streetAddress").value("미래에셋증권 지번주소"));
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("지점 검색 성공")
    @Test
    public void searchBranch() throws Exception {
        //given
        Long companyId = 1L;
        String keyword = "미래에셋증권";

        // when
        ResultActions resultActions = mvc
                .perform(get("/branch?companyId="+companyId+"&keyword="+keyword));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].id").value("1"));
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("미래에셋증권 여의도점"));
        resultActions.andExpect(jsonPath("$.data.list[0].roadAddress").value("미래에셋증권 도로명주소"));
        resultActions.andExpect(jsonPath("$.data.list[0].streetAddress").value("미래에셋증권 지번주소"));
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("증권사 리스트 가져오기 - 로고포함 성공")
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

    @DisplayName("증권사 리스트 가져오기 - 로고포함 성공")
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

        // S3에 저장안되게 다시 삭제시켜주기
        s3Util.deleteLatestFileWithSuffixFromS3Bucket("_businessCard.png");

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(status().isOk());
    }
}
