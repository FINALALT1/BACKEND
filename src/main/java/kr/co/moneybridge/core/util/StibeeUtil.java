package kr.co.moneybridge.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.exception.Exception500;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

// 스티비 자동 이메일 발송 관련 클래스
@Component
@Slf4j
public class StibeeUtil {
    // 주소록(전체)
    private final String TOTAL_LIST_URL = "https://api.stibee.com/v1/lists/269642/subscribers";
    // 주소록(일반)
    private final String USER_LIST_URL = "https://api.stibee.com/v1/lists/272140/subscribers";
    // 주소록(PB)
    private final String PB_LIST_URL = "https://api.stibee.com/v1/lists/272138/subscribers";
    private final String AUTHENTICATION_EMAIL_URL = "https://stibee.com/api/v1.0/auto/ZmQ4N2ZmNWMtYTZlYy00MGM2LWFkZGItZDY4YWZlYmM3ZDdi";
    // API 요청에 필요한 토큰
    private final String API_KEY;

    public StibeeUtil(@Value("${STIBEE_API_KEY}") String API_KEY) {
        this.API_KEY = API_KEY;
    }

    /**
     * 스티비 주소록 구독자 추가
     */
    public void subscribe(String role, String email, String name) {
        // role에 따라 구독자를 추가할 주소록 결정
        String requestUrl = "";
        switch (role) {
            case "ADMIN":
            case "USER":
                requestUrl = USER_LIST_URL;
                break;
            case "PB":
                requestUrl = PB_LIST_URL;
                break;
            default:
                throw new Exception500("구독자 추가 실패 : 해당 사용자의 role이 존재하지 않거나 잘못된 형태입니다.");
        }

        // 역할에 해당하는 주소록에 구독자 추가
        subscribeRequest(requestUrl, email, name);
        // 전체 주소록에 구독자 추가
        subscribeRequest(TOTAL_LIST_URL, email, name);
    }

    // 구독자 추가 API 요청
    private void subscribeRequest(String requestUrl, String email, String name) {
        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("AccessToken", API_KEY);

        JSONObject requestBody = new JSONObject();
        requestBody.put("eventOccuredBy", "MANUAL"); // 구독자를 추가한 방법
        requestBody.put("confirmEmailYN", "N"); // 구독 확인 이메일 발송 여부

        JSONArray subscribersArray = new JSONArray();
        JSONObject subscribers = new JSONObject();
        subscribers.put("email", email);
        subscribers.put("name", name);
        subscribersArray.put(subscribers);
        requestBody.put("subscribers", subscribersArray); // 구독자 정보
        // 광고성 정보 수신 동의와 관련된 헤더도 있으나 여기선 생략

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        // API 요청
        ResponseEntity<String> response = null;
        try {
            response = template.exchange(
                    requestUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );
        } catch (Exception e) {
            log.error("API 요청 실패 : " + e.getMessage());
            return;
        }

        String responseBody = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(responseBody);
        } catch (Exception e) {
            log.error("Json to JsonNode 실패 : " + e.getMessage());
            return;
        }

        if (root.path("Ok").asText().equals("false")) { // 실패
            JsonNode error = root.path("Error");
            log.error("구독자 추가 요청 실패 : " + error.path("message").asText());
        } else {
            log.info("구독자 추가 요청 성공 : " + email);
        }
    }

    /**
     * 스티비 주소록 구독자 완전삭제
     */
    public void withdraw(String role, String email) {
        // role에 따라 구독자를 삭제할 주소록 결정
        String requestUrl = "";
        switch (role) {
            case "ADMIN":
            case "USER":
                requestUrl = USER_LIST_URL;
                break;
            case "PB":
                requestUrl = PB_LIST_URL;
                break;
            default:
                throw new Exception500("구독자 삭제 실패 : 해당 사용자의 role이 존재하지 않거나 잘못된 형태입니다.");
        }

        // 역할에 해당하는 주소록에서 구독자 삭제
        withdrawRequest(requestUrl, email);
        // 전체 주소록에서 구독자 삭제
        withdrawRequest(TOTAL_LIST_URL, email);
    }

    // 구독자 완전 삭제 API 요청
    private void withdrawRequest(String requestUrl, String email) {
        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("AccessToken", API_KEY);

        JSONArray requestBody = new JSONArray();
        requestBody.put(email);

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        // API 요청
        ResponseEntity<String> response = null;
        try {
            response = template.exchange(
                    requestUrl,
                    HttpMethod.DELETE,
                    request,
                    String.class
            );
        } catch (Exception e) {
            log.error("API 요청 실패 : " + e.getMessage());
            return;
        }

        String responseBody = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(responseBody);
        } catch (Exception e) {
            log.error("Json to JsonNode 실패 : " + e.getMessage());
            return;
        }

        if (root.path("Ok").asText().equals("false")) { // 실패
            JsonNode error = root.path("Error");
            log.error("주소록 구독 요청 실패 : " + error.path("message").asText());
        } else {
            log.info("주소록 구독 요청 성공 : " + email);
        }
    }

    /**
     * 인증 코드 안내 이메일 발송
     */
    public void sendAuthenticationEmail(String email, String code) {
        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("AccessToken", API_KEY);

        JSONObject requestBody = new JSONObject();
        requestBody.put("subscriber", email); // 구독자 이메일
        requestBody.put("code", code); // 인증 코드

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        // API 요청
        try {
            template.exchange(
                    AUTHENTICATION_EMAIL_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );
        } catch (Exception e) {
            log.error("API 요청 실패 : " + e.getMessage());
        }
    }
}
