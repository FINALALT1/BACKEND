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

@RequiredArgsConstructor
@RestController
public class PBController {
    private final PBService pbService;

    @MyLog
    @ApiOperation(value = "PB 회원가입", notes = "<b>joinInDTO 예시 및 설명</b>\n<br>{\n" +
            "&nbsp;&nbsp;\"email\": \"investor2@naver.com\",&nbsp;<font color=\"#C0C0C0\">// @ 포함해야함 + 30바이트 이내</font>\n" +
            "&nbsp;&nbsp;\"password\": \"abcd5678\",&nbsp;<font color=\"#C0C0C0\">// 영문(대소문자), 숫자 포함해서 8자 이상, 공백없이</font>\n" +
            "&nbsp;&nbsp;\"name\": \"김피비\",&nbsp;<font color=\"#C0C0C0\">// 20바이트 이내</font>\n" +
            "&nbsp;&nbsp;\"phoneNumber\": \"01012345678\",&nbsp;<font color=\"#C0C0C0\">// -없이 + (정규식: ^01(?:0|1|[6-9])(?:\\\\d{3}|\\\\d{4})\\\\d{4}$)</font>\n" +
            "&nbsp;&nbsp;\"branchId\": 1,&nbsp;<font color=\"#C0C0C0\">// 지점id</font>\n" +
            "&nbsp;&nbsp;\"career\": 5,&nbsp;<font color=\"#C0C0C0\">// 경력(연차) + 0 또는 양수만 가능</font>\n" +
            "&nbsp;&nbsp;\"speciality1\": \"FUND\",&nbsp;<font color=\"#C0C0C0\">// 전문분야(enum)</font>\n" +
            "&nbsp;&nbsp;\"speciality2\": \"US_STOCK\",&nbsp;<font color=\"#C0C0C0\">// 없어도 됨</font>\n" +
            "&nbsp;&nbsp;\"agreements\": [&nbsp;<font color=\"#C0C0C0\">// 없어도 가능</font>\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;{\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"title\": \"돈줄 이용약관 동의\",&nbsp;<font color=\"#C0C0C0\">// 약관명</font>\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"type\": \"REQUIRED\",&nbsp;<font color=\"#C0C0C0\">// 약관 종류(enum)</font>\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"isAgreed\": true&nbsp;<font color=\"#C0C0C0\">// 동의 여부</font>\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;},\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;{\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"title\": \"마케팅 정보 수신 동의\",\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"type\": \"OPTIONAL\",\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"isAgreed\": true\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;}\n"+
            "&nbsp;&nbsp;}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="businessCard", value="명함 사진"),
            @ApiImplicitParam(name="joinInDTO", value = "joinInDTO")})
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/join/pb")
    public ResponseDTO<PBResponse.JoinOutDTO> joinPB(@RequestPart(value = "businessCard") MultipartFile businessCard,
                                    @RequestPart(value = "joinInDTO") @Valid PBRequest.JoinInDTO joinInDTO, Errors errors) {
        PBResponse.JoinOutDTO joinOutDTO = pbService.joinPB(businessCard, joinInDTO);
        return new ResponseDTO<>(joinOutDTO);
    }

    @ApiOperation("북마크한 PB 목록 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/user/bookmarks/pb")
    public ResponseEntity<?> getBookmarkPBs(@AuthenticationPrincipal MyUserDetails myUserDetails) {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<PBResponse.PBPageDTO> pageDTO = pbService.getBookmarkPBs(myUserDetails, pageable);
        ResponseDTO<PageDTO<PBResponse.PBPageDTO>> responseDTO = new ResponseDTO<>(pageDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation("PB 검색하기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "김피비", dataType = "String", paramType = "query")})
    @GetMapping("/pbs")
    public ResponseEntity<?> getPBWithName(@RequestParam(value = "name") String name) {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<PBResponse.PBPageDTO> pageDTO = pbService.getPBWithName(name, pageable);
        ResponseDTO<PageDTO<PBResponse.PBPageDTO>> responseDTO = new ResponseDTO<>(pageDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/list/pb/distance")
    public ResponseEntity<?> getDistancePBList(@RequestParam(value = "latitude") Double latitude,
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

        ResponseDTO<PageDTO<PBResponse.PBPageDTO>> responseDTO = new ResponseDTO<>(pageDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/list/pb/career")
    public ResponseEntity<?> getCareerPBList(@RequestParam(value = "speciality", required = false) PBSpeciality speciality,
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

        ResponseDTO<PageDTO<PBResponse.PBPageDTO>> responseDTO = new ResponseDTO<>(pageDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/user/list/pb")
    public ResponseEntity<?> getRecommendedPBList(@AuthenticationPrincipal MyUserDetails myUserDetails) {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTOV2<PBResponse.PBPageDTO> pageDTO = pbService.getRecommendedPBList(myUserDetails, pageable);

        ResponseDTO<PageDTOV2<PBResponse.PBPageDTO>> responseDTO = new ResponseDTO<>(pageDTO);

        return ResponseEntity.ok(responseDTO);
    }
}
