package kr.co.moneybridge.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.annotation.SwaggerResponses;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.PageDTOV2;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.pb.PBRequest;
import kr.co.moneybridge.dto.pb.PBResponse;
import kr.co.moneybridge.model.pb.PBSpeciality;
import kr.co.moneybridge.service.PBService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.ArrayList;

@RequiredArgsConstructor
@RestController
public class PBController {
    private final PBService pbService;

    // 투자 성향에 따라 맞춤 분야별 PB리스트 필터링 3개 (나의 투자 성향 분석페이지 하단의 맞춤 PB리스트)
    @MyLog
    @SwaggerResponses.GetMyPropensityPB
    @GetMapping("/user/mypage/list/pb")
    public ResponseDTO<PBResponse.MyPropensityPBOutDTO> getMyPropensityPB(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        PBResponse.MyPropensityPBOutDTO myPropensityPBOutDTO = pbService.getMyPropensityPB(myUserDetails.getMember().getId());
        return new ResponseDTO<>(myPropensityPBOutDTO);
    }

    // PB 마이페이지 가져오기
    @MyLog
    @SwaggerResponses.GetMyPagePB
    @GetMapping("/pb/mypage")
    public ResponseDTO<PBResponse.MyPageOutDTO> getMyPage(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        PBResponse.MyPageOutDTO myPageOutDTO = pbService.getMyPage(myUserDetails);
        return new ResponseDTO<>(myPageOutDTO);
    }

    // 지점 검색
    @MyLog
    @SwaggerResponses.SearchBranch
    @GetMapping("/branch")
    public ResponseDTO<PageDTO<PBResponse.BranchDTO>> searchBranch(@RequestParam Long companyId,
                                                                   @RequestParam(required = false) String keyword) {
        keyword = keyword == null ? "" : keyword.replaceAll("\\s", "");
        if (keyword.isEmpty()) {
            List<PBResponse.BranchDTO> empty = new ArrayList<>();
            return new ResponseDTO<>(new PageDTO<>(empty, new PageImpl<>(empty))); // 빈칸 검색시
        }
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        PageDTO<PBResponse.BranchDTO> pageDTO = pbService.searchBranch(companyId, keyword, pageable);
        return new ResponseDTO<>(pageDTO);
    }

    // 증권사 리스트 가져오기 - 메인페이지, 회원가입시
    @MyLog
    @SwaggerResponses.GetCompanies
    @GetMapping("/companies")
    public ResponseEntity<?> getCompanies(@RequestParam(defaultValue = "true") Boolean includeLogo) {
        ResponseDTO<?> responseDTO = null;
        if (includeLogo) {
            PBResponse.CompanyOutDTO companyOutDTO = pbService.getCompanies();
            responseDTO = new ResponseDTO<>(companyOutDTO);
        } else {
            PBResponse.CompanyNameOutDTO companyNameOutDTO = pbService.getCompanyNames();
            responseDTO = new ResponseDTO<>(companyNameOutDTO);
        }
        return ResponseEntity.ok().body(responseDTO);
    }

    // PB 회원가입
    @MyLog
    @SwaggerResponses.JoinPB
    @PostMapping("/join/pb")
    public ResponseDTO<PBResponse.JoinOutDTO> joinPB(@RequestPart(value = "businessCard") MultipartFile businessCard,
                                                     @RequestPart(value = "joinInDTO") @Valid PBRequest.JoinInDTO joinInDTO, Errors errors) {
        PBResponse.JoinOutDTO joinOutDTO = pbService.joinPB(businessCard, joinInDTO);
        return new ResponseDTO<>(joinOutDTO);
    }

