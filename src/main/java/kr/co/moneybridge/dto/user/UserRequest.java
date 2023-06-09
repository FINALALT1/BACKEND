package kr.co.moneybridge.dto.user;

import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserAgreement;
import kr.co.moneybridge.model.user.UserAgreementType;
import lombok.*;

import javax.validation.constraints.*;
import java.util.List;

public class UserRequest {
    @Setter
    @Getter
    public static class RePasswordInDTO {
        @NotNull
        private Long id;
        @NotNull
        private Role role;
        @NotEmpty
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$", message = "공백없이 영문(대소문자), 숫자 포함해서 8자 이상으로 작성해주세요")
        private String password;
    }

    @Setter
    @Getter
    public static class EmailFindInDTO {
        @NotNull
        private Role role;
        @NotEmpty
        private String name;
        @NotEmpty
        @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
                message = "유효하지 않은 휴대폰 번호 형식입니다")
        private String phoneNumber;
    }

    @Setter
    @Getter
    public static class PasswordInDTO {
        @ApiModelProperty(example = "USER")
        @NotNull
        private Role role;

        @ApiModelProperty(example = "김투자")
        @NotEmpty
        private String name;

        @ApiModelProperty(example = "jisu3148496@naver.com")
        @NotEmpty
        @Email(message = "이메일 형식으로 작성해주세요")
        private String email;
    }

    @Setter
    @Getter
    public static class EmailInDTO {
        @NotEmpty
        @Email(message = "이메일 형식으로 작성해주세요")
        private String email;
    }

    @Setter
    @Getter
    public static class WithdrawInDTO {
        @ApiModelProperty(example = "01012345678")
        @NotEmpty
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$", message = "공백없이 영문(대소문자), 숫자 포함해서 8자 이상으로 작성해주세요")
        private String password;
    }

    @Setter
    @Getter
    public static class AgreementDTO {
        @ApiModelProperty(example = "돈줄 이용약관 동의")
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

    @Setter
    @Getter
    public static class JoinInDTO {
        @ApiModelProperty(example = "investor2@naver.com")
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
                    .build();
        }
    }

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
