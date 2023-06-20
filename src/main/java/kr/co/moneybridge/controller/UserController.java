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

    // 투자 성향 변경
    @MyLog
    @SwaggerResponses.UpdatePropensity
    @PatchMapping("/user/propensity")
    public ResponseDTO updatePropensity(@RequestBody @Valid UserRequest.UpdatePropensityInDTO updatePropensityInDTO,
                                              Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.updatePropensity(updatePropensityInDTO, myUserDetails.getMember().getId());
        return  new ResponseDTO<>();
    }

    // 투자 성향 테스트
    @MyLog
    @SwaggerResponses.TestPropensity
    @PostMapping("/user/propensity")
    public ResponseDTO testPropensity(@RequestBody @Valid UserRequest.TestPropensityInDTO testPropensityInDTO,
                                            Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.testPropensity(testPropensityInDTO, myUserDetails.getMember().getId());
        return new ResponseDTO<>();
    }

    // 투자자 마이페이지 가져오기
    @MyLog
    @SwaggerResponses.GetMyPageUser
    @GetMapping("/user/mypage")
    public ResponseDTO<UserResponse.MyPageOutDTO> getMyPage(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.MyPageOutDTO myPageOutDTO = userService.getMyPage(myUserDetails);
        return new ResponseDTO<>(myPageOutDTO);
    }

    // 개인정보 수정 (투자자  + PB)
    @MyLog
    @SwaggerResponses.UpdateMyInfo
    @PatchMapping("/auth/myinfo")
    public ResponseDTO updateMyInfo(@RequestBody @Valid UserRequest.UpdateMyInfoInDTO updateMyInfoInDTO,
                                    Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.updateMyInfo(updateMyInfoInDTO, myUserDetails);
        return new ResponseDTO<>();
    }

    // 개인정보 가져오기 (투자자  + PB)
    @MyLog
    @SwaggerResponses.GetMyInfo
    @GetMapping("/auth/myinfo")
    public ResponseDTO<UserResponse.MyInfoOutDTO> getMyInfo(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.MyInfoOutDTO myInfoOutDTO = userService.getMyInfo(myUserDetails);
        return new ResponseDTO<>(myInfoOutDTO);
    }

    // 비밀번호 확인하기
    @MyLog
    @SwaggerResponses.CheckPassword
    @PostMapping("/auth/password")
    public ResponseDTO checkPassword(@RequestBody @Valid UserRequest.CheckPasswordInDTO checkPasswordInDTO,
                                           Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.checkPassword(checkPasswordInDTO, myUserDetails);
        return new ResponseDTO<>();
    }

    // 비밀번호 찾기
    @MyLog
    @SwaggerResponses.UpdatePassword
    @PatchMapping("/password")
    public ResponseDTO updatePassword(@RequestBody @Valid UserRequest.RePasswordInDTO rePasswordInDTO, Errors errors) {
        userService.updatePassword(rePasswordInDTO);
        return new ResponseDTO<>();
    }

    // 이메일 찾기
    @MyLog
    @SwaggerResponses.FindEmail
    @PostMapping("/email")
    public ResponseDTO<List<UserResponse.EmailFindOutDTO>> findEmail(@RequestBody @Valid UserRequest.EmailFindInDTO emailFindInDTO, Errors errors) {
        List<UserResponse.EmailFindOutDTO> emailFindOutDTOs = userService.findEmail(emailFindInDTO);
        return new ResponseDTO<>(emailFindOutDTOs);
    }

    // 비밀번호 찾기 + 이메일 인증
    @MyLog
    @SwaggerResponses.Password
    @PostMapping("/password")
    public ResponseDTO<UserResponse.PasswordOutDTO> password(@RequestBody @Valid UserRequest.PasswordInDTO passwordInDTO, Errors errors) throws Exception {
        UserResponse.PasswordOutDTO passwordOutDTO = userService.password(passwordInDTO);
        return new ResponseDTO<>(passwordOutDTO);
    }

    // 이메일 인증
    @MyLog
    @SwaggerResponses.Email
    @PostMapping("/email/authentication")
    public ResponseDTO<UserResponse.EmailOutDTO> email(@RequestBody @Valid UserRequest.EmailInDTO emailInDTO, Errors errors) throws Exception {
        UserResponse.EmailOutDTO emailOutDTO = userService.email(emailInDTO.getEmail());
        return new ResponseDTO<>(emailOutDTO);
    }

    // 탈퇴하기
    @MyLog
    @SwaggerResponses.Withdraw
    @DeleteMapping("/auth/account")
    public ResponseDTO withdraw(@RequestBody @Valid UserRequest.WithdrawInDTO withdrawInDTO, Errors errors,
                                      @AuthenticationPrincipal MyUserDetails myUserDetails) {
        userService.withdraw(withdrawInDTO, myUserDetails);
        return new ResponseDTO<>();
    }

    // 투자자 회원가입
    @MyLog
    @SwaggerResponses.JoinUser
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

    // 로그인 (성공시 access 토큰과 refresh 토큰 둘 다 발급)
    @MyLog
    @SwaggerResponses.Login
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

    // AccessToken, RefreshToken 재발급
    @MyLog
    @SwaggerResponses.Reissue
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        Pair<String, String> tokens = userService.reissue(request, getRefreshToken(request));
        response.setHeader("Set-Cookie", "refreshToken=" + tokens.getRight() + "; HttpOnly; Path=/");
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok()
                .header(MyJwtProvider.HEADER_ACCESS, tokens.getLeft())
                .body(responseDTO);
    }

    // 로그아웃
    @MyLog
    @SwaggerResponses.Logout
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

    @ApiOperation("PB 북마크하기")
    @SwaggerResponses.DefaultApiResponses
    @PostMapping("/user/bookmark/{pbId}")
    public ResponseDTO bookmarkPB(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable(value = "pbId") Long pbId) {

        userService.bookmarkPB(myUserDetails, pbId);

        return new ResponseDTO<>();
    }

    @DeleteMapping("/user/bookmark/{pbId}")
    public ResponseDTO deletePBBookmark(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable(value = "pbId") Long pbId) {

        userService.deletePBBookmark(myUserDetails, pbId);

        return new ResponseDTO<>();
    }
}
