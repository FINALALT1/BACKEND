package kr.co.moneybridge.service;

import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.PageDTOV2;
import kr.co.moneybridge.dto.pb.PBRequest;
import kr.co.moneybridge.dto.pb.PBResponse;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.reservation.ReservationProcess;
import kr.co.moneybridge.model.reservation.ReservationRepository;
import kr.co.moneybridge.model.reservation.ReviewRepository;
import kr.co.moneybridge.model.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PBService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final BranchRepository branchRepository;
    private final PBRepository pbRepository;
    private final PBAgreementRepository pbAgreementRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final AwardRepository awardRepository;
    private final CareerRepository careerRepository;
    private final UserBookmarkRepository userBookmarkRepository;
    private final PortfolioRepository portfolioRepository;


    @MyLog
    @Transactional
    public PBResponse.MyPageOutDTO getMyPage(MyUserDetails myUserDetails) {
        String email = myUserDetails.getMember().getEmail();
        Optional<PB> pbOP = pbRepository.findByEmail(email);
        if(!pbOP.isPresent()){
            throw new Exception500("PB 계정이 없습니다");
        }
        return new PBResponse.MyPageOutDTO(pbOP.get(),
                reservationRepository.countByPBIdAndProcess(pbOP.get().getId(), ReservationProcess.COMPLETE),
                reviewRepository.countByPBId(pbOP.get().getId()));
    }

    @MyLog
    public PageDTO<PBResponse.BranchDTO> searchBranch(Long companyId, String keyword, Pageable pageable) {
        Page<Branch> branchPG = branchRepository.findByCompanyIdAndKeyword(companyId, keyword, pageable);
        List<PBResponse.BranchDTO> list = branchPG.getContent().stream()
                .map(branch -> new PBResponse.BranchDTO(branch))
                .collect(Collectors.toList());
        return new PageDTO<>(list, branchPG, Branch.class);
    }

    @MyLog
    public PBResponse.CompanyNameOutDTO getCompanyNames() {
        List<PBResponse.CompanyNameDTO> list = new ArrayList<>();
        companyRepository.findAll().stream().forEach(company -> {
            list.add(new PBResponse.CompanyNameDTO(company));
        });
        return new PBResponse.CompanyNameOutDTO(list);
    }

    @MyLog
    public PBResponse.CompanyOutDTO getCompanies() {
        List<PBResponse.CompanyDTO> list = new ArrayList<>();
        companyRepository.findAll().stream().forEach(company -> {
            list.add(new PBResponse.CompanyDTO(company));
        });
        return new PBResponse.CompanyOutDTO(list);
    }

    @MyLog
    @Transactional
    public PBResponse.JoinOutDTO joinPB(MultipartFile businessCard, PBRequest.JoinInDTO joinInDTO) {
        Optional<PB> pbOP = pbRepository.findByEmail(joinInDTO.getEmail());
        if (pbOP.isPresent()) {
            if (pbOP.get().getStatus().equals(PBStatus.PENDING)) {
                throw new Exception400("email", "회원가입 후 승인을 기다리고 있는 PB 계정입니다");
            }
            throw new Exception400("email", "이미 PB로 회원가입된 이메일입니다");
        }
        String encPassword = passwordEncoder.encode(joinInDTO.getPassword()); // 60Byte
        joinInDTO.setPassword(encPassword);

        Branch branchPS = branchRepository.findById(joinInDTO.getBranchId()).orElseThrow(
                () -> new Exception400("branchId", "해당하는 지점이 존재하지 않습니다")
        );
        if (businessCard == null || businessCard.isEmpty()) {
            throw new Exception400("businessCard", "명함 사진이 없습니다");
        }
        // 압축해서 S3에 사진 저장하는 부분 추가 필요함
        String fileName = businessCard.getOriginalFilename();
        try {
            PB pbPS = pbRepository.save(joinInDTO.toEntity(branchPS, fileName));
            List<PBRequest.AgreementDTO> agreements = joinInDTO.getAgreements();
            if (agreements != null) {
                agreements.stream().forEach(agreement ->
                        pbAgreementRepository.save(agreement.toEntity(pbPS)));
            }
            return new PBResponse.JoinOutDTO(pbPS);
        } catch (Exception e) {
            throw new Exception500("회원가입 실패 : " + e.getMessage());
        }
    }

    //북마크한 pb 목록 가져오기
    public PageDTO<PBResponse.PBPageDTO> getBookmarkPBs(MyUserDetails myUserDetails, Pageable pageable) {

        User user = userRepository.findById(myUserDetails.getMember().getId()).orElseThrow(() -> new Exception404("존재하지 않는 회원입니다."));

        try {
            Page<PBResponse.PBPageDTO> pbPG = pbRepository.findByUserId(user.getId(), pageable);

            for (PBResponse.PBPageDTO pbPageDTO : pbPG) {
                Long pbId = pbPageDTO.getId();
                pbPageDTO.setReserveCount(pbRepository.countReservationsByPbId(pbId));
                pbPageDTO.setReviewCount(pbRepository.countReviewsByPbId(pbId));
            }

            List<PBResponse.PBPageDTO> list = pbPG.getContent().stream().collect(Collectors.toList());
            return new PageDTO<>(list, pbPG);
        } catch (Exception e) {
            throw new Exception500("PB 목록 조회 에러");
        }
    }

    public PB getPB(Long id) {
        return pbRepository.findById(id).orElseThrow(
                () -> new Exception404("해당 PB 는 찾을 수 없습니다.")
        );
    }

    //PB 검색하기
    public PageDTO<PBResponse.PBPageDTO> getPBWithName(String name, Pageable pageable) {

        Page<PBResponse.PBPageDTO> pbPG = pbRepository.findByName(name, pageable);
        List<PBResponse.PBPageDTO> list = pbPG.getContent().stream().collect(Collectors.toList());
        return new PageDTO<>(list, pbPG);
    }

    //거리순 PB리스트 가져오기(전문분야필터)
    public PageDTO<PBResponse.PBPageDTO> getSpecialityPBWithDistance(Double latitude, Double longitude, PBSpeciality speciality, Pageable pageable) {

        List<PBResponse.PBPageDTO> list = pbRepository.findByPBListSpeciality(speciality);
        list.sort(Comparator.comparing(dto -> calDistance(latitude, longitude, dto.getBranchLat(), dto.getBranchLon())));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        Page<PBResponse.PBPageDTO> pbPG = new PageImpl(list.subList(start, end), pageable, list.size());
        return new PageDTO<>(list, pbPG);
    }

    //거리순 PB리스트 가져오기(증권사필터)
    public PageDTO<PBResponse.PBPageDTO> getCompanyPBWithDistance(Double latitude, Double longitude, Long companyId, Pageable pageable) {

        List<PBResponse.PBPageDTO> list = pbRepository.findByPBListCompany(companyId);
        list.sort(Comparator.comparing(dto -> calDistance(latitude, longitude, dto.getBranchLat(), dto.getBranchLon())));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        Page<PBResponse.PBPageDTO> pbPG = new PageImpl(list.subList(start, end), pageable, list.size());
        return new PageDTO<>(list, pbPG);
    }

    //거리순 전체PB리스트 가져오기
    public PageDTO<PBResponse.PBPageDTO> getPBWithDistance(Double latitude, Double longitude, Pageable pageable) {

        List<PBResponse.PBPageDTO> list = pbRepository.findAllPB();
        list.sort(Comparator.comparing(dto -> calDistance(latitude, longitude, dto.getBranchLat(), dto.getBranchLon())));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        Page<PBResponse.PBPageDTO> pbPG = new PageImpl(list.subList(start, end), pageable, list.size());
        return new PageDTO<>(list, pbPG);
    }

    //거리계산 메서드
    public static double calDistance(double lat1, double lon1, double lat2, double lon2) {
        final double KILL = 111.32; // 위도 1도 거리(킬로미터)

        double x = lat1 - lat2;
        double y = (lon1 - lon2) * Math.cos((lat1 + lat2) / 2);

        return KILL * Math.sqrt(x * x + y * y);
    }

    //경력순 PB리스트 가져오기(전문분야필터)
    public PageDTO<PBResponse.PBPageDTO> getSpecialityPBWithCareer(PBSpeciality speciality, Pageable pageable) {

        Page<PBResponse.PBPageDTO> pbPG = pbRepository.findBySpecialityOrderedByCareer(speciality, pageable);
        List<PBResponse.PBPageDTO> list = pbPG.getContent().stream().collect(Collectors.toList());

        return new PageDTO<>(list, pbPG);
    }

    //경력순 PB리스트 가져오기(증권사필터)
    public PageDTO<PBResponse.PBPageDTO> getCompanyPBWithCareer(Long companyId, Pageable pageable) {

        Page<PBResponse.PBPageDTO> pbPG = pbRepository.findByCompanyIdOrderedByCareer(companyId, pageable);
        List<PBResponse.PBPageDTO> list = pbPG.getContent().stream().collect(Collectors.toList());

        return new PageDTO<>(list, pbPG);
    }

    //경력순 전체 PB리스트 가져오기
    public PageDTO<PBResponse.PBPageDTO> getPBWithCareer(Pageable pageable) {

        Page<PBResponse.PBPageDTO> pbPG = pbRepository.findAllPBWithCareer(pageable);
        List<PBResponse.PBPageDTO> list = pbPG.getContent().stream().collect(Collectors.toList());

        return new PageDTO<>(list, pbPG);
    }

    //맞춤 PB 리스트 가져오기
    public PageDTOV2<PBResponse.PBPageDTO> getRecommendedPBList(MyUserDetails myUserDetails, Pageable pageable) {

        User user = userRepository.findById(myUserDetails.getMember().getId()).orElseThrow(() -> new Exception404("존재하지 않는 회원입니다."));
        UserPropensity propensity = user.getPropensity();
        Page<PBResponse.PBPageDTO> pbPG;

        if (propensity == null) {
            throw new Exception404("투자성향 분석이 되지않았습니다.");
        } else if (propensity.equals(UserPropensity.CONSERVATIVE)) {
            pbPG = pbRepository.findRecommendedPBList(pageable, PBSpeciality.BOND,
                                                                PBSpeciality.US_STOCK,
                                                                PBSpeciality.KOREAN_STOCK,
                                                                PBSpeciality.FUND,
                                                                PBSpeciality.DERIVATIVE,
                                                                PBSpeciality.ETF,
                                                                PBSpeciality.WRAP);
        } else if (propensity.equals(UserPropensity.CAUTIOUS)) {
            pbPG = pbRepository.findRecommendedPBList(pageable, PBSpeciality.BOND,
                                                                PBSpeciality.US_STOCK,
                                                                PBSpeciality.KOREAN_STOCK,
                                                                PBSpeciality.FUND,
                                                                PBSpeciality.ETF,
                                                                PBSpeciality.WRAP);
        } else {
            pbPG = pbRepository.findRecommendedPBList(pageable, PBSpeciality.BOND,
                                                                PBSpeciality.FUND,
                                                                PBSpeciality.WRAP);
        }

        List<PBResponse.PBPageDTO> list = pbPG.getContent().stream().collect(Collectors.toList());

        PageDTOV2<PBResponse.PBPageDTO> pageDTO = new PageDTOV2<>(list, pbPG);
        pageDTO.setUserPropensity(propensity);

        return pageDTO;
    }

    //거리순 PB 두명 가져오기
    public List<PBResponse.PBSimpleDTO> getTwoPBWithDistance(Double latitude, Double longitude) {

        List<PBResponse.PBSimpleDTO> pbList = new ArrayList<>();
        List<PBResponse.PBPageDTO> list = pbRepository.findAllPB();
        list.sort(Comparator.comparing(dto -> calDistance(latitude, longitude, dto.getBranchLat(), dto.getBranchLon())));
        pbList.add(new PBResponse.PBSimpleDTO(list.get(0)));
        pbList.add(new PBResponse.PBSimpleDTO(list.get(1)));

        return pbList;
    }

    //PB 프로필가져오기(비회원)
    public PBResponse.PBSimpleProfileDTO getSimpleProfile(Long id) {

        PBResponse.PBSimpleProfileDTO pbDTO = pbRepository.findSimpleProfile(id).orElseThrow(() -> new Exception404("해당 PB 존재하지 않습니다"));

        return pbDTO;
    }

    //PB 프로필가져오기(회원)
    public PBResponse.PBProfileDTO getPBProfile(MyUserDetails myUserDetails, Long id) {

        PBResponse.PBProfileDTO pbDTO = pbRepository.findPBProfile(id).orElseThrow(()-> new Exception404("해당 PB 존재하지 않습니다."));
        pbDTO.setAward(awardRepository.getAwards(id));
        pbDTO.setCareer(careerRepository.getCareers(id));
        pbDTO.setReserveCount(reservationRepository.countByPBIdAndProcess(id, ReservationProcess.COMPLETE));
        pbDTO.setReviewCount(reviewRepository.countByPBId(id));

        if (myUserDetails.getMember().getRole().equals(Role.USER)) {
            Optional<UserBookmark> bookmark = userBookmarkRepository.findByUserIdWithPbId(myUserDetails.getMember().getId(), id);
            if (bookmark.isPresent()) pbDTO.setIsBookmarked(true);
        }

        return pbDTO;
    }

    //PB 포트폴리오 가져오기
    public PBResponse.PortfolioOutDTO getPortfolio(Long id) {

        if(pbRepository.findById(id).isEmpty()) throw new Exception404("존재하지 않는 PB 입니다.");

        Optional<Portfolio> portfolioOP = portfolioRepository.findByPbId(id);
        PBResponse.PortfolioOutDTO dto = new PBResponse.PortfolioOutDTO();

        if (portfolioOP.isPresent()) {
            Portfolio portfolio = portfolioOP.get();
            dto.setPbId(portfolio.getPb().getId());
            dto.setCumulativeReturn(portfolio.getCumulativeReturn());
            dto.setMaxDrawdown(portfolio.getMaxDrawdown());
            dto.setProfitFactor(portfolio.getProfitFactor());
            dto.setAverageProfit(portfolio.getAverageProfit());
            dto.setFile(portfolio.getFile());
        }

        return dto;
    }
}
