package kr.co.moneybridge.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.util.Pair;
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


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @MyLog
    @MyErrorLog
    @PostMapping("/join/user")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.JoinUserInDTO joinUserInDTO, Errors errors,  HttpServletResponse response) {
        String password = joinUserInDTO.getPassword();
        UserResponse.JoinUserOutDTO joinUserOutDTO = userService.회원가입(joinUserInDTO);
        Pair<String, String> tokens = userService.토큰발급(joinUserInDTO.getEmail(), password);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(joinUserOutDTO);

        response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight() + "; HttpOnly; SameSite=strict; Path=/");
        // 개발 완료 후 https로 연결하면 아래코드로 변경
        // response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight() + "; HttpOnly; SameSite=strict; Path=/; Secure");

        return ResponseEntity.ok()
                .header(MyJwtProvider.HEADER_ACCESS, tokens.getLeft())
                .body(responseDTO);
    }

    // 로그인 성공시 access 토큰과 refresh 토큰 둘 다 발급.
    @MyLog
    @MyErrorLog
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginInDTO loginInDTO, Errors errors, HttpServletResponse response){
        Pair<String, String> tokens = userService.토큰발급(loginInDTO.getEmail(), loginInDTO.getPassword());
        UserResponse.LoginOutDTO loginOutDTO = userService.로그인(loginInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(loginOutDTO);

        // HttpOnly(XSS 방지 - 자바스크립트로 쿠키 접근 불가), SameSite=strict(CSRF 방지- 쿠키는 동일한 사이트로만 전송), Secure(중간자 공격(MITM) 방지 - https로만 쿠키 전송)
        response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight() + "; HttpOnly; SameSite=strict; Path=/");
        // 개발 완료 후 https로 연결하면 아래코드로 변경
        // response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight() + "; HttpOnly; SameSite=strict; Path=/; Secure");

        return ResponseEntity.ok()
                .header(MyJwtProvider.HEADER_ACCESS, tokens.getLeft())
                .body(responseDTO);
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
