package kr.co.moneybridge.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserPropensity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class UserResponse {
    @ApiModel(description = "로그인 계정 정보 응답 데이터")
    @Setter
    @Getter
    public static class AccountOutDTO {
        @ApiModelProperty(example = "id")
        private Long id;
        @ApiModelProperty(example = "USER", value = "관리자도 USER로 줌")
        private Role role;
        @ApiModelProperty(example = "김투자")
        private String name;
        @ApiModelProperty(example = "AGGRESSIVE", value = "투자자는 항상 null")
        private UserPropensity propensity;
        @ApiModelProperty(example = "false", value = "관리자 여부")
        private Boolean isAdmin;

        public AccountOutDTO(Member member) {
            this.id = member.getId();
            this.role = member.getRole() == Role.PB ? Role.PB : Role.USER;
            this.name = member.getName();
            this.propensity = member.getPropensity();
            this.isAdmin = member.getRole() == Role.ADMIN ? true : false;
        }
    }

    @ApiModel
    @Setter
    @Getter
    public static class BookmarkDTO {
        @ApiModelProperty(example = "1", value = "id - 두 개만 보여줌")
        private Long id;
        @ApiModelProperty(example = "thumbnail.png", value = "이미지 - 두 개만 보여줌")
        private String thumbnail;

        public BookmarkDTO(Board board) {
            this.id = board.getId();
            this.thumbnail = board.getThumbnail();
        }
        public BookmarkDTO(PB pb) { // 오버로딩
            this.id = pb.getId();
            this.thumbnail = pb.getProfile();
        }
    }

    @ApiModel
    @Setter
    @Getter
    public static class BookmarkListDTO {
        @ApiModelProperty
        private List<BookmarkDTO> list;
        @ApiModelProperty(example = "0", value = "북마크한 개수")
        private Long count;

        public BookmarkListDTO(List<BookmarkDTO> list, Long count) {
            this.list = list;
            this.count = count;
        }
    }

    @ApiModel
    @Setter
    @Getter
    public static class ReservationCountDTO {
        @ApiModelProperty(example = "0", value = "예약 신청 개수")
        private Long apply;
        @ApiModelProperty(example = "0", value = "예약 확정 개수")
        private Long confirm;
        @ApiModelProperty(example = "0", value = "상담 완료 개수")
        private Long complete;

        public ReservationCountDTO(Long apply, Long comfirm, Long complete) {
            this.apply = apply;
            this.confirm = comfirm;
            this.complete = complete;
        }
    }

    @ApiModel
    @Setter
    @Getter
    public static class StepDTO {
        @ApiModelProperty(example = "true", value = "투자 성향 검사 한 적 있는지")
        private Boolean hasDonePropensity;
        @ApiModelProperty(example = "false", value = "콘텐츠 북마크 한 적 있는지")
        private Boolean hasDoneBoardBookmark;
        @ApiModelProperty(example = "false", value = "상담 예약 신청한 적있는지")
        private Boolean hasDoneReservation;
        @ApiModelProperty(example = "false", value = "후기작성 완료한 적 있는지")
        private Boolean hasDoneReview;

        public StepDTO(User user) {
            this.hasDonePropensity = user.getPropensity() == null ? false : true;
            this.hasDoneBoardBookmark = user.getHasDoneBoardBookmark();
            this.hasDoneReservation = user.getHasDoneReservation();
            this.hasDoneReview = user.getHasDoneReview();
        }
    }

    @ApiModel(description = "투자자 마이페이지 가져오기시 응답 데이터")
    @Setter
    @Getter
    public static class MyPageOutDTO {
        @ApiModelProperty(example = "1", value = "투자자 id")
        private Long id;
        @ApiModelProperty(example = "김투자", value = "투자자 이름")
        private String name;
        @ApiModelProperty(example = "AGGRESSIVE", value = "투자 성향")
        private UserPropensity propensity;
        @ApiModelProperty
        private StepDTO step;
        @ApiModelProperty
        private ReservationCountDTO reservationCount;
        @ApiModelProperty
        private BookmarkListDTO boardBookmark;
        @ApiModelProperty
        private BookmarkListDTO pbBookmark;

        public MyPageOutDTO(User user, StepDTO step,ReservationCountDTO reservationCount,
                                BookmarkListDTO boardBookmark, BookmarkListDTO pbBookmark) {
            this.id = user.getId();
            this.name = user.getName();
            this.propensity = user.getPropensity();
            this.step = step;
            this.reservationCount = reservationCount;
            this.boardBookmark = boardBookmark;
            this.pbBookmark = pbBookmark;
        }
    }

    @ApiModel(description = "개인 정보 가져오기시 응답 데이터")
    @Setter
    @Getter
    public static class MyInfoOutDTO {
        @ApiModelProperty(example = "김투자")
        private String name;
        @ApiModelProperty(example = "01012345678")
        private String phoneNumber;
        @ApiModelProperty(example = "김투자@nate.com")
        private String email;

        public MyInfoOutDTO(Member member) {
            this.name = member.getName();
            this.email = member.getEmail();
            this.phoneNumber = member.getPhoneNumber();
        }
    }

    @ApiModel(description = "이메일 찾기시 응답 데이터")
    @Setter
    @Getter
    public static class EmailFindOutDTO {
        @ApiModelProperty(example = "김투자")
        private String name;

        @ApiModelProperty(example = "01012345678")
        private String phoneNumber;

        @ApiModelProperty(example = "김투자@nate.com")
        private String email;

        public EmailFindOutDTO(Member member) {
            this.name = member.getName();
            this.email = member.getEmail();
            this.phoneNumber = member.getPhoneNumber();
        }

        public EmailFindOutDTO() {
            this.name = null;
            this.email = null;
            this.phoneNumber = null;
        }
    }

    @ApiModel(description = "비밀번호 찾기시 이메일 인증 응답 데이터")
    @Setter
    @Getter
    public static class PasswordOutDTO {
        @ApiModelProperty(example = "4")
        private Long id;

        @ApiModelProperty(example = "USER")
        private Role role;

        @ApiModelProperty(example = "사용자")
        private String name;

        @ApiModelProperty(example = "01012345678")
        private String phoneNumber;

        @ApiModelProperty(example = "jisu8496@naver.com")
        private String email;

        @ApiModelProperty(example = "J46L4SBJ")
        private String code;

        public PasswordOutDTO(Member member, String code) {
            this.id = member.getId();
            this.role = member.getRole();
            this.name = member.getName();
            this.email = member.getEmail();
            this.phoneNumber = member.getPhoneNumber();
            this.code = code;
        }

        public PasswordOutDTO() {}
    }

    @ApiModel(description = "이메일 인증시 응답 데이터")
    @Setter
    @Getter
    public static class EmailOutDTO {
        @ApiModelProperty(example = "J46L4SBJ")
        private String code;
        public EmailOutDTO(String code){
            this.code = code;
        }
    }

    @ApiModel(description = "휴대폰 번호 중복 체크 응답 데이터")
    @Setter
    @Getter
    public static class PhoneNumberOutDTO {
        @ApiModelProperty(example = "true", value = "중복 여부(true or false)")
        private boolean isDuplicated;

        public PhoneNumberOutDTO(boolean isDuplicated) {
            this.isDuplicated = isDuplicated;
        }
    }

    @ApiModel(description = "투자자 회원 가입시 응답 데이터")
    @Setter
    @Getter
    public static class JoinOutDTO {
        @ApiModelProperty(example = "1")
        private Long id;

        public JoinOutDTO(User user) {
            this.id = user.getId();
        }
    }

    @ApiModel(description = "백오피스 로그인시 응답 데이터")
    @Setter
    @Getter
    public static class BackOfficeLoginOutDTO {
        @ApiModelProperty(example = "1")
        private Long id;

        @ApiModelProperty(example = "김투자")
        private String name;

        @ApiModelProperty(example = "YFOEC1AC", value = "인증코드")
        private String code;

        public BackOfficeLoginOutDTO(Member member, String code) {
            this.id = member.getId();
            this.name = member.getName();
            this.code = code;
        }
    }

    @ApiModel(description = "로그인시 응답 데이터")
    @Setter
    @Getter
    public static class LoginOutDTO {
        @ApiModelProperty(example = "1")
        private Long id;

        @ApiModelProperty(example = "김투자")
        private String name;

        @ApiModelProperty(example = "false", value = "관리자 여부")
        private Boolean isAdmin;

        public LoginOutDTO(Member member, Boolean isAdmin) {
            this.id = member.getId();
            this.name = member.getName();
            this.isAdmin = isAdmin;
        }
    }
}
