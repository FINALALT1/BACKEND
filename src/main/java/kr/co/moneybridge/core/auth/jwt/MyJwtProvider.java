package kr.co.moneybridge.core.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import kr.co.moneybridge.core.annotation.MyErrorLog;
import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.util.RedisUtil;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@RequiredArgsConstructor
@Component
public class MyJwtProvider {
    private final RedisUtil redisUtil;
    private static final String SUBJECT = "moneybridge";
    private static final Long EXP_ACCESS = 1000 * 60 * 60 * 12L; // 12시간
    protected static final Long EXP_REFRESH = 1000 * 60 * 60 * 24 * 14L; // 14일
    public static final String TOKEN_PREFIX = "Bearer "; // 스페이스 필요함
    public static final String HEADER_ACCESS = "Authorization";
    public static final String SECRET_ACCESS = System.getenv("SECRET_ACCESS");
    public static final String SECRET_REFRESH = System.getenv("SECRET_REFRESH");

    // Access 토큰 생성
    @MyLog
    @MyErrorLog
    public static String createAccess(Member member) {
        return TOKEN_PREFIX + JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP_ACCESS))
                .withClaim("id", member.getId())
                .withClaim("role", member.getRole().toString())
                .sign(Algorithm.HMAC512(SECRET_ACCESS));
    }

    // Refresh 토큰 생성
    @MyLog
    @MyErrorLog
    public String createRefresh(Member member) {
        String refreshToken = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP_REFRESH))
                .withClaim("id", member.getId())
                .withClaim("role", member.getRole().toString())
                .sign(Algorithm.HMAC512(SECRET_REFRESH));
        // Redis에 refresh token을 저장
        redisUtil.set(
                member.getId() + member.getRole().toString(),
                refreshToken,
                EXP_REFRESH
        );
        return refreshToken;
    }

    // Access 토큰을 검증
    @MyLog
    @MyErrorLog
    public static DecodedJWT verifyAccess(String accessJwt) throws SignatureVerificationException, TokenExpiredException {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET_ACCESS))
                .build().verify(accessJwt);
        return decodedJWT;
    }

    // Refresh 토큰을 검증
    @MyLog
    @MyErrorLog
    public static DecodedJWT verifyRefresh(String jwt) throws SignatureVerificationException, TokenExpiredException {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET_REFRESH))
                .build().verify(jwt);
        return decodedJWT;
    }
}