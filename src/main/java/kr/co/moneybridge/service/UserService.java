package kr.co.moneybridge.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimbusds.jose.util.Pair;
import kr.co.moneybridge.core.annotation.Log;
import kr.co.moneybridge.core.auth.jwt.JwtProvider;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.core.exception.Exception401;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.core.util.MemberUtil;
import kr.co.moneybridge.core.util.MsgUtil;
import kr.co.moneybridge.core.util.RedisUtil;
import kr.co.moneybridge.core.util.StibeeUtil;
import kr.co.moneybridge.dto.user.UserRequest;
import kr.co.moneybridge.dto.user.UserResponse;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.board.BoardBookmarkRepository;
import kr.co.moneybridge.model.board.BoardRepository;
import kr.co.moneybridge.model.board.BookmarkerRole;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBRepository;
import kr.co.moneybridge.model.pb.PBStatus;
import kr.co.moneybridge.model.reservation.ReservationProcess;
import kr.co.moneybridge.model.reservation.ReservationRepository;
import kr.co.moneybridge.model.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private String defaultProfile = "https://moneybridge.s3.ap-northeast-2.amazonaws.com/default/profile.svg";
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserAgreementRepository userAgreementRepository;
    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
    private final MemberUtil memberUtil;
    private final JavaMailSender javaMailSender;
    private final ReservationRepository reservationRepository;
    private final BoardRepository boardRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final UserBookmarkRepository userBookmarkRepository;
    private final PBRepository pbRepository;
    private final MsgUtil msgUtil;
    private final StibeeUtil stibeeUtil;

    @Log
    @Transactional
    public void testPropensity(UserRequest.TestPropensityInDTO testPropensityInDTO, Long id) {
        User userPS = userRepository.findById(id).orElseThrow(
                () -> new Exception404("해당 유저를 찾을 수 없습니다"));
        userPS.testPropensity(testPropensityInDTO.getScore());
    }

    @Log
    public UserResponse.AccountOutDTO getAccount(MyUserDetails myUserDetails) {
        return new UserResponse.AccountOutDTO(myUserDetails.getMember());
    }

    @Log
    public UserResponse.MyPageOutDTO getMyPage(MyUserDetails myUserDetails) {
        User user = (User) myUserDetails.getMember();
        UserResponse.StepDTO step = new UserResponse.StepDTO(user);
        UserResponse.ReservationCountDTO reservationCount = new UserResponse.ReservationCountDTO(
                reservationRepository.countByUserIdAndProcess(user.getId(), ReservationProcess.APPLY),
                reservationRepository.countByUserIdAndProcess(user.getId(), ReservationProcess.CONFIRM),
                reservationRepository.countByUserIdAndProcess(user.getId(), ReservationProcess.COMPLETE));

        Pageable topTwo = PageRequest.of(0, 2);
        Page<UserResponse.BookmarkDTO> boardBookmarkTwo = boardRepository.findTwoByBookmarker(BookmarkerRole.USER, user.getId(), topTwo);
        Page<UserResponse.BookmarkDTO> pbBookmarkTwo = pbRepository.findTwoByBookmarker(user.getId(), topTwo);

        UserResponse.BookmarkListDTO boardBookmark = new UserResponse.BookmarkListDTO(
                boardBookmarkTwo.getContent(), boardBookmarkRepository.countByBookmarker(BookmarkerRole.USER, user.getId()));
        UserResponse.BookmarkListDTO pbBookmark = new UserResponse.BookmarkListDTO(
                pbBookmarkTwo.getContent(), userBookmarkRepository.countByUserId(user.getId()));
        return new UserResponse.MyPageOutDTO(user, step, reservationCount, boardBookmark, pbBookmark);
    }

    @Log
    @Transactional
    public void updateMyInfo(UserRequest.UpdateMyInfoInDTO updateMyInfoInDTO, MyUserDetails myUserDetails) {
        Long id = myUserDetails.getMember().getId();
        Role role = myUserDetails.getMember().getRole();
        Member memberPS = memberUtil.findById(id, role);

        if (updateMyInfoInDTO.getName() != null && !updateMyInfoInDTO.getName().isEmpty()) { // isEmpty()는 null이 아닐 때만 확인 가능
            memberPS.updateName(updateMyInfoInDTO.getName());
        }
        if (updateMyInfoInDTO.getPhoneNumber() != null && !updateMyInfoInDTO.getPhoneNumber().isEmpty()) {
            memberPS.updatePhoneNumber(updateMyInfoInDTO.getPhoneNumber());
        }
    }

    @Log
    public UserResponse.MyInfoOutDTO getMyInfo(MyUserDetails myUserDetails) {
        return new UserResponse.MyInfoOutDTO(myUserDetails.getMember());
    }

    @Log
    public void checkPassword(UserRequest.CheckPasswordInDTO checkPasswordInDTO, MyUserDetails myUserDetails) {
        if (!passwordEncoder.matches(checkPasswordInDTO.getPassword(), myUserDetails.getPassword())) {
            throw new Exception401("비밀번호가 틀렸습니다");
        }
    }

    @Log
    @Transactional
    public void updatePassword(UserRequest.RePasswordInDTO rePasswordInDTO) {
        Member memberPS = memberUtil.findById(rePasswordInDTO.getId(), rePasswordInDTO.getRole());
        String encPassword = passwordEncoder.encode(rePasswordInDTO.getPassword()); // 60Byte
        memberPS.updatePassword(encPassword);
    }

    @Log
    public List<UserResponse.EmailFindOutDTO> findEmail(UserRequest.EmailFindInDTO emailFindInDTO) {
        List<Member> membersPS = memberUtil.findByPhoneNumberWithoutException(
                emailFindInDTO.getPhoneNumber(),
                emailFindInDTO.getRole()
        );
        if (membersPS == null) {
            return Arrays.asList(new UserResponse.EmailFindOutDTO());
        }
        List<UserResponse.EmailFindOutDTO> emailFindOutDTOs = new ArrayList<>();
        membersPS.stream().forEach(memberPS -> emailFindOutDTOs.add(new UserResponse.EmailFindOutDTO(memberPS)));
        return emailFindOutDTOs;
    }

    @Log
    public UserResponse.PasswordOutDTO password(UserRequest.PasswordInDTO passwordInDTO) throws Exception {
        Member memberPS = memberUtil.findByEmailWithoutException(passwordInDTO.getEmail(), passwordInDTO.getRole());
        if (memberPS == null) {
            return new UserResponse.PasswordOutDTO();
        }
        // 스티비 임시 주소록 구독
        stibeeUtil.subscribeTemp(passwordInDTO.getEmail());
        // 인증 코드 안내 임시 이메일 발송
        String code = sendTempEmailByStibee(passwordInDTO.getEmail());
        UserResponse.PasswordOutDTO passwordOutDTO = new UserResponse.PasswordOutDTO(memberPS, code);
        return passwordOutDTO;
    }

    @Log
    public UserResponse.EmailOutDTO email(UserRequest.EmailInDTO emailInDTO) {
        Member memberPS = memberUtil.findByEmailWithoutException(emailInDTO.getEmail(), emailInDTO.getRole());
        if (memberPS != null) {
            if (emailInDTO.getRole().equals(Role.USER)) {
                throw new Exception400("email", "이미 투자자로 회원가입된 이메일입니다");
            }
            PB pb = (PB) memberPS;
            if (pb.getStatus().equals(PBStatus.PENDING)) {
                throw new Exception400("email", "회원가입 후 승인을 기다리고 있는 PB 계정입니다");
            }
            if (pb.getStatus().equals(PBStatus.ACTIVE)) {
                throw new Exception400("email", "이미 PB로 회원가입된 이메일입니다");
            }
        }
        // 스티비 임시 주소록 구독
        stibeeUtil.subscribeTemp(emailInDTO.getEmail());
        // 인증 코드 안내 임시 이메일 발송
        String code = sendTempEmailByStibee(emailInDTO.getEmail());
        UserResponse.EmailOutDTO emailOutDTO = new UserResponse.EmailOutDTO(code);
        return emailOutDTO;
    }

    @Log
    public UserResponse.PhoneNumberOutDTO checkPhoneNumber(String type, String phoneNumber) {
        if (type.equals("user")) {
            int count = userRepository.countByPhoneNumber(phoneNumber);
            if (count >= 1) {
                return new UserResponse.PhoneNumberOutDTO(true);
            }
            return new UserResponse.PhoneNumberOutDTO(false);
        }

        int count = pbRepository.countByPhoneNumber(phoneNumber);
        if (count >= 1) {
            return new UserResponse.PhoneNumberOutDTO(true);
        }
        return new UserResponse.PhoneNumberOutDTO(false);
    }

    // 인증 코드 안내 이메일 발송(JavaMailSender)
    private String sendEmail(String email) {
        String code = createCode();
        MimeMessage message = msgUtil.createMessage(email, msgUtil.getSubjectAuthenticate(),
                msgUtil.getMsgAuthenticate(code));
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new Exception500("인증 이메일 전송 실패 " + e.getMessage());
        }
        return code;
    }

    // 인증 코드 안내 이메일 발송(Stibee)
    private String sendEmailByStibee(String email) {
        String code = createCode();
        try {
            stibeeUtil.sendAuthenticationEmail(email, code);
        } catch (Exception e) {
            throw new Exception500("인증 코드 안내 이메일 전송 실패 : " + e.getMessage());
        }
        return code;
    }

    // 인증 코드 안내 이메일 발송(Stibee, 회원가입시)
    private String sendTempEmailByStibee(String email) {
        String code = createCode();
        try {
            stibeeUtil.sendAuthenticationTempEmail(email, code);
        } catch (Exception e) {
            throw new Exception500("인증 코드 안내 이메일 전송 실패 : " + e.getMessage());
        }
        return code;
    }

    public String createCode() {
        Random random = new Random();
        return random.ints('0', 'z' + 1)
                .filter(i -> (i <= '9' || i >= 'A') && (i <= 'Z' || i > 'z'))
                .limit(8) // 인증코드 8자리
                .collect(StringBuffer::new, StringBuffer::appendCodePoint, StringBuffer::append)
                .toString();
    }

    @Log
    @Transactional
    public void withdraw(UserRequest.WithdrawInDTO withdrawInDTO, MyUserDetails myUserDetails) {
        if (!passwordEncoder.matches(withdrawInDTO.getPassword(), myUserDetails.getPassword())) {
            throw new Exception401("비밀번호가 틀렸습니다");
        }
        memberUtil.deleteById(myUserDetails.getMember().getId(), myUserDetails.getMember().getRole());
    }

    @Log
    @Transactional
    public UserResponse.JoinOutDTO joinUser(UserRequest.JoinInDTO joinInDTO) {

        Optional<User> userOP = userRepository.findByEmail(joinInDTO.getEmail());
        if (userOP.isPresent()) {
            throw new Exception400("email", "이미 투자자로 회원가입된 이메일입니다");
        }
        String encPassword = passwordEncoder.encode(joinInDTO.getPassword()); // 60Byte
        joinInDTO.setPassword(encPassword);

        User userPS = null;
        try {
            userPS = userRepository.save(joinInDTO.toEntity(defaultProfile));
            List<UserRequest.AgreementDTO> agreements = joinInDTO.getAgreements();
            if (agreements != null) {
                User finalUserPS = userPS;
                agreements.stream().forEach(agreement ->
                        userAgreementRepository.save(agreement.toEntity(finalUserPS)));
            }
        } catch (Exception e) {
            throw new Exception500("회원가입 실패 : " + e.getMessage());
        }

        // 스티비 주소록 구독
        stibeeUtil.subscribe(Role.USER.name(), joinInDTO.getEmail(), joinInDTO.getName());
        return new UserResponse.JoinOutDTO(userPS);
    }

    @Log
    public Pair<String, String> issue(Role role, String email, String password) {
        try {
            // 인증
            String username = role + "-" + email;
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(username, password); // username과 password는 사용자가 제공한 인증 정보
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken); // 인증 매니저(authenticationManager)를 통해 인증되고, Authentication 객체를 반환
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal(); //  인증된 사용자의 주체(Principal)를 반환 - 주체는 보통 UserDetails 인터페이스를 구현한 사용자 정보 객체

            // 로그인 성공하면 액세스 토큰, 리프레시 토큰 발급.
            String accessToken = jwtProvider.createAccess(myUserDetails.getMember());
            String refreshToken = jwtProvider.createRefresh(myUserDetails.getMember());
            return Pair.of(accessToken, refreshToken);
        } catch (Exception e) {
            throw new Exception400("email or password", "아이디가 존재하지 않거나 비밀번호가 틀렸습니다.");
        }
    }

    @Log
    public UserResponse.BackOfficeLoginOutDTO backofficeLogin(UserRequest.BackOfficeLoginInDTO loginInDTO) {
        User userPS = userRepository.findByEmail(loginInDTO.getEmail()).orElseThrow(
                () -> new Exception404("해당하는 계정이 없습니다")
        );
        if (!userPS.getRole().equals(Role.ADMIN)) {
            throw new Exception400("email", "관리자 계정이 아닙니다");
        }
        String code = sendEmailByStibee(loginInDTO.getEmail());
        return new UserResponse.BackOfficeLoginOutDTO(userPS, code);
    }

    @Log
    public UserResponse.LoginOutDTO login(UserRequest.LoginInDTO loginInDTO) {
        Member memberPS = memberUtil.findByEmail(loginInDTO.getEmail(), loginInDTO.getRole());
        Boolean isAdmin = memberPS.getRole().equals(Role.ADMIN) ? true : false;
        return new UserResponse.LoginOutDTO(memberPS, isAdmin);
    }

    @Log
    public Pair<String, String> reissue(HttpServletRequest request, String refreshToken) {
        // access token에서 나온 사용자 정보로 redis에서 refresh token을 조회해서
        // 요청받은 refresh token과 같아야 재발급

        // 1) access token 꺼내기
        String accessToken = request.getHeader(JwtProvider.HEADER_ACCESS);
        String accessJwt = accessToken.replace(JwtProvider.TOKEN_PREFIX, "");

        // 2) access token에서 나온 사용자 정보
        DecodedJWT decodedJWT = JWT.decode(accessJwt);
        Long id = decodedJWT.getClaim("id").asLong();
        String role = decodedJWT.getClaim("role").asString().toUpperCase();
        // 3) redis에서 refresh token을 조회할 key
        String key = id + role;

        // 4) 해당 key로 redis에서 refresh token을 조회
        String RefreshTokenValid = redisUtil.get(key);
        // 5) redis에 key에 해당하는 리프레시 토큰이 없을 때
        if (RefreshTokenValid == null) {
            log.error("액세스 토큰 정보로 만든 키를 가진 리프레시 토큰이 레디스에 없음");
            throw new Exception500("토큰을 재발급할 수 없습니다. 다시 로그인 해주세요");
        }
        // 6) Redis에 저장된 refresh token이 사용자가 요청한 refresh token과 같아야 함.
        if (!refreshToken.equals(RefreshTokenValid)) {
            log.error("레디스에 저장된 리프레시 토큰이 사용자가 요청한 리프레시 토큰이 다름");
            throw new Exception500("사용할 수 없는 리프레시 토큰입니다. 다시 로그인 해주세요");
        }

        // 7) Redis에 저장된 기존 refresh token 삭제 후 새로 refresh token과 access token을 생성해서 응답
        redisUtil.delete(key);
        log.info("레디스에서 리프레시 토큰을 삭제했습니다. key: ", key);

        // 8) 액세스 토큰, 리프레시 토큰 발급.
        try {
            Member memberPS = memberUtil.findById(id, Role.valueOf(role));
            String newAccessToken = jwtProvider.createAccess(memberPS);
            String newRefreshToken = jwtProvider.createRefresh(memberPS);
            return Pair.of(newAccessToken, newRefreshToken);
        } catch (Exception e) {
            throw new Exception500("토큰 재발급 실패: " + e.getMessage());
        }
    }

    @Log
    public void logout(HttpServletRequest request, String refreshToken) {
        // 로그아웃하면 redis에 refresh token 정보는 지워서 새로 재발급 못받게 함
        // access token은 redis에 블랙리스트로 저장해둬서 해당 aceess token을 탈취해서 로그인 하는 행위 막음

        // 1) refresh token 디코딩
        DecodedJWT decodedJWT = null;
        try {
            decodedJWT = JwtProvider.verifyRefresh(refreshToken);
        } catch (SignatureVerificationException sve) {
            log.error("리프레시 토큰 검증 실패");
        }
        // 2) 디코딩한 리프레시 토큰으로 사용자 정보 얻음
        Long id = decodedJWT.getClaim("id").asLong();
        String role = decodedJWT.getClaim("role").asString().toUpperCase();
        // 3) 사용자 정보를 토대로  redis에서 refresh token을 조회할 key 값 생성
        String key = id + role;

        // 4) Redis에서 해당 유저의 refresh token 삭제
        redisUtil.delete(key);
        log.info("레디스에서 리프레시 토큰 삭제");

        // 5) 해당 access token을 Redis의 블랙리스트로 추가
        String accessToken = request.getHeader(JwtProvider.HEADER_ACCESS);
        String accessJwt = accessToken.replace(JwtProvider.TOKEN_PREFIX, "");
        try {
            decodedJWT = JwtProvider.verifyAccess(accessJwt);
        } catch (SignatureVerificationException sve) {
            log.error("액세스 토큰 검증 실패");
        }
        // 6) 추가할 때 남은 만료시간 계산해서 넣어주기 - 그래야 해당 시간 지나면 자동으로 사라짐
        Long remainingTimeMillis = decodedJWT.getExpiresAt().getTime() - System.currentTimeMillis();
        redisUtil.setBlackList(accessJwt, "access_token_blacklist", remainingTimeMillis);
        log.info("로그아웃한 액세스 토큰 블랙리스트로 등록");
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new Exception404("해당 유저를 찾을 수 없습니다.")
        );
    }

    //PB 북마크하기
    @Transactional
    public void bookmarkPB(MyUserDetails myUserDetails, Long pbId) {

        User user = userRepository.findById(myUserDetails.getMember().getId()).orElseThrow(() -> new Exception404("해당 유저 찾을 수 없습니다."));
        PB pb = pbRepository.findById(pbId).orElseThrow(() -> new Exception404("해당 PB 찾을 수 없습니다."));

        try {
            if (userBookmarkRepository.findByUserIdWithPbId(user.getId(), pbId).isEmpty()) {
                userBookmarkRepository.save(UserBookmark.builder()
                        .user(user)
                        .pb(pb)
                        .build());
            }
        } catch (Exception e) {
            throw new Exception500("북마크 실패 : " + e);
        }
    }

    //PB 북마크 취소하기
    @Transactional
    public void deletePBBookmark(MyUserDetails myUserDetails, Long pbId) {

        User user = userRepository.findById(myUserDetails.getMember().getId()).orElseThrow(() -> new Exception404("해당 유저 찾을 수 없습니다."));
        PB pb = pbRepository.findById(pbId).orElseThrow(() -> new Exception404("해당 PB 찾을 수 없습니다."));
        Optional<UserBookmark> userBookmarkOP = userBookmarkRepository.findByUserIdWithPbId(user.getId(), pbId);

        try {
            if (userBookmarkOP.isPresent()) {
                UserBookmark userBookmark = userBookmarkOP.get();
                userBookmarkRepository.delete(userBookmark);
            }
        } catch (Exception e) {
            throw new Exception500("북마크 취소 실패 : " + e);
        }
    }
}
