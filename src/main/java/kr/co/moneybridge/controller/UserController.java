package kr.co.moneybridge.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.util.Pair;
import kr.co.moneybridge.core.annotation.MyErrorLog;
import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.auth.jwt.MyJwtProvider;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @MyLog
    @MyErrorLog
    @PostMapping("/join/user")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.JoinUserInDTO joinUserInDTO, Errors errors,  HttpServletResponse response) {
        String password = joinUserInDTO.getPassword();
        UserResponse.JoinUserOutDTO joinUserOutDTO = userService.join(joinUserInDTO);
        Pair<String, String> tokens = userService.issue(joinUserInDTO.getEmail(), password);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(joinUserOutDTO);

        response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight() + "; HttpOnly; Path=/");
        return ResponseEntity.ok()
                .header(MyJwtProvider.HEADER_ACCESS, tokens.getLeft())
                .body(responseDTO);
    }

    // 로그인 성공시 access 토큰과 refresh 토큰 둘 다 발급.
    @MyLog
    @MyErrorLog
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginInDTO loginInDTO, Errors errors, HttpServletResponse response){
        Pair<String, String> tokens = userService.issue(loginInDTO.getEmail(), loginInDTO.getPassword());
        UserResponse.LoginOutDTO loginOutDTO = userService.login(loginInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(loginOutDTO);

        // HttpOnly 플래그 설정 (XSS 방지 - 자바스크립트로 쿠키 접근 불가),
        response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight() + "; HttpOnly; Path=/");
        return ResponseEntity.ok()
                .header(MyJwtProvider.HEADER_ACCESS, tokens.getLeft())
                .body(responseDTO);
    }

    // AccessToken, RefreshToken 재발급을 위한 API
    @MyLog
    @MyErrorLog
    @PostMapping("/reissue")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new Exception400("Cookie", "쿠키에 값이 없습니다");
        }
        Optional<Cookie> cookieOP = Arrays.stream(cookies).filter(cookie ->
                cookie.getName().equals("refreshToken")).findFirst();
        if (cookieOP.isEmpty()) {
            throw new Exception400("Cookie", "리프레시 토큰이 없습니다");
        }
        String refreshToken = cookieOP.get().getValue();

        Pair<String, String> tokens = userService.reissue(request, refreshToken);
        response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight() + "; HttpOnly; Path=/");
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
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
