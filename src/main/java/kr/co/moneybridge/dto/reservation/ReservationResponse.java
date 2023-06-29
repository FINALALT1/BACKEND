package kr.co.moneybridge.dto.reservation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static kr.co.moneybridge.core.util.MyDateUtil.localDateTimeToString;

public class ReservationResponse {
    @ApiModel(description = "현재 나의 상담 가능 시간 불러오기 응답 데이터")
    @Getter
    @Setter
    public static class MyConsultTimeDTO {
        @ApiModelProperty(example = "09:00", value = "상담 가능 시작 시간")
        private String consultStart;

        @ApiModelProperty(example = "18:00", value = "상담 가능 종료 시간")
        private String consultEnd;

        @ApiModelProperty(example = "월요일 불가능합니다", value = "상담 불가 시간 메시지")
        private String consultNotice;

        public MyConsultTimeDTO(PB pb) {
            this.consultStart = (pb.getConsultStart() == null) ? null : pb.getConsultStart().format(DateTimeFormatter.ofPattern("HH:mm"));
            this.consultEnd = (pb.getConsultEnd() == null) ? null : pb.getConsultEnd().format(DateTimeFormatter.ofPattern("HH:mm"));
            this.consultNotice = pb.getConsultNotice();
        }
    }

    @ApiModel(description = "나의 후기 하나 가져오기 응답 데이터")
    @Getter
    @Setter
    public static class MyReviewDTO {
        @ApiModelProperty(example = "EXCELLENT", value = "상담 일정 준수 등급")
        private ReviewAdherence adherence;

        @ApiModelProperty(example = "FAST", value = "상담 스타일")
        private List<StyleDTO> styleList;

        @ApiModelProperty(example = "content 입니다", value = "후기 내용")
        private String content;

        public MyReviewDTO(Review review, List<StyleDTO> styleList) {
            this.adherence = review.getAdherence();
            this.styleList = styleList;
            this.content = review.getContent();
        }
    }

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
        private String userName;

        @ApiModelProperty(example = "content입니다.")
        private String content;

        @ApiModelProperty(example = "2023-05-15")
        private String createdAt;

        @ApiModelProperty
        private List<StyleDTO> list;

        public ReviewDTO(Review review, User user, List<Style> styles) {
            this.reviewId = review.getId();
            this.userName = user.getName();
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
        @ApiModelProperty(example = "HONEST", value = "상담 스타일")
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
        private Long applyCount;

        @ApiModelProperty(example = "true")
        private Boolean isNewApply;

        @ApiModelProperty(example = "3")
        private Long confirmCount;

        @ApiModelProperty(example = "false")
        private Boolean isNewConfirm;

        @ApiModelProperty(example = "0")
        private Long completeCount;

        @ApiModelProperty(example = "false")
        private Boolean isNewComplete;

        public RecentInfoDTO(Long applyCount, Boolean isNewApply, Long confirmCount, Boolean isNewConfirm, Long completeCount, Boolean isNewComplete) {
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

    @Getter
    @Setter
    public static class RecentPagingByUserDTO {
        private Long reservationId;
        private Long pbId;
        private String profileImage;
        private String name;
        private LocalDateTime createdAt;
        private ReservationType type;

        public RecentPagingByUserDTO(Reservation reservation, PB pb) {
            this.reservationId = reservation.getId();
            this.pbId = pb.getId();
            this.profileImage = pb.getProfile();
            this.name = pb.getName();
            this.createdAt = reservation.getCreatedAt();
            this.type = reservation.getType();
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class RecentReservationByUserDTO {
        @ApiModelProperty(example = "1")
        private Long reservationId;

        @ApiModelProperty(example = "true")
        private Boolean isNewReservation;

        @ApiModelProperty(example = "1")
        private Long pbId;

        @ApiModelProperty(example = "profile.png")
        private String profileImage;

        @ApiModelProperty(example = "홍길동")
        private String name;

        @ApiModelProperty(example = "2023년 6월 1일 오전 9시 20분")
        private String createdAt;

        @ApiModelProperty(example = "CALL")
        private ReservationType type;

        public RecentReservationByUserDTO(Long reservationId, Boolean isNewReservation, Long pbId, String profileImage, String name, String createdAt, ReservationType type) {
            this.reservationId = reservationId;
            this.isNewReservation = isNewReservation;
            this.pbId = pbId;
            this.profileImage = profileImage;
            this.name = name;
            this.createdAt = createdAt;
            this.type = type;
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class DetailByPBDTO {
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

        @ApiModelProperty(example = "09:00")
        private String consultStart;

        @ApiModelProperty(example = "09:00")
        private String consultEnd;

        @ApiModelProperty(example = "09:00")
        private String notice;

        @ApiModelProperty(example = "true")
        private Boolean reviewCheck;

        public DetailByPBDTO(Long userId, String profileImage, String name, String phoneNumber, String email, Long reservationId, String candidateTime1, String candidateTime2, String time, ReservationType type, String location, String locationAddress, ReservationGoal goal, String question, String consultStart, String consultEnd, String notice, Boolean reviewCheck) {
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
            this.consultStart = consultStart;
            this.consultEnd = consultEnd;
            this.notice = notice;
            this.reviewCheck = reviewCheck;
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class DetailByUserDTO {
        @ApiModelProperty(example = "1")
        private Long pbId;

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

        @ApiModelProperty(example = "09:00")
        private String consultStart;

        @ApiModelProperty(example = "09:00")
        private String consultEnd;

        @ApiModelProperty(example = "09:00")
        private String notice;

        @ApiModelProperty(example = "true")
        private Boolean reviewCheck;

        public DetailByUserDTO(Long pbId, String profileImage, String name, String phoneNumber, String email, Long reservationId, String candidateTime1, String candidateTime2, String time, ReservationType type, String location, String locationAddress, ReservationGoal goal, String question, String consultStart, String consultEnd, String notice, Boolean reviewCheck) {
            this.pbId = pbId;
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
            this.consultStart = consultStart;
            this.consultEnd = consultEnd;
            this.notice = notice;
            this.reviewCheck = reviewCheck;
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class ReviewIdDTO {
        @ApiModelProperty(example = "1")
        private Long id;

        public ReviewIdDTO(Long id) {
            this.id = id;
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class ReservationInfoDTO {
        @ApiModelProperty(example = "1")
        private Long id;

        @ApiModelProperty(example = "이승민")
        private String userName;

        @ApiModelProperty(example = "2023-05-15")
        private LocalDate day;

        @ApiModelProperty(example = "09:00")
        private String time;

        @ApiModelProperty(example = "CALL")
        private ReservationType type;

        @ApiModelProperty(example = "APPLY")
        private ReservationProcess process;

        // time 컬럼의 값이 null이라면 candidateTime1의 값을 가져온다.
        public ReservationInfoDTO(Reservation reservation, User user) {
            this.id = reservation.getId();
            this.userName = user.getName();
            if (reservation.getTime() != null) {
                this.day = reservation.getTime().toLocalDate();
                this.time = reservation.getTime().toLocalTime().truncatedTo(ChronoUnit.MINUTES).toString();
            } else {
                this.day = reservation.getCandidateTime1().toLocalDate();
                this.time = reservation.getCandidateTime1().toLocalTime().truncatedTo(ChronoUnit.MINUTES).toString();
            }
            this.type = reservation.getType();
            this.process = reservation.getProcess();
        }
    }
}
