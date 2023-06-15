package kr.co.moneybridge.controller;
import com.nimbusds.jose.util.Pair;
import io.swagger.annotations.ApiOperation;
import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.annotation.SwaggerResponses;
import kr.co.moneybridge.core.auth.jwt.MyJwtProvider;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
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


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @ApiOperation(value = "개인 정보 수정")
    @SwaggerResponses.DefaultApiResponses
    @MyLog
    @PatchMapping("/auth/myInfo")
    public ResponseEntity<?> updateMyInfo(@RequestBody @Valid UserRequest.UpdateMyInfoInDTO updateMyInfoInDTO,
                                          Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.updateMyInfo(updateMyInfoInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    @ApiOperation(value = "개인 정보 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @MyLog
    @GetMapping("/auth/myInfo")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.MyInfoOutDTO myInfoOutDTO = userService.getMyInfo(myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(myInfoOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @ApiOperation(value = "개인정보 수정시 비밀번호 확인")
    @SwaggerResponses.DefaultApiResponses
    @MyLog
    @PostMapping("/auth/password")
    public ResponseEntity<?> checkPassword(@RequestBody @Valid UserRequest.CheckPasswordInDTO checkPasswordInDTO,
                                           Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.checkPassword(checkPasswordInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    @ApiOperation(value = "비밀번호 재설정")
    @SwaggerResponses.DefaultApiResponses
    @MyLog
    @PatchMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid UserRequest.RePasswordInDTO rePasswordInDTO, Errors errors) {
        userService.updatePassword(rePasswordInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    @ApiOperation(value = "이메일 찾기")
    @SwaggerResponses.DefaultApiResponses
    @MyLog
    @PostMapping("/email")
    public ResponseEntity<?> findEmail(@RequestBody @Valid UserRequest.EmailFindInDTO emailFindInDTO, Errors errors) {
        List<UserResponse.EmailFindOutDTO> emailFindOutDTOs = userService.findEmail(emailFindInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(emailFindOutDTOs);
        return ResponseEntity.ok().body(responseDTO);
    }

    // 비밀번호 찾기 + 이메일 인증
    @ApiOperation(value = "비밀번호 찾기시 이메일 인증")
    @SwaggerResponses.DefaultApiResponses
    @MyLog
    @PostMapping("/password")
    public ResponseEntity<?> password(@RequestBody @Valid UserRequest.PasswordInDTO passwordInDTO, Errors errors) throws Exception {
        UserResponse.PasswordOutDTO passwordOutDTO = userService.password(passwordInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(passwordOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @ApiOperation(value = "이메일 인증")
    @SwaggerResponses.DefaultApiResponses
    @MyLog
    @PostMapping("/email/authentication")
    public ResponseEntity<?> email(@RequestBody @Valid UserRequest.EmailInDTO emailInDTO, Errors errors) throws Exception {
        UserResponse.EmailOutDTO emailOutDTO = userService.email(emailInDTO.getEmail());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(emailOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

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
}
