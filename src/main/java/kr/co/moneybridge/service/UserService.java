package kr.co.moneybridge.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimbusds.jose.util.Pair;
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
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserAgreementRepository;
import kr.co.moneybridge.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    private final JavaMailSender javaMailSender;

    @MyLog
    @Transactional
    public List<UserResponse.EmailFindOutDTO> emailFind(UserRequest.EmailFindInDTO emailFindInDTO) throws Exception {
        List<Member> members = myMemberUtil.findByNameAndPhoneNumber(emailFindInDTO.getName(),
            emailFindInDTO.getPhoneNumber(), emailFindInDTO.getRole());
        List<UserResponse.EmailFindOutDTO> emailFindOutDTOs = new ArrayList<>();
        members.stream().forEach(member -> emailFindOutDTOs.add(new UserResponse.EmailFindOutDTO(member)));
        return emailFindOutDTOs;
    }

    @MyLog
    @Transactional
    public UserResponse.PasswordOutDTO password(UserRequest.PasswordInDTO passwordInDTO) throws Exception {
        Member member = myMemberUtil.findByEmail(passwordInDTO.getEmail(), passwordInDTO.getRole());
        if(!member.getName().equals(passwordInDTO.getName())){
            throw new Exception404("이름이 틀렸습니다");
        }
        String code = sendEmail(passwordInDTO.getEmail());
        UserResponse.PasswordOutDTO passwordOutDTO = new UserResponse.PasswordOutDTO(member, code);
        return passwordOutDTO;
    }

    @MyLog
    public UserResponse.EmailOutDTO email(String email) throws Exception {
        String code = sendEmail(email);
        UserResponse.EmailOutDTO emailOutDTO = new UserResponse.EmailOutDTO(code);
        return emailOutDTO;
    }


    private String sendEmail(String email) throws Exception{
        String code = createCode();
        MimeMessage message = createMessage(email, code);
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new Exception500("인증 이메일 전송 실패");
        }
        return code;
    }

    private MimeMessage createMessage(String email, String code) throws Exception {
        System.out.println("인증 번호 : " + code);
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, email); // 메일 받을 사용자
        message.setSubject("[Money Bridge] 이메일 인증코드 입니다"); // 이메일 제목

        String msg = "";
        // msg += "<img src=../resources/static/image/emailheader.jpg />"; // header image
        msg += "<div style='margin:20px;'>";
        msg += "<h1> 안녕하세요. </h1>";
        msg += "<br>";
        msg += "<h1> Money Bridge 입니다</h1>";
        msg += "<br>";
        msg += "<p>아래 인증코드를 Money Bridge 페이지의 입력 칸에 입력해주세요</p>";
        msg += "<br>";
        msg += "<br>";
        msg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msg += "<div style='font-size:130%'>";
        msg += "CODE : <strong>";
        msg += code + "</strong><div><br/> ";
        msg += "</div>";
        // msg += "<img src=../resources/static/image/emailfooter.jpg />"; // footer image
        message.setText(msg, "utf-8", "html"); // 메일 내용, charset타입, subtype
        message.setFrom(new InternetAddress("moneybridge@naver.com", "Money-Bridge")); // 보내는 사람의 이메일 주소, 보내는 사람 이름

        return message;
    }

    public String createCode() {
        Random random = new Random();
        return random.ints('0', 'z' + 1)
                .filter(i -> (i <= '9' || i >= 'A') && (i <= 'Z' || i > 'z'))
                .limit(8) // 인증코드 8자리
                .collect(StringBuffer::new, StringBuffer::appendCodePoint, StringBuffer::append)
                .toString();
    }

    @MyLog
    @Transactional
    public void withdraw(UserRequest.WithdrawInDTO withdrawInDTO, MyUserDetails myUserDetails) {
        if(!passwordEncoder.matches(withdrawInDTO.getPassword(), myUserDetails.getPassword())){
            throw new Exception400("password", "비밀번호가 틀렸습니다");
        }
        myMemberUtil.deleteById(myUserDetails.getMember().getId(), myUserDetails.getMember().getRole());
    }

    @MyLog
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
    public UserResponse.LoginOutDTO login(UserRequest.LoginInDTO loginInDTO) {
        Member memberPS = myMemberUtil.findByEmail(loginInDTO.getEmail(), loginInDTO.getRole());
        return new UserResponse.LoginOutDTO(memberPS);
    }

    @MyLog
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

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new Exception404("해당 유저를 찾을 수 없습니다.")
        );
    }
}
