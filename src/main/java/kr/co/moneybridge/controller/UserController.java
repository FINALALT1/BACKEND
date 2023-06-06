package kr.co.moneybridge.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.moneybridge.core.annotation.MyErrorLog;
import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.auth.jwt.MyJwtProvider;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception403;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.user.UserRequest;
import kr.co.moneybridge.dto.user.UserResponse;
import kr.co.moneybridge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @MyLog
    @MyErrorLog
    @PostMapping("/join/user")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.JoinUserInDTO joinUserInDTO, Errors errors) {
        UserResponse.JoinUserOutDTO joinUserOutDTO = userService.회원가입(joinUserInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(joinUserOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest.LoginInDTO loginInDTO){
        String jwt = userService.로그인(loginInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().header(MyJwtProvider.HEADER, jwt).body(responseDTO);
    }

    @GetMapping("/s/user/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) throws JsonProcessingException {
        if(id.longValue() != myUserDetails.getUser().getId()){
            throw new Exception403("권한이 없습니다");
        }
        UserResponse.DetailOutDTO detailOutDTO = userService.회원상세보기(id);
        //System.out.println(new ObjectMapper().writeValueAsString(detailOutDTO));
        ResponseDTO<?> responseDTO = new ResponseDTO<>(detailOutDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
