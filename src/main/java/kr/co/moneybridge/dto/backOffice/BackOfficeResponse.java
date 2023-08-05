package kr.co.moneybridge.dto.backOffice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.backoffice.FrequentQuestion;
import kr.co.moneybridge.model.backoffice.Notice;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBSpeciality;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static kr.co.moneybridge.core.util.MyDateUtil.*;

public class BackOfficeResponse {
    @ApiModel(description = "후기 리스트 데이터")
    @Getter
    @Setter
    public static class ReviewTotalDTO {
        @ApiModelProperty(example = "content 입니다", value = "후기 나용")
        private String content;

        @ApiModelProperty(example = "EXCELLENT", value = "상담 일정 준수 등급")
        private ReviewAdherence adherence;

        @ApiModelProperty
        private List<ReservationResponse.StyleDTO> styles;

        public ReviewTotalDTO(Review review, List<ReservationResponse.StyleDTO> styles) {
            this.content = review.getContent();
            this.adherence = review.getAdherence();
            this.styles = styles;
        }
    }

    @ApiModel(description = "예약 리스트 데이터")
    @Getter
    @Setter
    public static class ReservationTotalDTO {
        @ApiModelProperty(example = "1", value = "예약 id")
        private Long id;

        @ApiModelProperty(example = "APPLY", value = "예약 상태")
        private ReservationProcess process;

        @ApiModelProperty(example = "ACTIVE", value = "취소 여부")
        private ReservationStatus status;

        @ApiModelProperty(example = "2023년 6월 20일 오전 1시 39분", value = "확정 날짜")
        private String time;

        @ApiModelProperty(example = "VISIT", value = "유선/방문")
        private ReservationType type;

        @ApiModelProperty(example = "kb증권 강남중앙점", value = "상담 장소")
        private String locationName;

        @ApiModelProperty(example = "PROFIT", value = "상담 목적")
        private ReservationGoal goal;

        @ApiModelProperty(example = "질문입니다...", value = "문의 사항")
        private String question;

        @ApiModelProperty(example = "2023-01-01", value = "상담 신청일")
        private String createdAt;

        @ApiModelProperty
        private UserDTO user;

        @ApiModelProperty
        private PBDTO pb;

        @ApiModelProperty
        private ReviewTotalDTO review;

        public ReservationTotalDTO(Reservation reservation, UserDTO user, PBDTO pb, ReviewTotalDTO review) {
            this.id = reservation.getId();
            this.process = reservation.getProcess();
            this.status = reservation.getStatus();
            this.time = reservation.getTime() == null ? null : reservation.getTime().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분"));
            this.type = reservation.getType();
            this.locationName = reservation.getLocationName() == null ? null : reservation.getLocationName();
            this.goal = reservation.getGoal();
            this.question = reservation.getQuestion() == null ? null : reservation.getQuestion();
            this.createdAt = localDateTimeToString(reservation.getCreatedAt());
            this.user = user;
            this.pb = pb;
            this.review = review;
        }
    }

    @ApiModel(description = "상담 내역의 각 건수 데이터")
    @Getter
    @Setter
    public static class ReservationTotalCountDTO {
        @ApiModelProperty(example = "2", value = "총 상담 신청 건수")
        private Long apply;

        @ApiModelProperty(example = "3", value = "총 상담 확정 건수")
        private Long confirm;

        @ApiModelProperty(example = "2", value = "총 상담 완료 건수")
        private Long complete;

        @ApiModelProperty(example = "2", value = "총 후기 작성 건수")
        private Long review;

        @ApiModelProperty(example = "2", value = "PB 승인 대기 건수")
        private Long pb;

        public ReservationTotalCountDTO(Long apply, Long confirm, Long complete, Long review, Long pb) {
            this.apply = apply;
            this.confirm = confirm;
            this.complete = complete;
            this.review = review;
            this.pb = pb;
        }
    }

    @ApiModel(description = "PB 리스트 데이터")
    @Getter
    @Setter
    public static class PBDTO {
        @ApiModelProperty(example = "1", value = "PB id")
        private Long id;

        @ApiModelProperty(example = "김pb@nate.com", value = "이메일")
        private String email;

        @ApiModelProperty(example = "김pb", value = "이름")
        private String name;

        @ApiModelProperty(example = "01012345678", value = "휴대폰 번호")
        private String phoneNumber;

        public PBDTO(PB pb) {
            this.id = pb.getId();
            this.email = pb.getEmail();
            this.name = pb.getName();
            this.phoneNumber = pb.getPhoneNumber();
        }
    }

    @ApiModel(description = "투자자 리스트 데이터")
    @Getter
    @Setter
    public static class UserDTO {
        @ApiModelProperty(example = "1", value = "투자자 id")
        private Long id;

        @ApiModelProperty(example = "김투자@nate.com", value = "이메일")
        private String email;

        @ApiModelProperty(example = "김투자", value = "이름")
        private String name;

        @ApiModelProperty(example = "01012345678", value = "휴대폰 번호")
        private String phoneNumber;

        @ApiModelProperty(example = "true", value = "관리자 여부")
        private Boolean isAdmin;

        public UserDTO(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.name = user.getName();
            this.phoneNumber = user.getPhoneNumber();
            this.isAdmin = user.getRole() == Role.ADMIN ? true : false;
        }
    }

