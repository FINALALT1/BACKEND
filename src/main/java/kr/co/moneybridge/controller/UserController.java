package kr.co.moneybridge.controller;
import com.nimbusds.jose.util.Pair;
import io.swagger.annotations.*;
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
import org.springframework.http.HttpStatus;
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

    @MyLog
    @ApiOperation(value = "개인 정보 수정", notes = "i 소문자")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/auth/myinfo")
    public ResponseDTO updateMyInfo(@RequestBody @Valid UserRequest.UpdateMyInfoInDTO updateMyInfoInDTO,
                                    Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.updateMyInfo(updateMyInfoInDTO, myUserDetails);
        return new ResponseDTO<>();
    }

    @MyLog
    @ApiOperation(value = "개인 정보 가져오기", notes = "i 소문자")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/auth/myinfo")
    public ResponseDTO<UserResponse.MyInfoOutDTO> getMyInfo(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.MyInfoOutDTO myInfoOutDTO = userService.getMyInfo(myUserDetails);
        return new ResponseDTO<>(myInfoOutDTO);
    }

    @MyLog
    @ApiOperation(value = "개인정보 수정시 비밀번호 확인")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/auth/password")
    public ResponseDTO checkPassword(@RequestBody @Valid UserRequest.CheckPasswordInDTO checkPasswordInDTO,
                                           Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.checkPassword(checkPasswordInDTO, myUserDetails);
        return new ResponseDTO<>();
    }

    @MyLog
    @ApiOperation(value = "비밀번호 재설정")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/password")
    public ResponseDTO updatePassword(@RequestBody @Valid UserRequest.RePasswordInDTO rePasswordInDTO, Errors errors) {
        userService.updatePassword(rePasswordInDTO);
        return new ResponseDTO<>();
    }

    @MyLog
    @ApiOperation(value = "이메일 찾기")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/email")
    public ResponseDTO<List<UserResponse.EmailFindOutDTO>> findEmail(@RequestBody @Valid UserRequest.EmailFindInDTO emailFindInDTO, Errors errors) {
        List<UserResponse.EmailFindOutDTO> emailFindOutDTOs = userService.findEmail(emailFindInDTO);
        return new ResponseDTO<>(emailFindOutDTOs);
    }

    // 비밀번호 찾기 + 이메일 인증
    @MyLog
    @ApiOperation(value = "비밀번호 찾기시 이메일 인증")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/password")
    public ResponseDTO<UserResponse.PasswordOutDTO> password(@RequestBody @Valid UserRequest.PasswordInDTO passwordInDTO, Errors errors) throws Exception {
        UserResponse.PasswordOutDTO passwordOutDTO = userService.password(passwordInDTO);
        return new ResponseDTO<>(passwordOutDTO);
    }

    @MyLog
    @ApiOperation(value = "이메일 인증")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/email/authentication")
    public ResponseDTO<UserResponse.EmailOutDTO> email(@RequestBody @Valid UserRequest.EmailInDTO emailInDTO, Errors errors) throws Exception {
        UserResponse.EmailOutDTO emailOutDTO = userService.email(emailInDTO.getEmail());
        return new ResponseDTO<>(emailOutDTO);
    }

    @MyLog
    @ApiOperation(value = "탈퇴", notes = "연관데이터 전부 즉시 삭제")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/auth/account")
    public ResponseDTO withdraw(@RequestBody @Valid UserRequest.WithdrawInDTO withdrawInDTO, Errors errors,
                                      @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.withdraw(withdrawInDTO, myUserDetails);
        return new ResponseDTO<>();
    }

    @MyLog
    @ApiOperation(value = "투자자 회원가입", notes = "<b>성공시, 응답데이터</b>\n<br>" +
            "Authorization: Bearer <JWT 토큰>&nbsp;<font color=\"#C0C0C0\">// 액세스 토큰</font>\n" +
            "Set-Cookie: refreshToken=<JWT 토큰>; HttpOnly; Path=/&nbsp;<font color=\"#C0C0C0\">// 리프레시 토큰</font>\n<br>" +
            "{\n" +
            "&nbsp;&nbsp;\"status\": 200,\n" +
            "&nbsp;&nbsp;\"msg\": \"ok\",\n" +
            "&nbsp;&nbsp;\"data\": {\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\"id\": 1,&nbsp;<font color=\"#C0C0C0\">// 해당 투자자의 id</font>\n" +
            "&nbsp;&nbsp;}\n" +
            "}")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
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
    @MyLog
    @ApiOperation(value = "로그인", notes = "<b>성공시, 응답 데이터</b>\n<br>" +
            "Authorization: Bearer <JWT 토큰>&nbsp;<font color=\"#C0C0C0\">// 액세스 토큰</font>\n" +
            "Set-Cookie: refreshToken=<JWT 토큰>; HttpOnly; Path=/&nbsp;<font color=\"#C0C0C0\">// 리프레시 토큰</font>\n<br>" +
            "{\n" +
            "&nbsp;&nbsp;\"status\": 200,\n" +
            "&nbsp;&nbsp;\"msg\": \"ok\",\n" +
            "&nbsp;&nbsp;\"data\": {\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\"id\": 1,&nbsp;<font color=\"#C0C0C0\">// 해당 투자자의 id</font>\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\"code\": \"YFOEC1AC\"&nbsp;<font color=\"#C0C0C0\">// 관리자가 아닐때는 null</font>\n" +
            "&nbsp;&nbsp;}\n" +
            "}")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
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
    @MyLog
    @ApiOperation(value = "토큰 재발급", notes = "<b>401 에러 + data가 \"Access token expired\"일 때, 이 API 호출</b>\n<br>" +
            "인증이 필요한 요청에서 응답 메세지가 다음과 같을 때 이 요청을 보낸다.\n"+
            "{\n" +
            "&nbsp;&nbsp;\"status\": 401,\n" +
            "&nbsp;&nbsp;\"msg\": \"unAuthorized\",\n" +
            "&nbsp;&nbsp;\"data\": \"Access token expired\"\n" +
            "}\n<br><br>" +
            "<b>성공시, 응답 데이터</b>\n<br>" +
            "Authorization: Bearer <JWT 토큰>&nbsp;<font color=\"#C0C0C0\">// 액세스 토큰</font>\n" +
            "Set-Cookie: refreshToken=<JWT 토큰>; HttpOnly; Path=/&nbsp;<font color=\"#C0C0C0\">// 리프레시 토큰</font>\n<br>" +
            "{\n" +
            "&nbsp;&nbsp;\"status\": 200,\n" +
            "&nbsp;&nbsp;\"msg\": \"ok\",\n" +
            "&nbsp;&nbsp;\"data\": {}}\n" +
            "}")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        Pair<String, String> tokens = userService.reissue(request, getRefreshToken(request));
        response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight() + "; HttpOnly; Path=/");
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok()
                .header(MyJwtProvider.HEADER_ACCESS, tokens.getLeft())
                .body(responseDTO);
    }

    @MyLog
    @ApiOperation(value = "로그아웃")
    @SwaggerResponses.DefaultApiResponses
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/auth/logout")
    public ResponseDTO logout(HttpServletRequest request){
        userService.logout(request, getRefreshToken(request));
        return new ResponseDTO<>();
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
