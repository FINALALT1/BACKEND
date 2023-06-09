package kr.co.moneybridge.dto.reservation;

import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.reservation.LocationType;
import kr.co.moneybridge.model.reservation.ReservationGoal;
import kr.co.moneybridge.model.reservation.ReservationType;
import lombok.Getter;
import lombok.Setter;

public class ReservationRequest {
    // validation은 controller에서 수행
    @Getter
    @Setter
    public static class ApplyReservationInDTO {
        @ApiModelProperty(example = "PROFIT")
        private ReservationGoal goal1;

        @ApiModelProperty(example = "RISK")
        private ReservationGoal goal2;

        @ApiModelProperty(example = "VISIT")
        private ReservationType reservationType;

        @ApiModelProperty(example = "BRANCH")
        private LocationType locationType;

        @ApiModelProperty(example = "미래에셋증권 용산wm점")
        private String locationName;

        @ApiModelProperty(example = "서울특별시 용산구 한강로동 한강대로 92")
        private String locationAddress;

        @ApiModelProperty(example = "2023-05-15T09:00:00")
        private String candidateTime1;

        @ApiModelProperty(example = "2023-05-15T10:00:00")
        private String candidateTime2;

        @ApiModelProperty(example = "잘 부탁드립니다.")
        private String question;

        @ApiModelProperty(example = "홍길동")
        private String userName;

        @ApiModelProperty(example = "01012345678")
        private String userPhoneNumber;

        @ApiModelProperty(example = "asdf1234@gmail.com")
        private String userEmail;
    }
}
