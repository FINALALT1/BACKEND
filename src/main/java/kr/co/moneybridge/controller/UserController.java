package kr.co.moneybridge.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.util.Pair;
import io.swagger.annotations.ApiOperation;
import kr.co.moneybridge.core.annotation.MyErrorLog;
import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.annotation.SwaggerResponses;
import kr.co.moneybridge.core.auth.jwt.MyJwtProvider;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.core.exception.Exception403;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.user.UserRequest;
import kr.co.moneybridge.dto.user.UserResponse;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


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

    @ApiOperation(value = "탈퇴")
    @SwaggerResponses.DefaultApiResponses
    @MyLog
    @DeleteMapping("/auth/account")
    public ResponseEntity<?> withdraw(@RequestBody @Valid UserRequest.WithdrawInDTO withdrawInDTO, Errors errors,
                                      @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.withdraw(withdrawInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    @ApiOperation(value = "투자자 회원가입")
    @SwaggerResponses.DefaultApiResponses
    @MyLog
    @PostMapping("/join/user")
    public ResponseEntity<?> joinUser(@RequestBody @Valid UserRequest.JoinInDTO joinInDTO, Errors errors, HttpServletResponse response) {
        String rawPassword = joinInDTO.getPassword();
        UserResponse.JoinOutDTO joinOutDTO = userService.joinUser(joinInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(joinOutDTO);
        // 회원가입 완료시 자동로그인
        Pair<String, String> tokens = userService.issue(Role.USER, joinInDTO.getEmail(), rawPassword);
        response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight() + "; HttpOnly; Path=/");
        return ResponseEntity.ok()
                .header(MyJwtProvider.HEADER_ACCESS, tokens.getLeft())
                .body(responseDTO);
    }

    // 로그인 성공시 access 토큰과 refresh 토큰 둘 다 발급.
    @ApiOperation(value = "로그인")
    @SwaggerResponses.DefaultApiResponses
    @MyLog
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginInDTO loginInDTO, Errors errors, HttpServletResponse response){
        UserResponse.LoginOutDTO loginOutDTO = userService.login(loginInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(loginOutDTO);
        Pair<String, String> tokens = userService.issue(loginInDTO.getRole(), loginInDTO.getEmail(), loginInDTO.getPassword());
        // HttpOnly 플래그 설정 (XSS 방지 - 자바스크립트로 쿠키 접근 불가),
        response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight() + "; HttpOnly; Path=/");
        return ResponseEntity.ok()
                .header(MyJwtProvider.HEADER_ACCESS, tokens.getLeft())
                .body(responseDTO);
    }

    // AccessToken, RefreshToken 재발급을 위한 API
    @ApiOperation(value = "토큰 재발급")
    @SwaggerResponses.DefaultApiResponses
    @MyLog
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        Pair<String, String> tokens = userService.reissue(request, getRefreshToken(request));
        response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight() + "; HttpOnly; Path=/");
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok()
                .header(MyJwtProvider.HEADER_ACCESS, tokens.getLeft())
                .body(responseDTO);
    }

    @ApiOperation(value = "로그아웃")
    @SwaggerResponses.DefaultApiResponses
    @MyLog
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        userService.logout(request, getRefreshToken(request));
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    private String getRefreshToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new Exception400("Cookie", "쿠키에 값이 없습니다");
        }
        Optional<Cookie> cookieOP = Arrays.stream(cookies).filter(cookie ->
                cookie.getName().equals("refreshToken")).findFirst();
        if (cookieOP.isEmpty()) {
            throw new Exception400("Cookie", "쿠키에 리프레시 토큰이 없습니다");
        }
        return cookieOP.get().getValue();
    }

//    @GetMapping("/s/user/{id}")
//    public ResponseEntity<?> detail(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) throws JsonProcessingException {
//        if(id.longValue() != myUserDetails.getMember().getId()){
//            throw new Exception403("권한이 없습니다");
//        }
//        UserResponse.DetailOutDTO detailOutDTO = userService.회원상세보기(id);
//        //System.out.println(new ObjectMapper().writeValueAsString(detailOutDTO));
//        ResponseDTO<?> responseDTO = new ResponseDTO<>(detailOutDTO);
//        return ResponseEntity.ok(responseDTO);
//    }
}
