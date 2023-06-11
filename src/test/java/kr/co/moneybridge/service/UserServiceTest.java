package kr.co.moneybridge.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.Pair;
//import kr.co.moneybridge.core.MockClaim;
import kr.co.moneybridge.core.auth.jwt.MyJwtProvider;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.core.util.MyMemberUtil;
import kr.co.moneybridge.core.util.RedisUtil;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
import kr.co.moneybridge.dto.user.UserRequest;
import kr.co.moneybridge.dto.user.UserResponse;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.model.pb.Company;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBRepository;
import kr.co.moneybridge.model.reservation.ReservationRepository;
import kr.co.moneybridge.model.user.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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

    // 가짜 객체를 만들어서 Mockito 환경에 Load
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private MyJwtProvider myJwtProvider;
    @Mock
    private RedisUtil redisUtil;
    @Mock
    private MyMemberUtil myMemberUtil;

    // 진짜 객체를 만들어서 Mockito 환경에 Load
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void reissue_test() {
        // given
        String prefix = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtb25leWJyaWRnZSIsInJvbGUiOiJQQiIsImlkIjo";
        String accessJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtb25leWJyaWRnZSIsInJvbGUiOiJQQiIsImlkIjoxLCJleHAiOjE2ODc2ODQxMDd9.LzNpNcVuyyorwnHIewrg7s9CRmiUO0f-evAdtKiFMZ5kXAmEe4itiI-U_ow8PA288LgxeDD58-DJjC3ebbhxiQ";
        String refreshToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtb25leWJyaWRnZSIsInJvbGUiOiJQQiIsImlkIjoxLCJleHAiOjE2ODY1MTc2OTN9.O9eLsSPJICnGFtNVtXeFcape19-EisJbPPWQeKwPGJT6BXeueE2U0okUkqXAw8dIWu2espIMR4UHT3UWkftT-g";
        Long id = 1L;
        String role = "PB";
        String key = id + role;

        // HttpServletRequest를 모킹하여 HEADER_ACCESS 헤더에서 액세스 토큰을 반환하도록 설정
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getHeader(MyJwtProvider.HEADER_ACCESS)).thenReturn(accessJwt);

        // MyJwtProvider를 모킹하여 액세스 토큰을 디코딩하고, 디코딩된 JWT의 클레임을 반환하도록 설정
        DecodedJWT mockDecodedJWT = Mockito.mock(DecodedJWT.class);
        try (MockedStatic<JWT> jwtMock = Mockito.mockStatic(JWT.class)) {
            jwtMock.when(() -> JWT.decode(accessJwt.replace(MyJwtProvider.TOKEN_PREFIX, "")))
                    .thenReturn(mockDecodedJWT);
        }

        // RedisUtil를 모킹하여 레디스에서 리프레시 토큰을 조회하고, 키에 해당하는 리프레시 토큰을 삭제하도록 설정
        when(redisUtil.get(key)).thenReturn(refreshToken);
        when(redisUtil.delete(key)).thenReturn(true);

        // MyMemberUtil를 모킹하여 멤버를 조회하고, 멤버에 해당하는 액세스 토큰과 리프레시 토큰을 생성하도록 설정
        // 가짜 멤버 객체를 생성하고, getId와 getRole 메서드의 반환값을 설정합니다.
        Member mockMember = Mockito.mock(Member.class);
        when(mockMember.getId()).thenReturn(1L);
        when(mockMember.getRole()).thenReturn(Role.PB);

        when(myMemberUtil.findById(id, Role.valueOf(role))).thenReturn(mockMember);

        ReflectionTestUtils.setField(MyJwtProvider.class, "SECRET_ACCESS", "originwasdonjul", String.class);
        ReflectionTestUtils.setField(MyJwtProvider.class, "SECRET_REFRESH", "backend", String.class);

        try (MockedStatic<MyJwtProvider> myJwtProviderMock = Mockito.mockStatic(MyJwtProvider.class)) {
            myJwtProviderMock.when(() -> MyJwtProvider.createAccess(mockMember))
                    .thenReturn(accessJwt);
        }
        when(myJwtProvider.createRefresh(mockMember))
                .thenReturn(refreshToken);

        // when
        Pair<String, String> tokens = userService.reissue(mockRequest, refreshToken);

        // then
        Assertions.assertThat(tokens.getLeft().substring(0, prefix.length() + 7)).isEqualTo(
                MyJwtProvider.TOKEN_PREFIX + prefix.substring(0, prefix.length()));
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
        MyUserDetails mockUserDetails = Mockito.mock(MyUserDetails.class);
        Authentication mockAuthentication = Mockito.mock(Authentication.class);

        // 인증 매니저가 인증을 성공적으로 수행하도록 설정합니다.
        when(authenticationManager.authenticate(any()))
                .thenReturn(mockAuthentication);

        // 인증 매니저가 반환하는 인증 객체의 주체가 MyUserDetails 인스턴스를 반환하도록 설정합니다.
        when(mockAuthentication.getPrincipal())
                .thenReturn(mockUserDetails);

        // 가짜 멤버 객체를 생성하고, getId와 getRole 메서드의 반환값을 설정합니다.
        Member mockMember = Mockito.mock(Member.class);
        when(mockMember.getId()).thenReturn(1L);
        when(mockMember.getRole()).thenReturn(Role.PB);

        // 가짜 사용자 상세 정보 객체가 가짜 멤버 객체를 반환하도록 설정합니다.
        when(mockUserDetails.getMember())
                .thenReturn(mockMember);

        ReflectionTestUtils.setField(MyJwtProvider.class, "SECRET_ACCESS", "originwasdonjul", String.class);
        ReflectionTestUtils.setField(MyJwtProvider.class, "SECRET_REFRESH", "backend", String.class);

        // JwtProvider가 액세스 토큰과 리프레시 토큰을 성공적으로 생성하도록 설정합니다.
        String expectedAccessTokenPrefix = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtb25leWJyaWRnZSIsInJvbGUiOiJQQiIsImlkIjo";
        String expectedRefreshTokenPrefix = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtb25leWJyaWRnZSIsInJvbGUiOiJQQiIsImlkIjo";
        try (MockedStatic<MyJwtProvider> myJwtProviderMock = Mockito.mockStatic(MyJwtProvider.class)) {
            myJwtProviderMock.when(() -> MyJwtProvider.createAccess(mockMember))
                    .thenReturn(expectedAccessTokenPrefix + ".signature");
        } // static 메서드여서
        when(myJwtProvider.createRefresh(mockMember))
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
}
