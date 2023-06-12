package kr.co.moneybridge.dto.reservation;

import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.model.reservation.Review;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

public class ReservationResponse {
    @Getter
    @Setter
    public static class BaseOutDTO {
        @ApiModelProperty
        private pbInfoDTO pbInfo;

        @ApiModelProperty
        private consultInfoDTO consultInfo;

        @ApiModelProperty
        private userInfoDTO userInfo;

        public BaseOutDTO(pbInfoDTO pbInfo, consultInfoDTO consultInfo, userInfoDTO userInfo) {
            this.pbInfo = pbInfo;
            this.consultInfo = consultInfo;
            this.userInfo = userInfo;
        }
    }

    @Getter
    @Setter
    public static class pbInfoDTO{
        @ApiModelProperty(example = "김피비")
        private String pbName;

        @ApiModelProperty(example = "미래에셋증권 용산wm점")
        private String branchName;

        @ApiModelProperty(example = "서울특별시 용산구 한강로동 한강대로 92")
        private String branchAddress;

        @ApiModelProperty(example = "37.55628")
        private String branchLatitude;

        @ApiModelProperty(example = "126.97037")
        private String branchLongitude;

        public pbInfoDTO(String pbName, String branchName, String branchAddress, String branchLatitude, String branchLongitude) {
            this.pbName = pbName;
            this.branchName = branchName;
            this.branchAddress = branchAddress;
            this.branchLatitude = branchLatitude;
            this.branchLongitude = branchLongitude;
        }
    }

    @Getter
    @Setter
    public static class consultInfoDTO{
        @ApiModelProperty(example = "09:00")
        private String consultStart;

        @ApiModelProperty(example = "10:00")
        private String consultEnd;

        @ApiModelProperty(example = "월요일 13시 제외")
        private String notice;

        public consultInfoDTO(String consultStart, String consultEnd, String notice) {
            this.consultStart = consultStart;
            this.consultEnd = consultEnd;
            this.notice = notice;
        }
    }

    @Getter
    @Setter
    public static class userInfoDTO{
        @ApiModelProperty(example = "홍길동")
        private String userName;

        @ApiModelProperty(example = "01012345678")
        private String userPhoneNumber;

        @ApiModelProperty(example = "asdf1234@gmail.")
        private String userEmail;

        public userInfoDTO(String userName, String userPhoneNumber, String userEmail) {
            this.userName = userName;
            this.userPhoneNumber = userPhoneNumber;
            this.userEmail = userEmail;
        }
    }

    @Getter
    @Setter
    public static class ReviewDTO {
        private Long reviewId;
        private String username;
        private String content;
        private String createdAt;

        public ReviewDTO(Review review, User user) {
            this.reviewId = review.getId();
            this.username = user.getName();
            this.content = review.getContent();
            this.createdAt = MyDateUtil.localDateTimeToString(review.getCreatedAt());
        }
    }
}
