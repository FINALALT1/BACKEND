package kr.co.moneybridge.core.auth.jwt;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception401;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.core.util.FilterResponseUtil;
import kr.co.moneybridge.core.util.MemberUtil;
import kr.co.moneybridge.core.util.RedisUtil;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final RedisUtil redisUtil;
    private final MemberUtil memberUtil;
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, RedisUtil redisUtil, MemberUtil memberUtil) {
        super(authenticationManager);
        this.redisUtil = redisUtil;
        this.memberUtil = memberUtil;
    }

    // SecurityConfig 에 인증을 설정한 API에 대한 request 요청은 모두 이 필터를 거치기 때문에 토큰 정보가 없거나 유효하지 않은 경우 정상적으로 수행되지 않음
    // 헤더(Authorization)에 있는 토큰을 꺼내 이상이 없는 경우 SecurityContext에 저장
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String accessToken = request.getHeader(JwtProvider.HEADER_ACCESS);

        String requestURL = request.getRequestURI();

        // 인증 또는 권한이 필요한 URI가 아니면 JWT 토큰 검사를 아예 하지 않음(프론트에서 토큰을 보내도 검사 안함 + 리프레시 토큰 재발급시에도 검사 안함)
        // accessToken에 값이 없을 때도 필터처리를 하지 않음(테스트코드에서 토큰 안보냄) => 그래도 인증이 되지 않았기 때문에 Security에서 401, 403 실패 처리 해줌.
        if (!(requestURL.startsWith("/auth") || requestURL.equals("/auth") ||
                requestURL.startsWith("/user") || requestURL.equals("/user") ||
                requestURL.startsWith("/pb") || requestURL.equals("/pb") ||
                requestURL.startsWith("/admin") || requestURL.equals("/admin")) || (accessToken == null)) {
            log.debug("토큰 검사 안함");
            chain.doFilter(request, response);
            return;
        }

        String accessJwt = accessToken.replace(JwtProvider.TOKEN_PREFIX, "");
        try {
            log.debug("토큰 있음");
            // access token이 Blacklist에 등록되었는지 Redis를 조회하여 확인
            if(redisUtil.hasKeyBlackList(accessJwt)) {
                // 블랙리스트에 저장된 토큰이라면 에러를 반환
                log.error("블랙리스트에 등록된 액세스 토큰");
                FilterResponseUtil.serverError(response, new Exception500("이미 로그아웃한 액세스 토큰입니다"));
            }
            DecodedJWT decodedJWT = JwtProvider.verifyAccess(accessJwt);
            Long id = decodedJWT.getClaim("id").asLong();
            Role role = Role.valueOf(decodedJWT.getClaim("role").asString().toUpperCase());
            // 사용자 조회 - 탈퇴 여부 체크
            Member member = null;
            try{
                member = memberUtil.findById(id, role);
            }catch (Exception e){
                log.error("토큰 정보에서 에러 : " + e.getMessage());
                FilterResponseUtil.unAuthorized(response, new Exception401("인증 실패: " + e.getMessage()));
            }
            MyUserDetails myUserDetails = new MyUserDetails(member);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            myUserDetails,
                            myUserDetails.getPassword(),
                            myUserDetails.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("인증 객체 만들어짐");
        } catch (RedisConnectionFailureException e) { // 사용자가 유효한 인증 정보를 가지고 있더라도, 그 정보를 불러올 수 없으니 마치 사용자가 인증받지 않은 것처럼 보이게 됨.
            SecurityContextHolder.clearContext(); // 현재 사용자에 대한 인증 정보가 없거나 액세스할 수 없다는 것을 명확하게 나타낼 수 있다.
            log.error("Redis 연결 실패");
        } catch (SignatureVerificationException sve) {
            log.error("토큰 검증 실패");
        } catch (TokenExpiredException tee) {
            log.debug("토큰 만료됨");
            FilterResponseUtil.unAuthorized(response, new Exception401("Access token expired"));
        } finally {
            chain.doFilter(request, response);
        }
    }
}