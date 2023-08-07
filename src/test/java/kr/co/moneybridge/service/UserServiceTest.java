package kr.co.moneybridge.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimbusds.jose.util.Pair;
import kr.co.moneybridge.core.auth.jwt.JwtProvider;
import kr.co.moneybridge.core.auth.jwt.MyJwtProviderTest;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.core.exception.Exception401;
import kr.co.moneybridge.core.util.MemberUtil;
import kr.co.moneybridge.core.util.MsgUtil;
import kr.co.moneybridge.core.util.RedisUtil;
import kr.co.moneybridge.dto.user.UserRequest;
import kr.co.moneybridge.dto.user.UserResponse;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.board.BoardBookmarkRepository;
import kr.co.moneybridge.model.board.BoardRepository;
import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.model.pb.Company;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBRepository;
import kr.co.moneybridge.model.reservation.ReservationRepository;
import kr.co.moneybridge.model.user.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest extends MockDummyEntity {
    // 가짜 userService 객체를 만들고 Mock로 Load된 모든 객체를 userService에 주입
    @InjectMocks
    private UserService userService;

    // 진짜 객체를 만들어서 Mockito 환경에 Load
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserAgreementRepository userAgreementRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private PBRepository pbRepository;
    @Mock
    private BoardBookmarkRepository boardBookmarkRepository;
    @Mock
    private UserBookmarkRepository userBookmarkRepository;

    // 가짜 객체를 만들어서 Mockito 환경에 Load
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private RedisUtil redisUtil;
    @Mock
    private MemberUtil memberUtil;
    @Mock
    private MyUserDetails myUserDetails;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private MsgUtil msgUtil;

    // 진짜 객체를 만들어서 Mockito 환경에 Load
    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void testPropensity_test() {
        // given
        UserRequest.TestPropensityInDTO testPropensityInDTO = new UserRequest.TestPropensityInDTO();
        testPropensityInDTO.setScore(24);
        Long id = 1L;

        User user = newMockUserWithoutPropensity(1L, "lee");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // when
        userService.testPropensity(testPropensityInDTO, id);

        // then
        verify(userRepository, times(1)).findById(any());
        Assertions.assertThat(user.getPropensity()).isEqualTo(UserPropensity.AGGRESSIVE);
    }

    @Test
    public void getAccount_test() {
        // stub
        User user = newMockUser(1L, "lee");
        when(myUserDetails.getMember()).thenReturn(user);

        // when
        userService.getAccount(myUserDetails);

        // then
        verify(myUserDetails, times(1)).getMember();
    }

    @Test
    public void getMyPage() {
        when(myUserDetails.getMember()).thenReturn(newMockUser(1L, "lee"));
        when(boardRepository.findTwoByBookmarker(any(), any(), any())).thenReturn(new PageImpl<>(new ArrayList<>()));
        when(pbRepository.findTwoByBookmarker(any(), any())).thenReturn(new PageImpl<>(new ArrayList<>()));
        when(boardBookmarkRepository.countByBookmarker(any(), any())).thenReturn(0L);
        when(userBookmarkRepository.countByUserId(any())).thenReturn(0L);

        // when
        UserResponse.MyPageOutDTO myPageUserOutDTO = userService.getMyPage(myUserDetails);

        // then
        Assertions.assertThat(myPageUserOutDTO.getId()).isEqualTo(1L);
        Assertions.assertThat(myPageUserOutDTO.getName()).isEqualTo("lee");
        Assertions.assertThat(myPageUserOutDTO.getPropensity()).isEqualTo(UserPropensity.AGGRESSIVE);
        Assertions.assertThat(myPageUserOutDTO.getStep().getHasDoneBoardBookmark()).isEqualTo(false);
        Assertions.assertThat(myPageUserOutDTO.getStep().getHasDonePropensity()).isEqualTo(true);
        Assertions.assertThat(myPageUserOutDTO.getStep().getHasDoneReservation()).isEqualTo(false);
        Assertions.assertThat(myPageUserOutDTO.getStep().getHasDoneReview()).isEqualTo(false);
        Assertions.assertThat(myPageUserOutDTO.getReservationCount().getApply()).isEqualTo(0);
        Assertions.assertThat(myPageUserOutDTO.getReservationCount().getConfirm()).isEqualTo(0);
        Assertions.assertThat(myPageUserOutDTO.getReservationCount().getComplete()).isEqualTo(0);
        Assertions.assertThat(myPageUserOutDTO.getBoardBookmark().getList().size()).isEqualTo(0);
        Assertions.assertThat(myPageUserOutDTO.getBoardBookmark().getCount()).isEqualTo(0);
        Assertions.assertThat(myPageUserOutDTO.getPbBookmark().getList().size()).isEqualTo(0);
        Assertions.assertThat(myPageUserOutDTO.getPbBookmark().getCount()).isEqualTo(0);
    }

    @Test
    public void updateMyInfo_test() {
        // given
        String newPhoneNumber = "01011223344";
        UserRequest.UpdateMyInfoInDTO updateMyInfoInDTO = new UserRequest.UpdateMyInfoInDTO();
        updateMyInfoInDTO.setPhoneNumber(newPhoneNumber);

        // stub
        User mockUser = User.builder()
                .id(1L)
                .name("lee")
                .password(passwordEncoder.encode("password1234"))
                .email("jisu31484@naver.com")
                .phoneNumber(newPhoneNumber)
                .role(Role.USER)
                .profile("profile.png")
                .createdAt(LocalDateTime.now())
                .build();
        when(myUserDetails.getMember()).thenReturn(mockUser);
        when(memberUtil.findById(any(), any())).thenReturn(mockUser);

        // when
        userService.updateMyInfo(updateMyInfoInDTO, myUserDetails);

        // then
        Assertions.assertThat(mockUser.getPhoneNumber()).isEqualTo(newPhoneNumber);
    }

    @Test
    public void getMyInfo_test() {
        // stub
        User user = newMockUser(1L, "lee");
        when(myUserDetails.getMember()).thenReturn(user);

        // when
        userService.getMyInfo(myUserDetails);

        // then
        verify(myUserDetails, times(1)).getMember();
    }

    @Test
    public void checkPassword_fail_test() {
        // given
        String wrongPassword = "Wrong1234";
        UserRequest.CheckPasswordInDTO checkPasswordInDTO = new UserRequest.CheckPasswordInDTO();
        checkPasswordInDTO.setPassword(wrongPassword);

        String originalPassword = newMockUser(1L, "lee").getPassword();
        when(myUserDetails.getPassword()).thenReturn(originalPassword);

        // when & then
        assertThrows(Exception401.class, () -> {
            userService.checkPassword(checkPasswordInDTO, myUserDetails);
        });
    }

    @Test
    public void checkPassword_test() {
        // given
        String password = "password1234";
        UserRequest.CheckPasswordInDTO checkPasswordInDTO = new UserRequest.CheckPasswordInDTO();
        checkPasswordInDTO.setPassword(password);

        String originalPassword = newMockUser(1L, "lee").getPassword();
        when(myUserDetails.getPassword()).thenReturn(originalPassword);

        // when
        userService.checkPassword(checkPasswordInDTO, myUserDetails);

        // then
        verify(passwordEncoder, times(1)).matches(password, originalPassword);
    }

    @Test
    public void updatePassword_test() {
        // given
        UserRequest.RePasswordInDTO rePasswordInDTO = new UserRequest.RePasswordInDTO();
        rePasswordInDTO.setId(1L);
        rePasswordInDTO.setRole(Role.USER);
        rePasswordInDTO.setPassword("1111abcd");

        when(memberUtil.findById(rePasswordInDTO.getId(), rePasswordInDTO.getRole()))
                .thenReturn(newMockUser(1L, "lee"));

        // when
        userService.updatePassword(rePasswordInDTO);

        // then
        verify(memberUtil, times(1)).findById(1L, Role.USER);
        verify(passwordEncoder, times(1)).encode("1111abcd");
    }

    @Test
    public void findEmail_test() {
        // given
        UserRequest.EmailFindInDTO emailFindInDTO = new UserRequest.EmailFindInDTO();
        emailFindInDTO.setRole(Role.USER);
        emailFindInDTO.setPhoneNumber("01012345678");

        when(memberUtil.findByPhoneNumberWithoutException(emailFindInDTO.getPhoneNumber(),
                emailFindInDTO.getRole())).thenReturn(Arrays.asList(newMockUser(1L, "lee")));

        // when
        List<UserResponse.EmailFindOutDTO> emailFindOutDTOs = userService.findEmail(emailFindInDTO);

        // then
        Assertions.assertThat(emailFindOutDTOs.get(0).getName()).isEqualTo("lee");
        Assertions.assertThat(emailFindOutDTOs.get(0).getPhoneNumber()).isEqualTo("01012345678");
        Assertions.assertThat(emailFindOutDTOs.get(0).getEmail()).isEqualTo("lee@nate.com");
    }

    @Test
    public void password_test() throws Exception {
        // given
        String email = "lee@nate.com";
        UserRequest.PasswordInDTO passwordInDTO = new UserRequest.PasswordInDTO();
        passwordInDTO.setRole(Role.USER);
        passwordInDTO.setEmail(email);

        when(memberUtil.findByEmailWithoutException(passwordInDTO.getEmail(), passwordInDTO.getRole()))
                .thenReturn(newMockUser(1L, "lee"));
        when(msgUtil.createMessage(anyString(), any(), any())).thenReturn(mock(MimeMessage.class));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // when
        UserResponse.PasswordOutDTO passwordOutDTO = userService.password(passwordInDTO);

        // then
        String regex = "^[0-9A-Z]{8}$"; // 이는 8자리 숫자와 대문자를 예상하는 정규식입니다.
        Assertions.assertThat(passwordOutDTO.getCode()).matches(regex);
        Assertions.assertThat(passwordOutDTO.getRole()).isEqualTo(Role.USER);
        Assertions.assertThat(passwordOutDTO.getName()).isEqualTo("lee");
        Assertions.assertThat(passwordOutDTO.getPhoneNumber()).isEqualTo("01012345678");
        Assertions.assertThat(passwordOutDTO.getEmail()).isEqualTo(email);
    }

    @Test
    public void email_test() {
        // given
        Role role = Role.USER;
        String email = "lee@nate.com";
        UserRequest.EmailInDTO emailInDTO = new UserRequest.EmailInDTO();
        emailInDTO.setEmail(email);
        emailInDTO.setRole(role);

        when(msgUtil.createMessage(anyString(), any(), any())).thenReturn(mock(MimeMessage.class));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // when
        UserResponse.EmailOutDTO emailOutDTO = userService.email(emailInDTO);

        // then
        String regex = "^[0-9A-Z]{8}$"; // 이는 8자리 숫자와 대문자를 예상하는 정규식입니다.
        Assertions.assertThat(emailOutDTO.getCode()).matches(regex);
    }

    @Test
    public void withdraw_test() {
        // given
        String password = "Test1234";
        UserRequest.WithdrawInDTO withdrawInDTO = new UserRequest.WithdrawInDTO();
        withdrawInDTO.setPassword(password);
        String encodedPassword = passwordEncoder.encode(withdrawInDTO.getPassword());

        when(myUserDetails.getPassword()).thenReturn(encodedPassword);
        when(myUserDetails.getMember()).thenReturn(newMockUser(1L, "lee"));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        // when
        userService.withdraw(withdrawInDTO, myUserDetails);

        // then
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verify(memberUtil, times(1)).deleteById(1L, Role.USER);
    }

    @Test
    public void withdraw_fail_test() {
        // given
        String password = "Test1234";
        String wrongPassword = "Wrong1234";
        UserRequest.WithdrawInDTO withdrawInDTO = new UserRequest.WithdrawInDTO();
        withdrawInDTO.setPassword(wrongPassword);
        String encodedPassword = passwordEncoder.encode(password);

        when(myUserDetails.getPassword()).thenReturn(encodedPassword);
        when(passwordEncoder.matches(wrongPassword, encodedPassword)).thenReturn(false);

        // when & then
        assertThrows(Exception401.class, () -> {
            userService.withdraw(withdrawInDTO, myUserDetails);
        });
    }

    @Test
    public void logout_test() {
        // given
        Long id = 1L;
        String role = "USER";
        String key = id + role;
        User user = newMockUser(id, "김투자");
        String accessJwt = MyJwtProviderTest.createTestAccess(user);
        String refreshToken = MyJwtProviderTest.createTestRefresh(user);

        // HttpServletRequest를 모킹하여 HEADER_ACCESS 헤더에서 액세스 토큰을 반환하도록 설정
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader(JwtProvider.HEADER_ACCESS)).thenReturn(JwtProvider.TOKEN_PREFIX + accessJwt);

        // MyJwtProvider를 모킹하여 액세스 토큰과 리프레시 토큰을 검증하고, 검증된 JWT의 클레임을 반환하도록 설정
        DecodedJWT mockDecodedJWT = mock(DecodedJWT.class);

        ReflectionTestUtils.setField(JwtProvider.class, "SECRET_ACCESS", "originwasdonjul", String.class);
        ReflectionTestUtils.setField(JwtProvider.class, "SECRET_REFRESH", "backend", String.class);

        try (MockedStatic<JwtProvider> myJwtProviderMock = Mockito.mockStatic(JwtProvider.class)) {
            myJwtProviderMock.when(() -> JwtProvider.verifyAccess(accessJwt)).thenReturn(mockDecodedJWT);
            myJwtProviderMock.when(() -> JwtProvider.verifyRefresh(refreshToken)).thenReturn(mockDecodedJWT);
        } // static 메서드여서

        // RedisUtil를 모킹하여 레디스에서 리프레시 토큰을 조회하고, 키에 해당하는 리프레시 토큰을 삭제하고, 액세스 토큰을 블랙리스트에 추가하도록 설정
        when(redisUtil.delete(key)).thenReturn(true);
        doNothing().when(redisUtil).setBlackList(any(), any(), any());

        // when
        userService.logout(mockRequest, refreshToken);

        // 세 번째 파라미터를 캡쳐할 ArgumentCaptor를 생성합니다.
        ArgumentCaptor<Long> remainingTimeCaptor = ArgumentCaptor.forClass(Long.class);

        // then
        verify(redisUtil, times(1)).delete(key);
        verify(redisUtil, times(1)).setBlackList(
                eq(accessJwt.replace(JwtProvider.TOKEN_PREFIX, "")),
                eq("access_token_blacklist"),
                remainingTimeCaptor.capture() // 캡쳐합니다.
        );

        // 캡쳐한 값을 가져와서 원하는 조건을 만족하는지 검사합니다.
        Long capturedRemainingTimeMillis = remainingTimeCaptor.getValue();
        assertTrue(capturedRemainingTimeMillis <= JwtProvider.EXP_ACCESS && capturedRemainingTimeMillis >= 0);
    }

        @Test
    public void reissue_test() {
        // given
        String prefix = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtb25leWJyaWRnZSIsInJvbGUiOiJQQiIsImlkIjo";
        Long id = 1L;
        String role = "PB";
        String key = id + role;
        Company c= newMockCompany(1L, "미래에셋증권");
        Branch b = newMockBranch(1L, c, 0);
        PB pb = newMockPB(id, "김피비", b);
        String accessJwt = MyJwtProviderTest.createTestAccess(pb);
        String refreshToken = MyJwtProviderTest.createTestRefresh(pb);

        // HttpServletRequest를 모킹하여 HEADER_ACCESS 헤더에서 액세스 토큰을 반환하도록 설정
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader(JwtProvider.HEADER_ACCESS)).thenReturn(accessJwt);

        // MyJwtProvider를 모킹하여 액세스 토큰을 디코딩하고, 디코딩된 JWT의 클레임을 반환하도록 설정
        DecodedJWT mockDecodedJWT = mock(DecodedJWT.class);
        try (MockedStatic<JWT> jwtMock = Mockito.mockStatic(JWT.class)) {
            jwtMock.when(() -> JWT.decode(accessJwt.replace(JwtProvider.TOKEN_PREFIX, "")))
                    .thenReturn(mockDecodedJWT);
        }

        // RedisUtil를 모킹하여 레디스에서 리프레시 토큰을 조회하고, 키에 해당하는 리프레시 토큰을 삭제하도록 설정
        when(redisUtil.get(key)).thenReturn(refreshToken);
        when(redisUtil.delete(key)).thenReturn(true);

        // MyMemberUtil를 모킹하여 멤버를 조회하고, 멤버에 해당하는 액세스 토큰과 리프레시 토큰을 생성하도록 설정
        // 가짜 멤버 객체를 생성하고, getId와 getRole 메서드의 반환값을 설정합니다.
        Member mockMember = mock(Member.class);
        when(mockMember.getId()).thenReturn(1L);
        when(mockMember.getRole()).thenReturn(Role.PB);

        when(memberUtil.findById(id, Role.valueOf(role))).thenReturn(mockMember);

        ReflectionTestUtils.setField(JwtProvider.class, "SECRET_ACCESS", "originwasdonjul", String.class);
        ReflectionTestUtils.setField(JwtProvider.class, "SECRET_REFRESH", "backend", String.class);

        try (MockedStatic<JwtProvider> myJwtProviderMock = Mockito.mockStatic(JwtProvider.class)) {
            myJwtProviderMock.when(() -> JwtProvider.createAccess(mockMember))
                    .thenReturn(accessJwt);
        }
        when(jwtProvider.createRefresh(mockMember))
                .thenReturn(refreshToken);

        // when
        Pair<String, String> tokens = userService.reissue(mockRequest, refreshToken);

        // then
        Assertions.assertThat(tokens.getLeft().substring(0, prefix.length() + 7)).isEqualTo(
                JwtProvider.TOKEN_PREFIX + prefix.substring(0, prefix.length()));
        Assertions.assertThat(tokens.getRight().substring(0, prefix.length())).isEqualTo(
                prefix.substring(0, prefix.length()));
    }


    @Test
    public void issue_test() {
        // given
        Role role = Role.PB;
        String email = "김pb@nate.com";
        String password = "password1234";

        // 가짜 인증 객체를 생성합니다.
        MyUserDetails mockUserDetails = mock(MyUserDetails.class);
        Authentication mockAuthentication = mock(Authentication.class);

        // 인증 매니저가 인증을 성공적으로 수행하도록 설정합니다.
        when(authenticationManager.authenticate(any()))
                .thenReturn(mockAuthentication);

        // 인증 매니저가 반환하는 인증 객체의 주체가 MyUserDetails 인스턴스를 반환하도록 설정합니다.
        when(mockAuthentication.getPrincipal())
                .thenReturn(mockUserDetails);

        // 가짜 멤버 객체를 생성하고, getId와 getRole 메서드의 반환값을 설정합니다.
        Member mockMember = mock(Member.class);
        when(mockMember.getId()).thenReturn(1L);
        when(mockMember.getRole()).thenReturn(Role.PB);

        // 가짜 사용자 상세 정보 객체가 가짜 멤버 객체를 반환하도록 설정합니다.
        when(mockUserDetails.getMember())
                .thenReturn(mockMember);

        ReflectionTestUtils.setField(JwtProvider.class, "SECRET_ACCESS", "originwasdonjul", String.class);
        ReflectionTestUtils.setField(JwtProvider.class, "SECRET_REFRESH", "backend", String.class);

        // JwtProvider가 액세스 토큰과 리프레시 토큰을 성공적으로 생성하도록 설정합니다.
        String expectedAccessTokenPrefix = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtb25leWJyaWRnZSIsInJvbGUiOiJQQiIsImlkIjo";
        String expectedRefreshTokenPrefix = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtb25leWJyaWRnZSIsInJvbGUiOiJQQiIsImlkIjo";
        try (MockedStatic<JwtProvider> myJwtProviderMock = Mockito.mockStatic(JwtProvider.class)) {
            myJwtProviderMock.when(() -> JwtProvider.createAccess(mockMember))
                    .thenReturn(expectedAccessTokenPrefix + ".signature");
        } // static 메서드여서
        when(jwtProvider.createRefresh(mockMember))
                .thenReturn(expectedRefreshTokenPrefix + ".signature");

        // when
        Pair<String, String> tokens = userService.issue(role, email, password);

        // then
        Assertions.assertThat(tokens.getLeft().substring(0, expectedAccessTokenPrefix.length())).isEqualTo(expectedAccessTokenPrefix);
        Assertions.assertThat(tokens.getRight().substring(0, expectedRefreshTokenPrefix.length())).isEqualTo(expectedRefreshTokenPrefix);
    }

    @Test
    public void joinUser_test() {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setEmail("investor@naver.com");
        joinInDTO.setPassword("kang1234");
        joinInDTO.setName("강투자");
        joinInDTO.setPhoneNumber("01012345678");
        List<UserRequest.AgreementDTO> agreements = new ArrayList<>();
        UserRequest.AgreementDTO agreement1 = new UserRequest.AgreementDTO();
        agreement1.setTitle("돈줄 이용약관 동의");
        agreement1.setType(UserAgreementType.REQUIRED);
        agreement1.setIsAgreed(true);
        agreements.add(agreement1);
        UserRequest.AgreementDTO agreement2 = new UserRequest.AgreementDTO();
        agreement2.setTitle("마케팅 정보 수신 동의");
        agreement2.setType(UserAgreementType.OPTIONAL);
        agreement2.setIsAgreed(true);
        agreements.add(agreement2);
        joinInDTO.setAgreements(agreements);

        // stub 1
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        // stub 2
        User user = newMockUser(1L, "lee");
        when(userRepository.save(any())).thenReturn(user);

        // stub 3
        UserAgreement userAgreement = newMockUserAgreement(1L, user, UserAgreementType.REQUIRED);
        when(userAgreementRepository.save(any())).thenReturn(userAgreement);

        // when
        UserResponse.JoinOutDTO joinOutDTO = userService.joinUser(joinInDTO);

        // then
        Assertions.assertThat(joinOutDTO.getId()).isEqualTo(1L);
    }
    @Test
    public void login_user_test() {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setRole(Role.USER);
        loginInDTO.setEmail("lee@nate.com");
        loginInDTO.setPassword("password1234");

        // stub 1
        User user = newMockUser(1L, "lee");
        when(memberUtil.findByEmail(any(), any())).thenReturn(user);

        // when
        UserResponse.LoginOutDTO loginOutDTO = userService.login(loginInDTO);

        // then
        Assertions.assertThat(loginOutDTO.getIsAdmin()).isFalse();
    }

    @Test
    public void login_admin_test() {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setRole(Role.USER);
        loginInDTO.setEmail("jisu8496@naver.com");
        loginInDTO.setPassword("password1234");

        // stub 1
        User user = newMockUserADMIN(1L,"강투자");
        when(memberUtil.findByEmail(any(), any())).thenReturn(user);

        // when
        UserResponse.LoginOutDTO loginOutDTO = userService.login(loginInDTO);

        // then
        Assertions.assertThat(loginOutDTO.getIsAdmin()).isTrue();
    }

    @Test
    @DisplayName("PB 북마크하기")
    void bookmarkPB() {
        //given
        MyUserDetails mockUserDetails = mock(MyUserDetails.class);
        Member mockMember = mock(Member.class);
        User user = newMockUser(1L, "김투자");
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "김피비", branch);
        UserBookmark userBookmark = newMockUserBookmark(1L, user, pb);

        //stub
        when(mockUserDetails.getMember()).thenReturn(mockMember);
        when(mockMember.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(pbRepository.findById(1L)).thenReturn(Optional.of(pb));
        when(userBookmarkRepository.findByUserIdWithPbId(1L, 1L)).thenReturn(Optional.empty());

        //when
        userService.bookmarkPB(mockUserDetails, 1L);

        //then
        verify(userBookmarkRepository, times(1)).save(any(UserBookmark.class));
    }

    @Test
    @DisplayName("PB 북마크 취소하기")
    void deletePBBookmark() {
        //given
        MyUserDetails mockUserDetails = mock(MyUserDetails.class);
        Member mockMember = mock(Member.class);
        User user = newMockUser(1L, "김투자");
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "김피비", branch);
        UserBookmark userBookmark = newMockUserBookmark(1L, user, pb);

        //stub
        when(mockUserDetails.getMember()).thenReturn(mockMember);
        when(mockMember.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(pbRepository.findById(1L)).thenReturn(Optional.of(pb));
        when(userBookmarkRepository.findByUserIdWithPbId(1L, 1L)).thenReturn(Optional.of(userBookmark));

        //when
        userService.deletePBBookmark(mockUserDetails, 1L);

        //then
        verify(userBookmarkRepository, times(1)).delete(any(UserBookmark.class));
    }
}
