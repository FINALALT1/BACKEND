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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PBController {
    private final PBService pbService;

    // PB 마이페이지 가져오기
    @MyLog
    @GetMapping("/pb/mypage")
    public ResponseEntity<?> getMyPage(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        PBResponse.MyPageOutDTO myPageOutDTO = pbService.getMyPage(myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(myPageOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }


    // 지점 검색
    @MyLog
    @SwaggerResponses.SearchBranch
    @GetMapping("/branch")
    public ResponseDTO<PageDTO<PBResponse.BranchDTO>> searchBranch(@RequestParam Long companyId,
                                          @RequestParam(required = false) String keyword) {
        keyword = keyword == null ? "" : keyword.replaceAll("\\s", "");
        if(keyword.isEmpty()){
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
        if(includeLogo){
            PBResponse.CompanyOutDTO companyOutDTO = pbService.getCompanies();
            responseDTO = new ResponseDTO<>(companyOutDTO);
        }
        else {
            PBResponse.CompanyNameOutDTO companyNameOutDTO = pbService.getCompanyNames();
            responseDTO = new ResponseDTO<>(companyNameOutDTO);
        }
        return ResponseEntity.ok().body(responseDTO);
    }

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
    @GetMapping("/user/bookmarks/pb")
    public ResponseDTO<PageDTO<PBResponse.PBPageDTO>> getBookmarkPBs(@AuthenticationPrincipal MyUserDetails myUserDetails) {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<PBResponse.PBPageDTO> pageDTO = pbService.getBookmarkPBs(myUserDetails, pageable);

        return new ResponseDTO<>(pageDTO);
    }

    @ApiOperation("PB 검색하기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "김피비", dataType = "String", paramType = "query")})
    @GetMapping("/pbs")
    public ResponseDTO<PageDTO<PBResponse.PBPageDTO>> getPBWithName(@RequestParam(value = "name") String name) {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<PBResponse.PBPageDTO> pageDTO = pbService.getPBWithName(name, pageable);

        return new ResponseDTO<>(pageDTO);
    }

    @ApiOperation("PB 리스트 가져오기(거리순)")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "latitude", value = "127.0000", dataType = "Double", paramType = "query", required = true),
                        @ApiImplicitParam(name = "longitude", value = "81.1111", dataType = "Double", paramType = "query", required = true),
                        @ApiImplicitParam(name = "speciality", value = "ETF", dataType = "String", paramType = "query"),
                        @ApiImplicitParam(name = "company", value = "1", dataType = "Long", paramType = "query")})
    @GetMapping("/list/pb/distance")
    public ResponseDTO<PageDTO<PBResponse.PBPageDTO>> getDistancePBList(@RequestParam(value = "latitude") Double latitude,
                                                                        @RequestParam(value = "longitude") Double longitude,
                                                                        @RequestParam(value = "speciality", required = false) PBSpeciality speciality,
                                                                        @RequestParam(value = "company", required = false) Long company) {

        Pageable pageable = PageRequest.of(0, 10);
        PageDTO<PBResponse.PBPageDTO> pageDTO;

        if (speciality != null & company != null) {
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

    @ApiOperation("PB 리스트 가져오기(경력순)")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "speciality", value = "ETF", dataType = "String", paramType = "query"),
                        @ApiImplicitParam(name = "company", value = "1", dataType = "Long", paramType = "query")})
    @GetMapping("/list/pb/career")
    public ResponseDTO<PageDTO<PBResponse.PBPageDTO>> getCareerPBList(@RequestParam(value = "speciality", required = false) PBSpeciality speciality,
                                             @RequestParam(value = "company", required = false) Long company) {

        Pageable pageable = PageRequest.of(0, 10);
        PageDTO<PBResponse.PBPageDTO> pageDTO;

        if (speciality != null & company != null) {
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

    @ApiOperation("맞춤성향 PB 리스트")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/user/list/pb")
    public ResponseDTO<PageDTOV2<PBResponse.PBPageDTO>> getRecommendedPBList(@AuthenticationPrincipal MyUserDetails myUserDetails) {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
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

        return new ResponseDTO(pbList);
    }
}
