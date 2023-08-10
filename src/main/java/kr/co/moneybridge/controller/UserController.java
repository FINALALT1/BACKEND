package kr.co.moneybridge.controller;

import com.nimbusds.jose.util.Pair;
import io.swagger.annotations.ApiOperation;
import kr.co.moneybridge.core.annotation.Log;
import kr.co.moneybridge.core.annotation.SwaggerResponses;
import kr.co.moneybridge.core.auth.jwt.JwtProvider;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.user.UserRequest;
import kr.co.moneybridge.dto.user.UserResponse;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

    // 투자 성향 테스트
    @Log
    @SwaggerResponses.TestPropensity
    @PostMapping("/user/propensity")
    public ResponseDTO testPropensity(@RequestBody @Valid UserRequest.TestPropensityInDTO testPropensityInDTO,
                                      Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.testPropensity(testPropensityInDTO, myUserDetails.getMember().getId());
        return new ResponseDTO<>();
    }

    // 로그인 계정 정보 받아오기
    @Log
    @SwaggerResponses.GetAccount
    @GetMapping("/auth/account")
    public ResponseDTO<UserResponse.AccountOutDTO> getAccount(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.AccountOutDTO accountOutDTO = userService.getAccount(myUserDetails);
        return new ResponseDTO<>(accountOutDTO);
    }

    // 투자자 마이페이지 가져오기
    @Log
    @SwaggerResponses.GetMyPageUser
    @GetMapping("/user/mypage")
    public ResponseDTO<UserResponse.MyPageOutDTO> getMyPage(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.MyPageOutDTO myPageOutDTO = userService.getMyPage(myUserDetails);
        return new ResponseDTO<>(myPageOutDTO);
    }

    // 개인정보 수정 (투자자  + PB)
    @Log
    @SwaggerResponses.UpdateMyInfo
    @PatchMapping("/auth/myinfo")
    public ResponseDTO updateMyInfo(@RequestBody @Valid UserRequest.UpdateMyInfoInDTO updateMyInfoInDTO,
                                    Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.updateMyInfo(updateMyInfoInDTO, myUserDetails);
        return new ResponseDTO<>();
    }

    // 개인정보 가져오기 (투자자  + PB)
    @Log
    @SwaggerResponses.GetMyInfo
    @GetMapping("/auth/myinfo")
    public ResponseDTO<UserResponse.MyInfoOutDTO> getMyInfo(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.MyInfoOutDTO myInfoOutDTO = userService.getMyInfo(myUserDetails);
        return new ResponseDTO<>(myInfoOutDTO);
    }

    // 비밀번호 확인하기
    @Log
    @SwaggerResponses.CheckPassword
    @PostMapping("/auth/password")
    public ResponseDTO checkPassword(@RequestBody @Valid UserRequest.CheckPasswordInDTO checkPasswordInDTO,
                                     Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.checkPassword(checkPasswordInDTO, myUserDetails);
        return new ResponseDTO<>();
    }

    // 비밀번호 찾기
    @Log
    @SwaggerResponses.UpdatePassword
    @PatchMapping("/password")
    public ResponseDTO updatePassword(@RequestBody @Valid UserRequest.RePasswordInDTO rePasswordInDTO, Errors errors) {
        userService.updatePassword(rePasswordInDTO);
        return new ResponseDTO<>();
    }

    // 이메일 찾기
    @Log
    @SwaggerResponses.FindEmail
    @PostMapping("/email")
    public ResponseDTO<List<UserResponse.EmailFindOutDTO>> findEmail(@RequestBody @Valid UserRequest.EmailFindInDTO emailFindInDTO, Errors errors) {
        List<UserResponse.EmailFindOutDTO> emailFindOutDTOs = userService.findEmail(emailFindInDTO);
        return new ResponseDTO<>(emailFindOutDTOs);
    }

    // 비밀번호 찾기 + 이메일 인증
    @Log
    @SwaggerResponses.Password
    @PostMapping("/password")
    public ResponseDTO<UserResponse.PasswordOutDTO> password(@RequestBody @Valid UserRequest.PasswordInDTO passwordInDTO, Errors errors) throws Exception {
        UserResponse.PasswordOutDTO passwordOutDTO = userService.password(passwordInDTO);
        return new ResponseDTO<>(passwordOutDTO);
    }

    //회원가입시 이메일 인증
    @Log
    @SwaggerResponses.Email
    @PostMapping("/email/authentication")
    public ResponseDTO<UserResponse.EmailOutDTO> email(@RequestBody @Valid UserRequest.EmailInDTO emailInDTO, Errors errors) throws Exception {
        UserResponse.EmailOutDTO emailOutDTO = userService.email(emailInDTO);
        return new ResponseDTO<>(emailOutDTO);
    }

    // 휴대폰 번호 중복 체크
    @Log
    @SwaggerResponses.PhoneNumber
    @PostMapping("/phonenumber")
    public ResponseDTO<UserResponse.PhoneNumberOutDTO> checkPhoneNumber(
            String type,
            @RequestBody @Valid UserRequest.PhoneNumberInDTO phoneNumberInDTO,
            Errors errors) {
        if (!type.equals("user") && !type.equals("pb")) {
            throw new Exception400(type, "'user' 또는 'pb'만 입력 가능합니다.");
        }
        UserResponse.PhoneNumberOutDTO phoneNumberOutDTO = userService.checkPhoneNumber(type, phoneNumberInDTO.getPhoneNumber());

        return new ResponseDTO<>(phoneNumberOutDTO);
    }

    // 탈퇴하기
    @Log
    @SwaggerResponses.Withdraw
    @DeleteMapping("/auth/account")
    public ResponseDTO withdraw(@RequestBody @Valid UserRequest.WithdrawInDTO withdrawInDTO, Errors errors,
                                @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.withdraw(withdrawInDTO, myUserDetails);
        return new ResponseDTO<>();
    }

    // 투자자 회원가입
    @Log
    @SwaggerResponses.JoinUser
    @PostMapping("/join/user")
    public ResponseEntity<?> joinUser(@RequestBody @Valid UserRequest.JoinInDTO joinInDTO, Errors errors, HttpServletResponse response) {
        String rawPassword = joinInDTO.getPassword();
        UserResponse.JoinOutDTO joinOutDTO = userService.joinUser(joinInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(joinOutDTO);
        // 회원가입 완료시 자동로그인
        Pair<String, String> tokens = userService.issue(Role.USER, joinInDTO.getEmail(), rawPassword);
        response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight()
                + "; Max-Age=" + JwtProvider.EXP_REFRESH + "; SameSite=None; Secure; HttpOnly; Path=/");
        return ResponseEntity.ok()
                .header(JwtProvider.HEADER_ACCESS, tokens.getLeft())
                .header("refreshToken", tokens.getRight())
                .body(responseDTO);
    }

    // 백오피스 로그인
    @Log
    @SwaggerResponses.BackOfficeLogin
    @PostMapping("/backoffice/login")
    public ResponseEntity<?> backofficeLogin(@RequestBody @Valid UserRequest.BackOfficeLoginInDTO loginInDTO, Errors errors, HttpServletResponse response) {
        UserResponse.BackOfficeLoginOutDTO loginOutDTO = userService.backofficeLogin(loginInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(loginOutDTO);

        Pair<String, String> tokens = userService.issue(Role.USER, loginInDTO.getEmail(), loginInDTO.getPassword());
        // HttpOnly 플래그 설정 (XSS 방지 - 자바스크립트로 쿠키 접근 불가),
        response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight()
                + "; Max-Age=" + JwtProvider.EXP_REFRESH + "; SameSite=None; Secure; HttpOnly; Path=/");
        return ResponseEntity.ok()
                .header(JwtProvider.HEADER_ACCESS, tokens.getLeft())
                .body(responseDTO);
    }

    // 로그인 (성공시 access 토큰과 refresh 토큰 둘 다 발급)
    @Log
    @SwaggerResponses.Login
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginInDTO loginInDTO, Errors errors, HttpServletResponse response) {
        UserResponse.LoginOutDTO loginOutDTO = userService.login(loginInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(loginOutDTO);

        Pair<String, String> tokens = userService.issue(loginInDTO.getRole(), loginInDTO.getEmail(), loginInDTO.getPassword());
        // HttpOnly 플래그 설정 (XSS 방지 - 자바스크립트로 쿠키 접근 불가),
        if (loginInDTO.getRemember()) { // 브라우저 종료 후에도 로그인 상태 유지하려면
            response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight()
                    + "; Max-Age=" + JwtProvider.EXP_REFRESH + "; SameSite=None; Secure; HttpOnly; Path=/");
        } else {
            response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight()
                    + "; SameSite=None; Secure; HttpOnly; Path=/");
        }
        return ResponseEntity.ok()
                .header(JwtProvider.HEADER_ACCESS, tokens.getLeft())
                .body(responseDTO);
    }

    // AccessToken, RefreshToken 재발급
    @Log
    @SwaggerResponses.Reissue
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        Pair<String, String> tokens = userService.reissue(request, getRefreshToken(request));
        response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight()
                + "; Max-Age=" + JwtProvider.EXP_REFRESH + "; SameSite=None; Secure; HttpOnly; Path=/");
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok()
                .header(JwtProvider.HEADER_ACCESS, tokens.getLeft())
                .body(responseDTO);
    }

    // 로그아웃
    @Log
    @SwaggerResponses.Logout
    @PostMapping("/auth/logout")
    public ResponseDTO logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request, getRefreshToken(request));
        ResponseCookie cookie = ResponseCookie.from("refreshToken", getRefreshToken(request))
                .path("/")
                .sameSite("None")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
        return new ResponseDTO<>();
    }

    private String getRefreshToken(HttpServletRequest request) {
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

    @ApiOperation("PB 북마크하기")
    @SwaggerResponses.DefaultApiResponses
    @PostMapping("/user/bookmark/{pbId}")
    public ResponseDTO bookmarkPB(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable(value = "pbId") Long pbId) {

        userService.bookmarkPB(myUserDetails, pbId);

        return new ResponseDTO<>();
    }

    @ApiOperation("PB 북마크 취소하기")
    @SwaggerResponses.DefaultApiResponses
    @DeleteMapping("/user/bookmark/{pbId}")
    public ResponseDTO deletePBBookmark(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable(value = "pbId") Long pbId) {

        userService.deletePBBookmark(myUserDetails, pbId);

        return new ResponseDTO<>();
    }
}
