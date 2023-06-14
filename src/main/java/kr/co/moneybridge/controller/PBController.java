package kr.co.moneybridge.controller;

import io.swagger.annotations.ApiOperation;
import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.annotation.SwaggerResponses;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.pb.PBRequest;
import kr.co.moneybridge.dto.pb.PBResponse;
import kr.co.moneybridge.service.PBService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @ApiOperation(value = "PB 회원가입")
    @SwaggerResponses.DefaultApiResponses
    @MyLog
    @PostMapping("/join/pb")
    public ResponseEntity<?> joinPB(@RequestPart(value = "businessCard") MultipartFile businessCard,
                                    @RequestPart(value = "joinInDTO") @Valid PBRequest.JoinInDTO joinInDTO, Errors errors) {
        PBResponse.JoinOutDTO joinOutDTO = pbService.joinPB(businessCard, joinInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(joinOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/user/bookmarks/pb")
    public ResponseEntity<?> getBookmarkPBs(@AuthenticationPrincipal MyUserDetails myUserDetails) {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<PBResponse.PBPageDTO> pageDTO = pbService.getBookmarkPBs(myUserDetails, pageable);
        ResponseDTO<PageDTO<PBResponse.PBPageDTO>> responseDTO = new ResponseDTO<>(pageDTO);

        return ResponseEntity.ok(responseDTO);
    }
}
