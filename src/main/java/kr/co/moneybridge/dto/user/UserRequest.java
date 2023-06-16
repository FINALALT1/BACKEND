package kr.co.moneybridge.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.core.annotation.ValidNumbers;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserAgreement;
import kr.co.moneybridge.model.user.UserAgreementType;
import kr.co.moneybridge.model.user.UserInvestInfo;
import lombok.*;

import javax.validation.constraints.*;
import java.util.List;

public class UserRequest {
    @ApiModel(description = "투자자 성향 변경시 요청 데이터 (변경할 것만 보내기)")
    @Setter
    @Getter
    public static class UpdatePropensityInDTO {
        @ApiModelProperty(example = "5", value = "1번 문항의 점수. 2, 3, 4, 5 중 하나")
        @ValidNumbers(values = {2, 3, 4, 5}, message = "2, 3, 4, 5 중에 하나만 입력하세요")
        private Integer q1;

        @ApiModelProperty(example = "5", value = "2번 문항의 점수. 2, 3, 4 중 하나")
        @ValidNumbers(values = {2, 3, 4}, message = "2, 3, 4 중에 하나만 입력하세요")
        private Integer q2;

        @ApiModelProperty(example = "5", value = "3번 문항의 점수. 1, 3, 4, 5 중 하나")
        @ValidNumbers(values = {1, 3, 4, 5}, message = "1, 3, 4, 5 중에 하나만 입력하세요")
        private Integer q3;

        @ApiModelProperty(example = "5", value = "4번 문항의 점수. 1, 3, 4, 5 중 하나")
        @ValidNumbers(values = {1, 3, 4, 5}, message = "1, 3, 4, 5 중에 하나만 입력하세요")
        private Integer q4;

        @ApiModelProperty(example = "5", value = "5번 문항의 점수. 2, 3, 4, 5 중 하나")
        @ValidNumbers(values = {2, 3, 4, 5}, message = "2, 3, 4, 5 중에 하나만 입력하세요")
        private Integer q5;

        @ApiModelProperty(example = "5", value = "6번 문항의 점수. 1, 2, 4, 5 중 하나")
        @ValidNumbers(values = {1, 2, 4, 5}, message = "1, 2, 4, 5 중에 하나만 입력하세요")
        private Integer q6;
    }

    @ApiModel(description = "투자자 성향 체크시 요청 데이터")
    @Setter
    @Getter
    public static class TestPropensityInDTO {

        @ApiModelProperty(example = "5", value = "1번 문항의 점수. 2, 3, 4, 5 중 하나")
        @NotNull
        @ValidNumbers(values = {2, 3, 4, 5}, message = "2, 3, 4, 5 중에 하나만 입력하세요")
        private Integer q1;

        @ApiModelProperty(example = "5", value = "2번 문항의 점수. 2, 3, 4 중 하나")
        @NotNull
        @ValidNumbers(values = {2, 3, 4}, message = "2, 3, 4 중에 하나만 입력하세요")
        private Integer q2;

        @ApiModelProperty(example = "5", value = "3번 문항의 점수. 1, 3, 4, 5 중 하나")
        @NotNull
        @ValidNumbers(values = {1, 3, 4, 5}, message = "1, 3, 4, 5 중에 하나만 입력하세요")
        private Integer q3;

        @ApiModelProperty(example = "5", value = "4번 문항의 점수. 1, 3, 4, 5 중 하나")
        @NotNull
        @ValidNumbers(values = {1, 3, 4, 5}, message = "1, 3, 4, 5 중에 하나만 입력하세요")
        private Integer q4;

        @ApiModelProperty(example = "5", value = "5번 문항의 점수. 2, 3, 4, 5 중 하나")
        @NotNull
        @ValidNumbers(values = {2, 3, 4, 5}, message = "2, 3, 4, 5 중에 하나만 입력하세요")
        private Integer q5;

        @ApiModelProperty(example = "5", value = "6번 문항의 점수. 1, 2, 4, 5 중 하나")
        @NotNull
        @ValidNumbers(values = {1, 2, 4, 5}, message = "1, 2, 4, 5 중에 하나만 입력하세요")
        private Integer q6;

        public UserInvestInfo toEntity(User user) {
            return UserInvestInfo.builder()
                    .user(user)
                    .q1(q1)
                    .q2(q2)
                    .q3(q3)
                    .q4(q4)
                    .q5(q5)
                    .q6(q6)
                    .build();
        }
    }

    @ApiModel(description = "개인 정보 수정시 요청 데이터")
    @Setter
    @Getter
    public static class UpdateMyInfoInDTO {

        @ApiModelProperty(example = "김투자")
        private String name;

