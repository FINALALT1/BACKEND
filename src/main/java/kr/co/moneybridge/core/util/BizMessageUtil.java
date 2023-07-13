package kr.co.moneybridge.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.exception.Exception500;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static kr.co.moneybridge.core.util.MyDateUtil.StringToLocalDateTimeV2;

@Component
@Profile("prod")
@Slf4j
public class BizMessageUtil {
    private final String BASE_URL = "https://www.biztalk-api.com";
    private final String BIZ_ID;
    private final String BIZ_PASSWORD;
    private final String SENDER_KEY;
    private final String TOKEN_PREFIX = "Bearer ";

    // thread-safe
    private static String accessToken = null;

    // thread-safe
    private static LocalDateTime expirationTime = LocalDateTime.now();


    public BizMessageUtil(@Value("${biz.oauth2.biz-id}") String BIZ_ID,
                          @Value("${biz.oauth2.biz-password}") String BIZ_PASSWORD,
                          @Value("${biz.sender-key}") String SENDER_KEY) {
        this.BIZ_ID = BIZ_ID;
        this.BIZ_PASSWORD = BIZ_PASSWORD;
        this.SENDER_KEY = SENDER_KEY;
    }

    private static synchronized String getAccessToken() {
        return accessToken;
    }

    private static synchronized void setAccessToken(String accessToken) {
        BizMessageUtil.accessToken = accessToken;
    }

    private static synchronized LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    private static synchronized void setExpirationTime(LocalDateTime expirationTime) {
        BizMessageUtil.expirationTime = expirationTime;
    }

    /**
     * 액세스 토큰 발급
     */
    public void getToken() throws JsonProcessingException {
        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("bsid", BIZ_ID);
        requestBody.add("passwd", BIZ_PASSWORD);
        requestBody.add("expire", "720");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        // API 요청
        ResponseEntity<String> response = template.exchange(
                BASE_URL + "/v2/auth/getToken",
                HttpMethod.POST,
                request,
                String.class
        );

        String responseBody = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBody);

        int code = root.path("responseCode").asInt();
        if (code != 1000) {
            log.error("OAuth2 인증 실패 : " + root.path("msg").asText());
        }

        setAccessToken(root.path("token").asText()); // 액세스 토큰 갱신
        setExpirationTime(StringToLocalDateTimeV2(root.path("expireDate").asText())); // 만료시간 갱신
    }
}
