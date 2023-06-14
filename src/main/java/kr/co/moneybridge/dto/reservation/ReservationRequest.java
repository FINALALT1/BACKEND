package kr.co.moneybridge.dto.reservation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.reservation.LocationType;
import kr.co.moneybridge.model.reservation.ReservationGoal;
import kr.co.moneybridge.model.reservation.ReservationType;
import lombok.Getter;
import lombok.Setter;

public class ReservationRequest {
    // validation은 controller에서 수행
    @ApiModel
    @Getter
    @Setter
    public static class ApplyInDTO {
        @ApiModelProperty(example = "PROFIT")
        private ReservationGoal goal;

        @ApiModelProperty(example = "VISIT")
        private ReservationType reservationType;

        @ApiModelProperty(example = "BRANCH")
        private LocationType locationType;

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
