package kr.co.moneybridge.core.util;

import com.nimbusds.jose.util.Pair;
import kr.co.moneybridge.core.exception.Exception200;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.dto.backOffice.FullAddress;
import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.model.pb.BranchAddressType;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
public class GeoCodingUtil {
    private final String clientId;
    private final String clientSecret;

    public GeoCodingUtil(@Value("${MB_CLIENT_ID}") String clientId,
                         @Value("${MB_CLIENT_SECRET}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public FullAddress getFullAddress(String address) {
        // 1) API에 주소값 넣어서 요청
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query="+address;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // 2) 응답에서 지번주소, 도로명주소, 위도 경도 찾아서 FullAddress 반환
        JSONTokener tokener = new JSONTokener(response.getBody());
        JSONObject object = new JSONObject(tokener);
        JSONArray arr = object.getJSONArray("addresses");

        if(arr.length() == 0){
            throw new Exception400("address", "해당 주소로의 검색결과가 없습니다");
        }
        if(arr.length() != 1){
            throw new Exception400("address", "주소가 하나로 특정되지 않습니다. 정확한 주소를 입력해주세요");
        }

        JSONObject temp = (JSONObject) arr.get(0);
        String roadAddress = temp.get("roadAddress").toString(); // 도로명 주소
        String streetAddress = temp.get("jibunAddress").toString(); // 지번 주소

        Double yDouble =temp.getDouble("y"); // 위도
        BigDecimal bd = new BigDecimal(yDouble);
        bd = bd.setScale(5, RoundingMode.HALF_UP);
        Double latitude = bd.doubleValue();

        Double xDouble = temp.getDouble("x"); // 경도
        bd = new BigDecimal(xDouble);
        bd = bd.setScale(5, RoundingMode.HALF_UP);
        Double longitude = bd.doubleValue();

        return FullAddress.builder()
                .roadAddress(roadAddress)
                .streetAddress(streetAddress)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