        @ApiModelProperty(example = "01011119999")
        @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
                message = "유효하지 않은 휴대폰 번호 형식입니다")
        private String phoneNumber;
    }

    @ApiModel(description = "개인정보 수정시 비밀번호 확인 요청 데이터")
    @Setter
    @Getter
    public static class CheckPasswordInDTO {
        @ApiModelProperty(example = "password1234")
        @NotEmpty
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$", message = "공백없이 영문(대소문자), 숫자 포함해서 8자 이상으로 작성해주세요")
        private String password;
    }

    @ApiModel(description = "비밀번호 재설정시 요청 데이터")
    @Setter
    @Getter
    public static class RePasswordInDTO {

        @ApiModelProperty(example = "1")
        @NotNull
        private Long id;

        @ApiModelProperty(example = "USER")
        @NotNull
        private Role role;

        @ApiModelProperty(example = "password12345")
        @NotEmpty
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$", message = "공백없이 영문(대소문자), 숫자 포함해서 8자 이상으로 작성해주세요")
        private String password;
    }

    @ApiModel(description = "이메일 찾기시 요청 데이터")
    @Setter
    @Getter
    public static class EmailFindInDTO {
        @ApiModelProperty(example = "USER")
        @NotNull
        private Role role;

        @ApiModelProperty(example = "김투자")
        @NotEmpty
        private String name;

        @ApiModelProperty(example = "01012345678")
        @NotEmpty
        @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
                message = "유효하지 않은 휴대폰 번호 형식입니다")
        private String phoneNumber;
    }

    @ApiModel(description = "비밀번호 찾기시 이메일 인증 요청 데이터")
    @Setter
    @Getter
    public static class PasswordInDTO {
        @ApiModelProperty(example = "USER")
        @NotNull
        private Role role;

        @ApiModelProperty(example = "사용자")
        @NotEmpty
        private String name;

        @ApiModelProperty(example = "jisu8496@naver.com")
        @NotEmpty
        @Email(message = "이메일 형식으로 작성해주세요")
        private String email;
    }

    @ApiModel(description = "이메일 인증시 요청 데이터")
    @Setter
    @Getter
    public static class EmailInDTO {
        @ApiModelProperty(example = "jisu8496@naver.com")
        @NotEmpty
        @Email(message = "이메일 형식으로 작성해주세요")
        private String email;
    }

    @ApiModel(description = "탈퇴시 요청 데이터")
    @Setter
    @Getter
    public static class WithdrawInDTO {
        @ApiModelProperty(example = "password1234")
        @NotEmpty
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$", message = "공백없이 영문(대소문자), 숫자 포함해서 8자 이상으로 작성해주세요")
        private String password;
    }

    @ApiModel(description = "투자자 회원가입시 약관 데이터")
    @Setter
    @Getter
    public static class AgreementDTO {
        @ApiModelProperty(example = "돈줄 이용약관 동의", value = "약관명")
        @NotEmpty
        private String title;

        @ApiModelProperty(example = "REQUIRED")
        @NotNull
        private UserAgreementType type;

        @ApiModelProperty(example = "true")
        @NotNull
        private Boolean isAgreed;

        public UserAgreement toEntity(User user) {
            return UserAgreement.builder()
                    .user(user)
                    .title(title)
                    .type(type)
                    .isAgreed(isAgreed)
                    .build();
        }
    }

    @ApiModel(description = "투자자 회원가입시 요청 데이터")
    @Setter
    @Getter
    public static class JoinInDTO {
        @ApiModelProperty(example = "investor2@naver.com", value = "이메일 형식, 30바이트 초과 x")
        @NotEmpty
        @Email(message = "이메일 형식으로 작성해주세요")
        @Size(max = 30, message = "이메일은 30바이트를 초과할 수 없습니다")
        private String email;

        @ApiModelProperty(example = "kang1234")
        @NotEmpty
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$", message = "공백없이 영문(대소문자), 숫자 포함해서 8자 이상으로 작성해주세요")
        private String password;

        @ApiModelProperty(example = "강투자")
        @NotEmpty
        @Size(max = 20, message = "이름은 20바이트를 초과할 수 없습니다")
        private String name;

        @ApiModelProperty(example = "01012345678")
        @NotEmpty
        @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
                message = "유효하지 않은 휴대폰 번호 형식입니다")
        private String phoneNumber;

        @ApiModelProperty
        private List<AgreementDTO> agreements;

        public User toEntity() {
            return User.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .phoneNumber(phoneNumber)
                    .role(Role.USER)
                    .profile("profile.png")
                    .hasDoneReview(false)
                    .hasDoneReservation(false)
                    .hasDoneBoardBookmark(false)
                    .build();
        }
    }

    @ApiModel(description = "로그인시 요청 데이터")
    @Setter
    @Getter
    public static class LoginInDTO {
        @ApiModelProperty(example = "USER")
        @NotNull
        private Role role;

        @ApiModelProperty(example = "investor2@naver.com")
        @NotEmpty
        @Email(message = "이메일 형식으로 작성해주세요")
        private String email;

        @ApiModelProperty(example = "kang1234")
        @NotEmpty
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$", message = "공백없이 영문(대소문자), 숫자 포함해서 8자 이상으로 작성해주세요")
        private String password;
    }
}
