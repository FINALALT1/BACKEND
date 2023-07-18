package kr.co.moneybridge.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.reservation.Reservation;
import kr.co.moneybridge.model.reservation.ReservationType;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static kr.co.moneybridge.core.util.MyDateUtil.StringToLocalDateTimeV2;
import static kr.co.moneybridge.core.util.MyDateUtil.localDateTimeToStringV2;

@Component
@Slf4j
public class BizMessageUtil {
    private final String BASE_URL = "https://www.biztalk-api.com";
    private final String BIZ_ID;
    private final String BIZ_PASSWORD;
    private final String SENDER_KEY;

    // thread-safe
    private static String accessToken = null;

    // thread-safe
    private static LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(10L);


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

    // template_001
    public String getTempMsg001(String pbName, String userName, Reservation res) {
        return "안녕하세요 " + pbName + " PB님,\n" +
                userName + " 투자자님으로부터\n" +
                "새로운 예약이 도착했습니다.\n" +
                "내용을 확인하신 후 예약을 확정해주세요.\n" +
                "\n" +
                "# 신청된 예약 정보\n" +
                "■ 예약자: " + res.getInvestor() + "\n" +
                "■ 담당 PB: " + pbName + "\n" +
                "■ 상담 희망 일정(1순위): " + localDateTimeToStringV2(res.getCandidateTime1()) + "\n" +
                "■ 상담 희망 일정(2순위): " + localDateTimeToStringV2(res.getCandidateTime1()) + "\n" +
                "■ 상담 방식: " + res.getType().name() + "\n" +
                "■ 미팅 장소: " + (res.getType().equals(ReservationType.CALL) ? "-" : res.getLocationName()) + "\n" +
                "■ 상담 목적: " + res.getGoal().name() + "\n" +
                "■ 요청 사항: " + res.getQuestion();
    }

    // template_002
    public String getTempMsg002(String userName, String pbName, LocalDateTime date) {
        return "안녕하세요 " + userName + "님,\n" +
                pbName + " PB님이 예약을 취소하셨습니다.\n" +
                "\n" +
                "# 취소된 예약 정보\n" +
                "■ 예약자: " + userName + "\n" +
                "■ 담당 PB: " + pbName + "\n" +
                "■ 예약 신청일: " + localDateTimeToStringV2(date);
    }

    // template_003
    public String getTempMsg003(String pbName, String userName, LocalDateTime date) {
        return "안녕하세요 " + pbName + " PB님,\n" +
                userName + "님이 예약을 취소하셨습니다.\n" +
                "\n" +
                "# 취소된 예약 정보\n" +
                "■ 예약자: " + userName + "\n" +
                "■ 담당 PB: " + pbName + "\n" +
                "■ 예약 신청일: " + localDateTimeToStringV2(date);
    }

    // template_004
    public String getTempMsg004(String userName, String pbName, Reservation res) {
        return "안녕하세요 " + userName + "님,\n" +
                pbName + " PB님이 예약을 확정하셨습니다.\n" +
                "\n" +
                "# 확정된 예약 정보\n" +
                "■ 예약자: " + res.getInvestor() + "\n" +
                "■ 담당 PB: " + pbName + "\n" +
                "■ 상담 일정: " + localDateTimeToStringV2(res.getTime()) + "\n" +
                "■ 상담 방식: " + res.getType().name() + "\n" +
                "■ 미팅 장소: " + (res.getType().equals(ReservationType.CALL) ? "-" : res.getLocationName()) + "\n" +
                "■ 상담 목적: " + res.getGoal().name() + "\n" +
                "■ 요청 사항: " + res.getQuestion();
    }

    // template_005
    public String getTempMsg005(String userName, String pbName, Board board) {
        return "안녕하세요 " + userName + "님,\n" +
                "고객님이 북마크하신 " + pbName + "PB님의 새로운 컨텐츠가 올라왔습니다.\n" +
                "\n" +
                "# 게시 정보\n" +
                "■ 제목: " + board.getTitle() + "\n" +
                "■ 내용: " + (board.getContent().length() > 20 ? board.getContent().substring(0, 21) + "..." : board.getContent());
    }