    @ApiModel(description = "회원수")
    @Getter
    @Setter
    public static class CountDTO {
        @ApiModelProperty(example = "7", value = "전체 회원수")
        private Long total;

        @ApiModelProperty(example = "4", value = "총 투자가 회원수")
        private Long user;

        @ApiModelProperty(example = "3", value = "총 PB 회원수")
        private Long pb;

        public CountDTO(Long user, Long pb) {
            this.total = user + pb;
            this.user = user;
            this.pb = pb;
        }
    }

    @ApiModel(description = "회원 관리 페이지 전체 가져오기 응답 데이터")
    @Getter
    @Setter
    public static class MemberOutDTO {
        @ApiModelProperty(example = "1", value = "투자자 id")
        private Long id;

        @ApiModelProperty(example = "2023-01-01", value = "가입일")
        private String createdAt;

        @ApiModelProperty(example = "김투자@nate.com", value = "이메일")
        private String email;

        @ApiModelProperty(example = "김투자", value = "이름")
        private String name;

        @ApiModelProperty(example = "01012345678", value = "휴대폰 번호")
        private String phoneNumber;

        @ApiModelProperty(example = "true", value = "관리자 여부(관리자면 true, 아니면 false(PB는 항상 false))")
        private Boolean isAdmin;

        public MemberOutDTO(Member member) {
            this.id = member.getId();
            this.createdAt = localDateTimeToString(member.getCreatedAt());
            this.email = member.getEmail();
            this.name = member.getName();
            this.phoneNumber = member.getPhoneNumber();
            this.isAdmin = member.getRole() == Role.ADMIN ? true : false;
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class PBPendingDTO {
        @ApiModelProperty(example = "4", value = "승인 대기 중인 pb id")
        private Long id;

        @ApiModelProperty(example = "윤pb@nate.com", value = "이메일")
        private String email;

        @ApiModelProperty(example = "윤pb", value = "이름")
        private String name;

        @ApiModelProperty(example = "01012345678", value = "핸드폰 번호")
        private String phoneNumber;

        @ApiModelProperty(example = "미래에셋증권 여의도점", value = "지점명")
        private String branchName;

        @ApiModelProperty(example = "10", value = "경력(연차)")
        private Integer career;

        @ApiModelProperty(example = "BOND", value = "전문분야1")
        private PBSpeciality speciality1;

        @ApiModelProperty(example = "null", value = "전문분야2")
        private PBSpeciality speciality2;

        @ApiModelProperty(example = "https://d23znr2pczcvf6.cloudfront.net/5fb1a367-f935-4905-a643-bbf8cd7afc19_%EB%8B%AC%ED%8C%BD%EC%9D%B4.jpg", value = "명함 사진")
        private String businessCard;

        public PBPendingDTO(PB pb, String branchName) {
            this.id = pb.getId();
            this.email = pb.getEmail();
            this.name = pb.getName();
            this.phoneNumber = pb.getPhoneNumber();
            this.branchName = branchName;
            this.career = pb.getCareer();
            this.speciality1 = pb.getSpeciality1();
            this.speciality2 = pb.getSpeciality2();
            this.businessCard = pb.getBusinessCard();
        }
    }

    @ApiModel(description = "공지사항 목록 가져오기 응답 데이터")
    @Getter
    @Setter
    public static class NoticeDTO {
        @ApiModelProperty(example = "1", value = "자주 묻는 질문 id")
        private Long id;

        @ApiModelProperty(example = "이메일이 주소가 변경되었어요.", value = "제목")
        private String title;

        @ApiModelProperty(example = "보다 나은 환경을 제공하기 위해 개발진에서 발견한 문제 복구 및 업데이트 점검을 진행할 예정입니다.\n" +
                "업데이트 점검을 진행하는 동안에는 접속할 수 없으니 불필요한 손해가 발생치 않도록 주의해 주세요.\n" +
                "이로 인해 불편을 끼쳐 드려 죄송합니다.", value = "내용")
        private String content;

        @ApiModelProperty(example = "2023-06-18", value = "작성 날짜")
        private LocalDate date;

        public NoticeDTO(Notice notice) {
            this.id = notice.getId();
            this.title = notice.getTitle();
            this.content = notice.getContent();
            this.date = notice.getCreatedAt().toLocalDate();
        }
    }

    @ApiModel(description = "자주 묻는 질문 목록 가져오기 응답 데이터")
    @Getter
    @Setter
    public static class FAQDTO {
        @ApiModelProperty(example = "1", value = "자주 묻는 질문 id")
        private Long id;

        @ApiModelProperty(example = "회원", value = "라벨")
        private String label;

        @ApiModelProperty(example = "이메일이 주소가 변경되었어요.", value = "제목")
        private String title;

        @ApiModelProperty(example = "가입 이메일은 회원 식별 고유 키로 가입 후 변경이 불가능 하므로 개인 이메일로 가입하기를 권유드립니다.", value = "내용")
        private String content;

        public FAQDTO(FrequentQuestion frequentQuestion) {
            this.id = frequentQuestion.getId();
            this.label = frequentQuestion.getLabel();
            this.title = frequentQuestion.getTitle();
            this.content = frequentQuestion.getContent();
        }
    }
}