    @ApiOperation("북마크한 PB 목록 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParam(name = "page", value = "0")
    @GetMapping("/user/bookmarks/pb")
    public ResponseDTO<PageDTO<PBResponse.PBPageDTO>> getBookmarkPBs(@AuthenticationPrincipal MyUserDetails myUserDetails,
                                                                     @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<PBResponse.PBPageDTO> pageDTO = pbService.getBookmarkPBs(myUserDetails, pageable);

        return new ResponseDTO<>(pageDTO);
    }

    @ApiOperation("PB 검색하기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParam(name = "page", value = "0")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "김피비", dataType = "String", paramType = "query")})
    @GetMapping("/pbs")
    public ResponseDTO<PageDTO<PBResponse.PBPageDTO>> getPBWithName(@RequestParam(value = "name") String name,
                                                                    @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<PBResponse.PBPageDTO> pageDTO = pbService.getPBWithName(name, pageable);

        return new ResponseDTO<>(pageDTO);
    }

    @ApiOperation("PB 리스트 가져오기(거리순)-비로그인")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "latitude", value = "127.0000", dataType = "Double", paramType = "query", required = true),
            @ApiImplicitParam(name = "longitude", value = "81.1111", dataType = "Double", paramType = "query", required = true),
            @ApiImplicitParam(name = "speciality", value = "ETF", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "company", value = "1", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "0")})
    @GetMapping("/list/pb/distance")
    public ResponseDTO<PageDTO<PBResponse.PBPageDTO>> getDistancePBList(@RequestParam(value = "latitude") Double latitude,
                                                                        @RequestParam(value = "longitude") Double longitude,
                                                                        @RequestParam(value = "speciality", required = false) PBSpeciality speciality,
                                                                        @RequestParam(value = "company", required = false) Long company,
                                                                        @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10);
        PageDTO<PBResponse.PBPageDTO> pageDTO;

        if (speciality != null && company != null) {
            throw new Exception404("잘못된 요청입니다.");
        } else if (speciality != null) {
            pageDTO = pbService.getSpecialityPBWithDistance(latitude, longitude, speciality, pageable);
        } else if (company != null) {
            pageDTO = pbService.getCompanyPBWithDistance(latitude, longitude, company, pageable);
        } else {
            pageDTO = pbService.getPBWithDistance(latitude, longitude, pageable);
        }

        return new ResponseDTO<>(pageDTO);
    }

    @ApiOperation("PB 리스트 가져오기(거리순) - 로그인")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "latitude", value = "127.0000", dataType = "Double", paramType = "query", required = true),
            @ApiImplicitParam(name = "longitude", value = "81.1111", dataType = "Double", paramType = "query", required = true),
            @ApiImplicitParam(name = "speciality", value = "ETF", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "company", value = "1", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "0")})
    @GetMapping("/auth/list/pb/distance")
    public ResponseDTO<PageDTO<PBResponse.PBPageDTO>> getLoginDistancePBList(@RequestParam(value = "latitude") Double latitude,
                                                                             @RequestParam(value = "longitude") Double longitude,
                                                                             @RequestParam(value = "speciality", required = false) PBSpeciality speciality,
                                                                             @RequestParam(value = "company", required = false) Long company,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @AuthenticationPrincipal MyUserDetails myUserDetails) {

        Pageable pageable = PageRequest.of(page, 10);
        PageDTO<PBResponse.PBPageDTO> pageDTO;

        if (speciality != null && company != null) {
            throw new Exception404("잘못된 요청입니다.");
        } else if (speciality != null) {
            pageDTO = pbService.getLoginSpecialityPBWithDistance(myUserDetails, latitude, longitude, speciality, pageable);
        } else if (company != null) {
            pageDTO = pbService.getLoginCompanyPBWithDistance(myUserDetails, latitude, longitude, company, pageable);
        } else {
            pageDTO = pbService.getLoginPBWithDistance(myUserDetails, latitude, longitude, pageable);
        }

        return new ResponseDTO<>(pageDTO);
    }

    @ApiOperation("PB 리스트 가져오기(경력순) - 비로그인")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "speciality", value = "ETF", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "company", value = "1", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "0")})
    @GetMapping("/list/pb/career")
    public ResponseDTO<PageDTO<PBResponse.PBPageDTO>> getCareerPBList(@RequestParam(value = "speciality", required = false) PBSpeciality speciality,
                                                                      @RequestParam(value = "company", required = false) Long company,
                                                                      @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10);
        PageDTO<PBResponse.PBPageDTO> pageDTO;

        if (speciality != null && company != null) {
            throw new Exception404("잘못된 요청입니다.");
        } else if (speciality != null) {
            pageDTO = pbService.getSpecialityPBWithCareer(speciality, pageable);
        } else if (company != null) {
            pageDTO = pbService.getCompanyPBWithCareer(company, pageable);
        } else {
            pageDTO = pbService.getPBWithCareer(pageable);
        }

        return new ResponseDTO<>(pageDTO);
    }

    @ApiOperation("PB 리스트 가져오기(경력순) - 로그인")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "speciality", value = "ETF", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "company", value = "1", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "0")})
    @GetMapping("/auth/list/pb/career")
    public ResponseDTO<PageDTO<PBResponse.PBPageDTO>> getLoginCareerPBList(@RequestParam(value = "speciality", required = false) PBSpeciality speciality,
                                                                           @RequestParam(value = "company", required = false) Long company,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @AuthenticationPrincipal MyUserDetails myUserDetails) {

        Pageable pageable = PageRequest.of(page, 10);
        PageDTO<PBResponse.PBPageDTO> pageDTO;

        if (speciality != null && company != null) {
            throw new Exception404("잘못된 요청입니다.");
        } else if (speciality != null) {
            pageDTO = pbService.getLoginSpecialityPBWithCareer(myUserDetails, speciality, pageable);
        } else if (company != null) {
            pageDTO = pbService.getLoginCompanyPBWithCareer(myUserDetails, company, pageable);
        } else {
            pageDTO = pbService.getLoginPBWithCareer(myUserDetails, pageable);
        }

        return new ResponseDTO<>(pageDTO);
    }

    @ApiOperation("맞춤성향 PB 리스트")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParam(name = "page", value = "0")
    @GetMapping("/user/list/pb")
    public ResponseDTO<PageDTOV2<PBResponse.PBPageDTO>> getRecommendedPBList(@AuthenticationPrincipal MyUserDetails myUserDetails,
                                                                             @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTOV2<PBResponse.PBPageDTO> pageDTO = pbService.getRecommendedPBList(myUserDetails, pageable);

        return new ResponseDTO<>(pageDTO);
    }

    @ApiOperation("PB 리스트 가져오기(거리순)")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "latitude", value = "127.0000", dataType = "Double", paramType = "query", required = true),
            @ApiImplicitParam(name = "longitude", value = "81.1111", dataType = "Double", paramType = "query", required = true)})
    @GetMapping("/main/pb")
    public ResponseDTO<List<PBResponse.PBSimpleDTO>> getRecommendedPB(@RequestParam(value = "latitude") Double latitude,
                                                                      @RequestParam(value = "longitude") Double longitude) {

        List<PBResponse.PBSimpleDTO> pbList = pbService.getTwoPBWithDistance(latitude, longitude);
        PBResponse.PBListOutDTO listOutDTO = new PBResponse.PBListOutDTO(pbList);

        return new ResponseDTO(listOutDTO);
    }

    @ApiOperation("PB 프로필가져오기(비회원)")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/profile/{id}")
    public ResponseDTO<PBResponse.PBSimpleProfileDTO> getSimpleProfile(@PathVariable(value = "id") Long id) {

        PBResponse.PBSimpleProfileDTO pbDTO = pbService.getSimpleProfile(id);

        return new ResponseDTO<>(pbDTO);
    }

    @ApiOperation("PB 프로필가져오기(회원)")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/auth/profile/{id}")
    public ResponseDTO<PBResponse.PBProfileDTO> getPBProfile(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable(value = "id") Long id) {

        PBResponse.PBProfileDTO pbDTO = pbService.getPBProfile(myUserDetails, id);

        return new ResponseDTO<>(pbDTO);
    }

    @ApiOperation("PB 프로필가져오기(회원)")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/auth/portfolio/{id}")
    public ResponseDTO<PBResponse.PortfolioOutDTO> getPBPortfolio(@PathVariable(value = "id") Long id) {

        PBResponse.PortfolioOutDTO portfolioDTO = pbService.getPortfolio(id);

        return new ResponseDTO<>(portfolioDTO);
    }

    @ApiOperation("PB 프로필 수정용 데이터 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/pb/portfolio/update")
    public ResponseDTO<PBResponse.PBUpdateOutDTO> getPBProfileForUpdate(@AuthenticationPrincipal MyUserDetails myUserDetails) {

        PBResponse.PBUpdateOutDTO updateDTO = pbService.getPBProfileForUpdate(myUserDetails);

        return new ResponseDTO<>(updateDTO);
    }

    @ApiOperation("PB 프로필 수정하기")
    @SwaggerResponses.DefaultApiResponses
    @PutMapping("/pb/profile")
    public ResponseDTO updateProfile(@AuthenticationPrincipal MyUserDetails myUserDetails,
                                     @RequestPart(value = "profileFile", required = false) MultipartFile profileFile,
                                     @RequestPart(value = "portfolioFile", required = false) MultipartFile portfolioFile,
                                     @RequestPart(value = "updateDTO") PBRequest.UpdateProfileInDTO updateDTO) {

        pbService.updateProfile(myUserDetails, updateDTO, profileFile, portfolioFile);

        return new ResponseDTO<>();
    }

    @ApiOperation("유사한 PB 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/auth/{pbId}/same")
    public ResponseDTO<List<PBResponse.PBPageDTO>> getSamePBs(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable(value = "pbId") Long pbId) {

        List<PBResponse.PBPageDTO> list = pbService.getSamePBs(myUserDetails, pbId);
        PBResponse.PBListOutDTO pbListOutDTO = new PBResponse.PBListOutDTO(list);

        return new ResponseDTO(pbListOutDTO);
    }

}
