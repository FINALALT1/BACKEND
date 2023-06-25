package kr.co.moneybridge.dto.backOffice;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class FullAddress {
    private String roadAddress; // 도로명 주소
    private String streetAddress; // 지번 주소
    private Double latitude; // 위도
    private Double longitude; // 경도
}
