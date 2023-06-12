package kr.co.moneybridge.core.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.model.Member;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

public class MyJwtProviderTest {
    private static final String SUBJECT = "moneybridge";
    public static final Long EXP_ACCESS = 1000 * 60 * 60 * 12L; // 12시간
    protected static final Long EXP_REFRESH = 1000 * 60 * 60 * 24 * 14L; // 14일
    private static final String SECRET_ACCESS = "originwasdonjul";
    private static final String SECRET_REFRESH = "backend";

    // Access 토큰 생성
    public static String createTestAccess(Member member) {
        return JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP_ACCESS))
                .withClaim("id", member.getId())
                .withClaim("role", member.getRole().toString())
                .sign(Algorithm.HMAC512(SECRET_ACCESS));
    }

    // Refresh 토큰 생성
    public static String createTestRefresh(Member member) {
        String refreshToken = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP_REFRESH))
                .withClaim("id", member.getId())
                .withClaim("role", member.getRole().toString())
                .sign(Algorithm.HMAC512(SECRET_REFRESH));
        return refreshToken;
    }
}