    /**
     * 액세스 토큰 발급
     */
    private void getToken() {
        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject requestBody = new JSONObject();
        requestBody.put("bsid", BIZ_ID);
        requestBody.put("passwd", BIZ_PASSWORD);
        requestBody.put("expire", "720");

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        // API 요청
        ResponseEntity<String> response = template.exchange(
                BASE_URL + "/v2/auth/getToken",
                HttpMethod.POST,
                request,
                String.class
        );

        String responseBody = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(responseBody);
        } catch (Exception e) {
            log.error("String to Json 실패 : " + e.getMessage());
        }

        int code = root.path("responseCode").asInt();
        if (code != 1000) {
            log.error("OAuth2 인증 실패 : " + code + ", " + root.path("msg").asText());
        }

        setAccessToken(root.path("token").asText()); // 액세스 토큰 갱신
        setExpirationTime(StringToLocalDateTimeV2(root.path("expireDate").asText())); // 만료시간 갱신
    }

    /**
     * 기본 알림톡 발신
     */
    public void sendNotification(String phoneNumber, Template temp, String message) {
        // 토큰 만료/미발급시 재발급
        if (getExpirationTime().isBefore(LocalDateTime.now())) {
            getToken();
        }

        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("bt-token", getAccessToken());

        JSONObject requestBody = new JSONObject();
        requestBody.put("msgIdx", "MB2023");
        requestBody.put("countryCode", "82");
        requestBody.put("resMethod", "PUSH");
        requestBody.put("senderKey", SENDER_KEY);
        requestBody.put("tmpltCode", temp.getCode());
        requestBody.put("message", message);
        requestBody.put("recipient", phoneNumber);

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        // API 요청
        ResponseEntity<String> response = template.exchange(
                BASE_URL + "/v2/kko/sendAlimTalk",
                HttpMethod.POST,
                request,
                String.class
        );

        String responseBody = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(responseBody);
        } catch (Exception e) {
            log.error("String to Json 실패 : " + e.getMessage());
        }

        int code = root.path("responseCode").asInt();
        if (code != 1000) {
            log.error("비즈톡 G/W 접수 실패 : " + code + ", " + root.path("msg").asText());
        }
    }

    /**
     * 웹링크 버튼 알림톡 발신
     */
    public void sendWebLinkNotification(String phoneNumber, Template temp, String message) {
        // 토큰 만료/미발급시 재발급
        if (getExpirationTime().isBefore(LocalDateTime.now())) {
            getToken();
        }

        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("bt-token", getAccessToken());

        JSONObject requestBody = new JSONObject();
        requestBody.put("msgIdx", "MB2023");
        requestBody.put("countryCode", "82");
        requestBody.put("resMethod", "PUSH");
        requestBody.put("senderKey", SENDER_KEY);
        requestBody.put("tmpltCode", temp.getCode());
        requestBody.put("message", message);
        requestBody.put("recipient", phoneNumber);

        JSONObject attach = new JSONObject();
        JSONArray buttonArray = new JSONArray();
        JSONObject button = new JSONObject();
        button.put("name", "홈페이지로 이동");
        button.put("type", "WL");
        button.put("url_mobile", "https://www.moneybridge.co.kr");
        button.put("url_pc", "https://www.moneybridge.co.kr");
        buttonArray.put(button);
        attach.put("button", buttonArray);
        requestBody.put("attach", attach);

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        // API 요청
        ResponseEntity<String> response = template.exchange(
                BASE_URL + "/v2/kko/sendAlimTalk",
                HttpMethod.POST,
                request,
                String.class
        );

        String responseBody = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(responseBody);
        } catch (Exception e) {
            log.error("String to Json 실패 : " + e.getMessage());
        }

        int code = root.path("responseCode").asInt();
        if (code != 1000) {
            log.error("비즈톡 G/W 접수 실패 : " + code + ", " + root.path("msg").asText());
        }
    }
}
