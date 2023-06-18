package kr.co.moneybridge.service;

import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.PageDTOV2;
import kr.co.moneybridge.dto.pb.PBResponse;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.reservation.ReservationProcess;
import kr.co.moneybridge.model.reservation.ReservationRepository;
import kr.co.moneybridge.model.reservation.ReviewRepository;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserBookmarkRepository;
import kr.co.moneybridge.model.user.UserPropensity;
import kr.co.moneybridge.model.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PBServiceTest extends MockDummyEntity {

    @InjectMocks
    PBService pbService;
    @Mock
    PBRepository pbRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CompanyRepository companyRepository;
    @Mock
    BranchRepository branchRepository;
    @Mock
    ReservationRepository reservationRepository;
    @Mock
    ReviewRepository reviewRepository;
    @Mock
    AwardRepository awardRepository;
    @Mock
    CareerRepository careerRepository;
    @Mock
    UserBookmarkRepository userBookmarkRepository;
    @Mock
    PortfolioRepository portfolioRepository;
    @Mock

    MyUserDetails myUserDetails;
    @Mock
    PB pb;
    @Mock
    Branch branch;
    @Mock
    Company company;
    @Mock
    Pageable pageable;

    @Test
    @DisplayName("PB 마이페이지 가져오기")
    void getMyPage() {
        //given
        Optional<PB> pbOP = Optional.of(newMockPB(1L, "김pb",
                newMockBranch(1L, newMockCompany(1L, "미래에셋증권"),0)));
        //stub
        when(myUserDetails.getMember()).thenReturn(pbOP.get());
        when(pbRepository.findByEmail(any())).thenReturn(pbOP);
        when(reservationRepository.countByPBIdAndProcess(any(), any())).thenReturn(0);
        when(reviewRepository.countByPBId(any())).thenReturn(0);

        //when
        PBResponse.MyPageOutDTO myPageOutDTO = pbService.getMyPage(myUserDetails);

        //then
        assertThat(myPageOutDTO.getProfile()).isEqualTo("profile.png");
        assertThat(myPageOutDTO.getName()).isEqualTo("김pb");
        assertThat(myPageOutDTO.getBranchName()).isEqualTo("미래에셋증권 여의도점");
        assertThat(myPageOutDTO.getMsg()).isEqualTo("한줄메시지..");
        assertThat(myPageOutDTO.getCareer()).isEqualTo(10);
        assertThat(myPageOutDTO.getSpecialty1()).isEqualTo(PBSpeciality.BOND);
        assertThat(myPageOutDTO.getSpecialty2()).isNull();
        assertThat(myPageOutDTO.getReserveCount()).isEqualTo(0);
        assertThat(myPageOutDTO.getReviewCount()).isEqualTo(0);
        Mockito.verify(myUserDetails, Mockito.times(1)).getMember();
        Mockito.verify(pbRepository, Mockito.times(1)).findByEmail(any());
        Mockito.verify(reservationRepository, Mockito.times(1)).countByPBIdAndProcess(any(), any());
        Mockito.verify(reviewRepository, Mockito.times(1)).countByPBId(any());
    }

    @Test
    @DisplayName("지점 검색")
    void searchBranch() {
        //given
        Long companyId = 1L;
        String keyword = "지번 주소";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Branch branch = newMockBranch(1L, newMockCompany(1L, "미래에셋증권"), 0);
        Page<Branch> branchPG =  new PageImpl<>(Arrays.asList(branch));

        //stub
        when(branchRepository.findByCompanyIdAndKeyword(any(), any(), any())).thenReturn(branchPG);

        //when
        PageDTO<PBResponse.BranchDTO> pageDTO = pbService.searchBranch(companyId, keyword, pageable);

        //then
        assertThat(pageDTO.getList().size()).isEqualTo(branchPG.getContent().size());
        Mockito.verify(branchRepository, Mockito.times(1)).findByCompanyIdAndKeyword(any(), any(), any());
    }

    @Test
    @DisplayName("증권사 리스트 이름만 가져오기")
    void getCompanyNames() {
        //given
        List<Company> companies = Arrays.asList(
                newMockCompany(1L, "미래에셋증권"),
                newMockCompany(2L, "키움증권")
        );

        //stub
        when(companyRepository.findAll()).thenReturn(companies);

        //when
        PBResponse.CompanyNameOutDTO companyNameOutDTO = pbService.getCompanyNames();

        //then
        assertThat(companyNameOutDTO.getList().size()).isEqualTo(companies.size());
        Mockito.verify(companyRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("증권사 리스트 가져오기")
    void getCompanies() {
        //given
        List<Company> companies = Arrays.asList(
                newMockCompany(1L, "미래에셋증권"),
                newMockCompany(2L, "키움증권")
        );

        //stub
        when(companyRepository.findAll()).thenReturn(companies);

        //when
        PBResponse.CompanyOutDTO companyOutDTO = pbService.getCompanies();

        //then
        assertThat(companyOutDTO.getList().size()).isEqualTo(companies.size());
        assertThat(companyOutDTO.getList().get(0).getLogo()).isEqualTo("logo.png");
        Mockito.verify(companyRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("PB 검색하기")
    void getPBWithName() {
        //given
        String name = "김피비";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        List<PBResponse.PBPageDTO> pbList = Arrays.asList(pbPageDTO1, pbPageDTO2);
        Page<PBResponse.PBPageDTO> pbPage = new PageImpl<>(pbList, pageable, pbList.size());

        //stub
        when(pbRepository.findByName(name, pageable)).thenReturn(pbPage);

        //when
        PageDTO<PBResponse.PBPageDTO> result = pbService.getPBWithName(name, pageable);

        //then
        assertThat(result.getList().size()).isEqualTo(pbPage.getContent().size());
        Mockito.verify(pbRepository, Mockito.times(1)).findByName(name, pageable);
    }

    @Test
    @DisplayName("거리순 PB리스트 가져오기(전문분야필터)")
    void getSpecialityPBWithDistance() {
        //given
        Double latitude = 112.1414;
        Double longitude = 87.1234;
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        List<PBResponse.PBPageDTO> list = Arrays.asList(pbPageDTO1, pbPageDTO2);
        PBSpeciality speciality = PBSpeciality.BOND;

        //stub
        when(pbRepository.findByPBListSpeciality(speciality)).thenReturn(list);

        //when
        PageDTO<PBResponse.PBPageDTO> result = pbService.getSpecialityPBWithDistance(latitude, longitude, speciality, pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(list.size());
        assertThat(result.getList()).isEqualTo(list);
    }

    @Test
    @DisplayName("거리순 PB리스트 가져오기(증권사필터)")
    void getCompanyPBWithDistance() {
        //given
        Double latitude = 112.1414;
        Double longitude = 87.1234;
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        List<PBResponse.PBPageDTO> list = Arrays.asList(pbPageDTO1, pbPageDTO2);
        Long companyId = 1L;

        //stub
        when(pbRepository.findByPBListCompany(anyLong())).thenReturn(list);

        //when
        PageDTO<PBResponse.PBPageDTO> result = pbService.getCompanyPBWithDistance(latitude, longitude, companyId, pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(list.size());
        assertThat(result.getList()).isEqualTo(list);
    }

    @Test
    @DisplayName("거리순 전체PB리스트 가져오기")
    void getPBWithDistance() {
        //given
        Double latitude = 112.1414;
        Double longitude = 87.1234;
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        List<PBResponse.PBPageDTO> list = Arrays.asList(pbPageDTO1, pbPageDTO2);

        //stub
        when(pbRepository.findAllPB()).thenReturn(list);

        //when
        PageDTO<PBResponse.PBPageDTO> result = pbService.getPBWithDistance(latitude, longitude, pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(list.size());
        assertThat(result.getList()).isEqualTo(list);
    }

    @Test
    @DisplayName("경력순 PB리스트 가져오기(전문분야필터)")
    void getSpecialityPBWithCareer() {
        //given
        PBSpeciality speciality = PBSpeciality.US_STOCK;
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        List<PBResponse.PBPageDTO> list = Arrays.asList(pbPageDTO1, pbPageDTO2);
        Page<PBResponse.PBPageDTO> pbPG = new PageImpl<>(list, pageable, list.size());

        //stub
        when(pbRepository.findBySpecialityOrderedByCareer(speciality, pageable)).thenReturn(pbPG);

        //when
        PageDTO<PBResponse.PBPageDTO> result = pbService.getSpecialityPBWithCareer(speciality, pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(list.size());
        assertThat(result.getList()).isEqualTo(list);
    }

    @Test
    @DisplayName("경력순 PB리스트 가져오기(증권사필터)")
    void getCompanyPBWithCareer() {
        //given
        Long companyId = 1L;
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        List<PBResponse.PBPageDTO> list = Arrays.asList(pbPageDTO1, pbPageDTO2);
        Page<PBResponse.PBPageDTO> pbPG = new PageImpl<>(list, pageable, list.size());

        //stub
        when(pbRepository.findByCompanyIdOrderedByCareer(companyId, pageable)).thenReturn(pbPG);

        //when
        PageDTO<PBResponse.PBPageDTO> result = pbService.getCompanyPBWithCareer(companyId, pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(list.size());
        assertThat(result.getList()).isEqualTo(list);
    }

    @Test
    @DisplayName("경력순 전체 PB리스트 가져오기")
    void getPBWithCareer() {
        //given
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        List<PBResponse.PBPageDTO> list = Arrays.asList(pbPageDTO1, pbPageDTO2);
        Page<PBResponse.PBPageDTO> pbPG = new PageImpl<>(list, pageable, list.size());

        //stub
        when(pbRepository.findAllPBWithCareer(pageable)).thenReturn(pbPG);

        //when
        PageDTO<PBResponse.PBPageDTO> result = pbService.getPBWithCareer(pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(list.size());
        assertThat(result.getList()).isEqualTo(list);
    }

    @Test
    @DisplayName("맞춤 PB 리스트 가져오기")
    void getRecommendedPBList() {
        // given
        Long memberId = 1L; // Assume
        User user = newMockUser(memberId, "김테스터");
        MyUserDetails myUserDetails = new MyUserDetails(user);
        Pageable pageable = PageRequest.of(0, 10);
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        List<PBResponse.PBPageDTO> list = Arrays.asList(pbPageDTO1, pbPageDTO2);
        Page<PBResponse.PBPageDTO> pbPG = new PageImpl<>(list, pageable, list.size());

        //stub
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(pbRepository.findRecommendedPBList(any(Pageable.class), any(PBSpeciality.class), any(PBSpeciality.class), any(PBSpeciality.class))).thenReturn(pbPG);

        // when
        PageDTOV2<PBResponse.PBPageDTO> result = pbService.getRecommendedPBList(myUserDetails, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(pbPG.getTotalElements());
        assertThat(result.getList().size()).isEqualTo(2);
        assertThat(result.getCurPage()).isEqualTo(pageable.getPageNumber());
        assertThat(result.getList()).isEqualTo(list);
        assertThat(result.getUserPropensity()).isEqualTo(user.getPropensity());
    }

    @Test
    @DisplayName("거리순 PB 두명 가져오기")
    void getTwoPBWithDistance() {
        //given
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBSimpleDTO pbSimpleDTO1 = new PBResponse.PBSimpleDTO(pbPageDTO1);
        PBResponse.PBSimpleDTO pbSimpleDTO2 = new PBResponse.PBSimpleDTO(pbPageDTO2);

        List<PBResponse.PBPageDTO> list = Arrays.asList(pbPageDTO1, pbPageDTO2);
        List<PBResponse.PBSimpleDTO> simpleList = Arrays.asList(pbSimpleDTO1, pbSimpleDTO2);

        //stub
        when(pbRepository.findAllPB()).thenReturn(list);

        //when
        List<PBResponse.PBSimpleDTO> result = pbService.getTwoPBWithDistance(100.0000, 99.9999);

        //then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("PB 프로필가져오기(비회원)")
    void getSimpleProfile() {
        //given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(id, "김피비", this.branch);
        PBResponse.PBSimpleProfileDTO dto = new PBResponse.PBSimpleProfileDTO(this.pb, company);

        //stub
        when(pbRepository.findSimpleProfile(id)).thenReturn(Optional.of(dto));

        //when
        PBResponse.PBSimpleProfileDTO result = pbService.getSimpleProfile(id);

        //then
        assertThat(result.getProfile()).isEqualTo(dto.getProfile());
        assertThat(result.getMsg()).isEqualTo(dto.getMsg());
        assertThat(result.getCompanyLogo()).isEqualTo(dto.getCompanyLogo());
    }

    @Test
    @DisplayName("PB 프로필가져오기(회원)")
    void getPBProfile() {
        //given
        Long id = 1L;
        Member member = User.builder()
                .role(Role.USER).id(1L).build();
        MyUserDetails myUserDetails = new MyUserDetails(member);
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(id, "김피비", this.branch);
        PBResponse.PBProfileDTO dto = new PBResponse.PBProfileDTO(pb, branch, company);

        //stub
        when(pbRepository.findPBProfile(id)).thenReturn(Optional.of(dto));
        when(awardRepository.getAwards(id)).thenReturn(new ArrayList<>());
        when(careerRepository.getCareers(id)).thenReturn(new ArrayList<>());
        when(reviewRepository.countByPBId(id)).thenReturn(1);

        //when
        PBResponse.PBProfileDTO result = pbService.getPBProfile(myUserDetails, id);

        //then
        assertThat(result).isEqualTo(dto);
    }
    @Test
    @DisplayName("PB 포트폴리오 가져오기")
    void getPortfolio() {
        //given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(id, "김피비", branch);

        Portfolio portfolio = newMockPortfolio(1L, pb);

        //stub
        when(pbRepository.findById(id)).thenReturn(Optional.of(pb));
        when(portfolioRepository.findByPbId(id)).thenReturn(Optional.of(portfolio));

        //when
        PBResponse.PortfolioOutDTO result = pbService.getPortfolio(id);

        //then
        assertThat(result.getPbId()).isEqualTo(1L);
        assertThat(result.getFile()).isEqualTo(portfolio.getFile());
        assertThat(result.getAverageProfit()).isEqualTo(portfolio.getAverageProfit());
    }

    @Test
    @DisplayName("PB 프로필 수정용 데이터 가져오기")
    void getPBProfileForUpdate() {
        //given
        Long id = 1L;
        Member member = PB.builder()
                .role(Role.PB).id(1L).build();
        MyUserDetails myUserDetails = new MyUserDetails(member);
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(id, "김피비", branch);
        Portfolio portfolio = newMockPortfolio(1L, pb);
        PBResponse.PBUpdateOutDTO dto = new PBResponse.PBUpdateOutDTO(pb, branch, company, portfolio);

        //stub
        when(pbRepository.findPBProfileForUpdate(id)).thenReturn(Optional.of(dto));
        when(careerRepository.getCareers(id)).thenReturn(new ArrayList<>());
        when(awardRepository.getAwards(id)).thenReturn(new ArrayList<>());

        //when
        PBResponse.PBUpdateOutDTO result = pbService.getPBProfileForUpdate(myUserDetails);

        //then
        assertThat(result).isEqualTo(dto);
        assertThat(result.getBranchName()).isEqualTo(dto.getBranchName());
    }
}