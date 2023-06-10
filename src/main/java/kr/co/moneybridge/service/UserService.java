package kr.co.moneybridge.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimbusds.jose.util.Pair;
import kr.co.moneybridge.core.annotation.MyErrorLog;
import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.auth.jwt.MyJwtProvider;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.core.util.MyMemberUtil;
import kr.co.moneybridge.core.util.RedisUtil;
import kr.co.moneybridge.dto.user.UserRequest;
import kr.co.moneybridge.dto.user.UserResponse;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserAgreementRepository;
import kr.co.moneybridge.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserAgreementRepository userAgreementRepository;
    private final MyJwtProvider myJwtProvider;
    private final RedisUtil redisUtil;
    private final MyMemberUtil myMemberUtil;

    @MyLog
    @MyErrorLog
    @Transactional
    public void withdraw(UserRequest.WithdrawInDTO withdrawInDTO, MyUserDetails myUserDetails) {
        if(!passwordEncoder.matches(withdrawInDTO.getPassword(), myUserDetails.getPassword())){
            throw new Exception400("password", "비밀번호가 틀렸습니다");
        }
        myMemberUtil.deleteById(myUserDetails.getMember().getId(), myUserDetails.getMember().getRole());
    }

    @MyLog
    @MyErrorLog
    @Transactional
    public UserResponse.JoinOutDTO joinUser(UserRequest.JoinInDTO joinInDTO){
        Optional<User> userOP = userRepository.findByEmail(joinInDTO.getEmail());
        if(userOP.isPresent()){
            throw new Exception400("email", "이미 투자자로 회원가입된 이메일입니다");
        }
        String encPassword = passwordEncoder.encode(joinInDTO.getPassword()); // 60Byte
        joinInDTO.setPassword(encPassword);

        try {
            User userPS = userRepository.save(joinInDTO.toEntity());
            List<UserRequest.AgreementDTO> agreements = joinInDTO.getAgreements();
            if(agreements != null){
                agreements.stream().forEach(agreement ->
                        userAgreementRepository.save(agreement.toEntity(userPS)));
            }
            return new UserResponse.JoinOutDTO(userPS);
        }catch (Exception e){
            throw new Exception500("회원가입 실패 : " + e.getMessage());
        }
    }

    @MyLog
    @MyErrorLog
    public Pair<String, String> issue(Role role, String email, String password) {
        try {
            String username = role + "-" + email;
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(username, password); // username과 password는 사용자가 제공한 인증 정보
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken); // 인증 매니저(authenticationManager)를 통해 인증되고, Authentication 객체를 반환
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal(); //  인증된 사용자의 주체(Principal)를 반환 - 주체는 보통 UserDetails 인터페이스를 구현한 사용자 정보 객체

            //로그인 성공하면 액세스 토큰, 리프레시 토큰 발급.
            String accessToken = myJwtProvider.createAccess(myUserDetails.getMember());
            String refreshToken = myJwtProvider.createRefresh(myUserDetails.getMember());
            return Pair.of(accessToken, refreshToken);
        }catch (Exception e){
            throw new Exception500("토큰발급 실패" + e.getMessage());
        }
    }

    @MyLog
    @MyErrorLog
    public UserResponse.LoginOutDTO login(UserRequest.LoginInDTO loginInDTO) {
        Member memberPS = myMemberUtil.findByEmail(loginInDTO.getEmail(), loginInDTO.getRole());
        return new UserResponse.LoginOutDTO(memberPS);
    }

    @MyLog
    @MyErrorLog
    public Pair<String, String> reissue(HttpServletRequest request, String refreshToken) {
        // access token에서 나온 사용자 정보로 redis에서 refresh token을 조회
        String accessToken = request.getHeader(MyJwtProvider.HEADER_ACCESS);
        String accessJwt = accessToken.replace(MyJwtProvider.TOKEN_PREFIX, "");

        DecodedJWT decodedJWT = JWT.decode(accessJwt);
        Long id = decodedJWT.getClaim("id").asLong();
        String role = decodedJWT.getClaim("role").asString().toUpperCase();
        String key = id + role;

        String RefreshTokenValid = redisUtil.get(key);
        if (RefreshTokenValid == null) {
            log.error("액세스 토큰 정보로 만든 키를 가진 리프레시 토큰이 레디스에 없음");
            throw new Exception500("토큰을 재발급할 수 없습니다. 다시 로그인 해주세요");
        }
        // Redis에 저장된 refresh token이 사용자가 요청한 refresh token과 같아야 함.
        if(!refreshToken.equals(RefreshTokenValid)){
            log.error("레디스에 저장된 리프레시 토큰이 사용자가 요청한 리프레시 토큰이 다름");
            throw new Exception500("사용할 수 없는 리프레시 토큰입니다. 다시 로그인 해주세요");
        }

        // Redis에 저장된 기존 refresh token 삭제 후 새로 refresh token과 access token을 생성해서 응답
        redisUtil.delete(key);
        log.info("레디스에서 리프레시 토큰을 삭제했습니다. key: ", key);

        // 액세스 토큰, 리프레시 토큰 발급.
        try {
            Member memberPS = myMemberUtil.findById(id, Role.valueOf(role));
            String newAccessToken = myJwtProvider.createAccess(memberPS);
            String newRefreshToken = myJwtProvider.createRefresh(memberPS);
            return Pair.of(newAccessToken, newRefreshToken);
        } catch (Exception e){
            throw new Exception500("토큰 재발급 실패 " + e.getMessage());
        }
    }

    @MyLog
    @MyErrorLog
    public void logout(HttpServletRequest request, String refreshToken) {
        // 요청받은 refresh token에서 나온 사용자 정보로 redis에서 refresh token을 조회
        DecodedJWT decodedJWT = null;
        try {
            decodedJWT = MyJwtProvider.verifyRefresh(refreshToken);
        } catch (SignatureVerificationException sve) {
            log.error("리프레시 토큰 검증 실패");
        }
        Long id = decodedJWT.getClaim("id").asLong();
        String role = decodedJWT.getClaim("role").asString().toUpperCase();
        String key = id + role;
        if (redisUtil.get(key) == null) {
            log.error("요청받은 리프레시 토큰 레디스에 없음");
        }
        // Redis에서 해당 유저의 refresh token 삭제
        redisUtil.delete(key);
        log.info("레디스에서 리프레시 토큰 삭제");

        // 해당 access token을 Redis의 블랙리스트로 추가
        String accessToken = request.getHeader(MyJwtProvider.HEADER_ACCESS);
        String accessJwt = accessToken.replace(MyJwtProvider.TOKEN_PREFIX, "");
        try {
            decodedJWT = MyJwtProvider.verifyAccess(accessJwt);
        } catch (SignatureVerificationException sve) {
            log.error("액세스 토큰 검증 실패");
        }
        Long remainingTimeMillis = decodedJWT.getExpiresAt().getTime() - System.currentTimeMillis();
        redisUtil.setBlackList(accessJwt, "access_token_blacklist", remainingTimeMillis);
        log.info("로그아웃한 액세스 토큰 블랙리스트로 등록");
    }

    @MyLog
    public UserResponse.DetailOutDTO 회원상세보기(Long id) {
        User userPS = userRepository.findById(id).orElseThrow(
                ()-> new Exception404("해당 유저를 찾을 수 없습니다")

        );
        return new UserResponse.DetailOutDTO(userPS);
    }
}
