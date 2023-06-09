package kr.co.moneybridge.dto.reservation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

public class ReservationResponse {
    @Getter
    @Setter
    public static class ReservationBaseOutDTO{
        @ApiModelProperty(example = "미래에셋증권 용산wm점")
        private String branchName;

        @ApiModelProperty(example = "서울특별시 용산구 한강로동 한강대로 92")
        private String branchAddress;

        @ApiModelProperty(example = "37.55628")
        private String branchLatitude;

        @ApiModelProperty(example = "126.97037")
        private String branchLongitude;

        @ApiModelProperty(example = "09:00")
        private String consultStart;

        @ApiModelProperty(example = "10:00")
        private String consultEnd;

        @ApiModelProperty(example = "홍길동")
        private String userName;

        @ApiModelProperty(example = "01012345678")
        private String userPhoneNumber;

        @ApiModelProperty(example = "asdf1234@gmail.")
        private String userEmail;

        @ApiModelProperty(example = "월요일 13시 제외")
        private String notice;

        public ReservationBaseOutDTO(String branchName, String branchAddress, String branchLatitude, String branchLongitude, String consultStart, String consultEnd, String userName, String userPhoneNumber, String userEmail, String notice) {
            this.branchName = branchName;
            this.branchAddress = branchAddress;
            this.branchLatitude = branchLatitude;
            this.branchLongitude = branchLongitude;
            this.consultStart = consultStart;
            this.consultEnd = consultEnd;
            this.userName = userName;
            this.userPhoneNumber = userPhoneNumber;
            this.userEmail = userEmail;
            this.notice = notice;
        }
    }
}
