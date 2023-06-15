package kr.co.moneybridge.dto.reservation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.reservation.LocationType;
import kr.co.moneybridge.model.reservation.ReservationGoal;
import kr.co.moneybridge.model.reservation.ReservationType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class ReservationRequest {
    // validation은 controller에서 수행
    @ApiModel
    @Getter
    @Setter
    public static class ApplyDTO {
        @ApiModelProperty(example = "PROFIT")
        private ReservationGoal goal;

        @ApiModelProperty(example = "VISIT")
        private ReservationType reservationType;

        @ApiModelProperty(example = "BRANCH")
        private LocationType locationType;

        @ApiModelProperty(example = "2023년 6월 1일 오전 9시 20분")
        private String candidateTime1;

        @ApiModelProperty(example = "2023년 6월 1일 오전 9시 20분")
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

    // validation은 controller에서 수행
    @ApiModel
    @Getter
    @Setter
    public static class UpdateDTO {
        @ApiModelProperty(example = "2023년 6월 1일 오전 9시 20분")
        private String time;

        @ApiModelProperty(example = "VISIT")
        private ReservationType type;

        @ApiModelProperty(example = "미래에셋증권 용산wm점")
        private String locationName;

        @ApiModelProperty(example = "서울특별시 용산구 한강로동 한강대로 92")
        private String locationAddress;
    }

    @ApiModel
    @Getter
    @Setter
    public static class ConfirmDTO {
        @ApiModelProperty(example = "2023년 6월 1일 오전 9시 20분")
        @Pattern(regexp = "\\\\d{1,2}월 \\\\d{1,2}일 (오전|오후) \\\\d{1,2}시 \\\\d{1,2}분",
                message = "형식에 맞춰 입력해주세요.")
        @NotEmpty
        private String time;
    }
}
