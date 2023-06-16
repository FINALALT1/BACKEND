package kr.co.moneybridge.dto.reservation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static kr.co.moneybridge.core.util.MyDateUtil.localDateTimeToString;

public class ReservationResponse {
    @ApiModel
    @Getter
    @Setter
    public static class BaseDTO {
        @ApiModelProperty
        private PBInfoDTO pbInfo;

        @ApiModelProperty
        private ConsultInfoDTO consultInfo;

        @ApiModelProperty
        private UserInfoDTO userInfo;

        public BaseDTO(PBInfoDTO pbInfo, ConsultInfoDTO consultInfo, UserInfoDTO userInfo) {
            this.pbInfo = pbInfo;
            this.consultInfo = consultInfo;
            this.userInfo = userInfo;
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class PBInfoDTO {
        @ApiModelProperty(example = "김피비")
        private String pbName;

        @ApiModelProperty(example = "미래에셋증권 용산wm점")
        private String branchName;

        @ApiModelProperty(example = "서울특별시 용산구 한강로동 한강대로 92")
        private String branchAddress;

        @ApiModelProperty(example = "37.55628")
        private Double branchLatitude;

        @ApiModelProperty(example = "126.97037")
        private Double branchLongitude;

        public PBInfoDTO(String pbName, String branchName, String branchAddress, Double branchLatitude, Double branchLongitude) {
            this.pbName = pbName;
            this.branchName = branchName;
            this.branchAddress = branchAddress;
            this.branchLatitude = branchLatitude;
            this.branchLongitude = branchLongitude;
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class ConsultInfoDTO {
        @ApiModelProperty(example = "09:00")
        private String consultStart;

        @ApiModelProperty(example = "10:00")
        private String consultEnd;

        @ApiModelProperty(example = "월요일 13시 제외")
        private String notice;

        public ConsultInfoDTO(String consultStart, String consultEnd, String notice) {
            this.consultStart = consultStart;
            this.consultEnd = consultEnd;
            this.notice = notice;
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class UserInfoDTO {
        @ApiModelProperty(example = "홍길동")
        private String userName;

        @ApiModelProperty(example = "01012345678")
        private String userPhoneNumber;

        @ApiModelProperty(example = "asdf1234@gmail.com")
        private String userEmail;

        public UserInfoDTO(String userName, String userPhoneNumber, String userEmail) {
            this.userName = userName;
            this.userPhoneNumber = userPhoneNumber;
            this.userEmail = userEmail;
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class ReviewDTO {
        @ApiModelProperty(example = "1")
        private Long reviewId;

        @ApiModelProperty(example = "홍길동")
        private String username;

        @ApiModelProperty(example = "content입니다.")
        private String content;

        @ApiModelProperty(example = "2023-05-15")
        private String createdAt;

        @ApiModelProperty
        private List<StyleDTO> list;

        public ReviewDTO(Review review, User user, List<Style> styles) {
            this.reviewId = review.getId();
            this.username = user.getName();
            this.content = review.getContent();
            this.createdAt = localDateTimeToString(review.getCreatedAt());
            this.list = styles.stream()
                    .map(style -> {
                        return new ReservationResponse.StyleDTO(style.getStyle());
                    })
                    .collect(Collectors.toList());
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class StyleDTO {
        @ApiModelProperty(example = "HONEST")
        private StyleStyle style;

        public StyleDTO(StyleStyle style) {
            this.style = style;
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class RecentInfoDTO {
        @ApiModelProperty(example = "2")
        private Integer applyCount;

        @ApiModelProperty(example = "true")
        private Boolean isNewApply;

        @ApiModelProperty(example = "3")
        private Integer confirmCount;

        @ApiModelProperty(example = "false")
        private Boolean isNewConfirm;

        @ApiModelProperty(example = "0")
        private Integer completeCount;

        @ApiModelProperty(example = "false")
        private Boolean isNewComplete;

        public RecentInfoDTO(Integer applyCount, Boolean isNewApply, Integer confirmCount, Boolean isNewConfirm, Integer completeCount, Boolean isNewComplete) {
            this.applyCount = applyCount;
            this.isNewApply = isNewApply;
            this.confirmCount = confirmCount;
            this.isNewConfirm = isNewConfirm;
            this.completeCount = completeCount;
            this.isNewComplete = isNewComplete;
        }
    }

    @Getter
    @Setter
    public static class RecentPagingDTO { // 여기에 페이징해서 먼저 담고(-> RecentReservationDTO)
        private Long reservationId;
        private Long userId;
        private String profileImage;
        private String name;
        private LocalDateTime createdAt;
        private ReservationType type;

        public RecentPagingDTO(Reservation reservation, User user) {
            this.reservationId = reservation.getId();
            this.userId = user.getId();
            this.profileImage = user.getProfile();
            this.name = user.getName();
            this.createdAt = reservation.getCreatedAt();
            this.type = reservation.getType();
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class RecentReservationDTO { // (->RecentPagingDTO)그걸 가공한 뒤 여기에 담아서 응답
        @ApiModelProperty(example = "1")
        private Long reservationId;

        @ApiModelProperty(example = "true")
        private Boolean isNewReservation;

        @ApiModelProperty(example = "1")
        private Long userId;

        @ApiModelProperty(example = "profile.png")
        private String profileImage;

        @ApiModelProperty(example = "홍길동")
        private String name;

        @ApiModelProperty(example = "2023년 6월 1일 오전 9시 20분")
        private String createdAt;

        @ApiModelProperty(example = "CALL")
        private ReservationType type;

        public RecentReservationDTO(Long reservationId, Boolean isNewReservation, Long userId, String profileImage, String name, String createdAt, ReservationType type) {
            this.reservationId = reservationId;
            this.isNewReservation = isNewReservation;
            this.userId = userId;
            this.profileImage = profileImage;
            this.name = name;
            this.createdAt = createdAt;
            this.type = type;
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class DetailDTO {
        @ApiModelProperty(example = "1")
        private Long userId;

        @ApiModelProperty(example = "profile.png")
        private String profileImage;

        @ApiModelProperty(example = "홍길동")
        private String name;

        @ApiModelProperty(example = "01012345678")
        private String phoneNumber;

        @ApiModelProperty(example = "gildong123@gmail.com")
        private String email;

        @ApiModelProperty(example = "1")
        private Long reservationId;

        @ApiModelProperty(example = "2023년 6월 1일 오전 9시 20분")
        private String candidateTime1;

        @ApiModelProperty(example = "2023년 6월 1일 오전 9시 50분")
        private String candidateTime2;

        @ApiModelProperty(example = "2023년 6월 1일 오전 9시 20분")
        private String time;

        @ApiModelProperty(example = "VISIT")
        private ReservationType type;

        @ApiModelProperty(example = "미래에셋증권 용산 WM점")
        private String location;

        @ApiModelProperty(example = "서울특별시 용산구 한강로동 한강대로 92")
        private String locationAddress;

        @ApiModelProperty(example = "TAX")
        private ReservationGoal goal;

        @ApiModelProperty(example = "잘 부탁드립니다.")
        private String question;

        public DetailDTO(Long userId, String profileImage, String name, String phoneNumber, String email, Long reservationId, String candidateTime1, String candidateTime2, String time, ReservationType type, String location, String locationAddress, ReservationGoal goal, String question) {
            this.userId = userId;
            this.profileImage = profileImage;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.reservationId = reservationId;
            this.candidateTime1 = candidateTime1;
            this.candidateTime2 = candidateTime2;
            this.time = time;
            this.type = type;
            this.location = location;
            this.locationAddress = locationAddress;
            this.goal = goal;
            this.question = question;
        }
    }
}
