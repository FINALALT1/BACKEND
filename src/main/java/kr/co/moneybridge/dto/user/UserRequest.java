package kr.co.moneybridge.dto.user;

import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserAgreement;
import kr.co.moneybridge.model.user.UserAgreementType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

public class UserRequest {
    @Setter
    @Getter
    public static class LoginInDTO {
        @NotNull
        private Role role;

        @NotEmpty
        @Email(message = "이메일 형식으로 작성해주세요")
        private String email;

        @NotEmpty
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$", message = "공백없이 영문(대소문자), 숫자 포함해서 8자 이상으로 작성해주세요")
        private String password;
    }

    @Setter
    @Getter
    @Builder
    public static class AgreementDTO {
        @NotEmpty
        private String title;

        @NotEmpty
        private UserAgreementType type;

        @NotEmpty
        private Boolean isAgreed;

        public UserAgreement toEntity(User user) {
            return UserAgreement.builder()
                    .user(user)
                    .title(title)
                    .type(type)
                    .isAgreed(isAgreed)
                    .status(true)
                    .build();
        }
    }

    @Setter
    @Getter
    public static class JoinUserInDTO {
        @NotEmpty
        @Email(message = "이메일 형식으로 작성해주세요")
        private String email;

        @NotEmpty
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$", message = "공백없이 영문(대소문자), 숫자 포함해서 8자 이상으로 작성해주세요")
        private String password;

        @NotEmpty
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$", message = "공백없이 영문(대소문자), 숫자 포함해서 8자 이상으로 작성해주세요")
        private String checkPassword;

        @NotEmpty
        private String name;

        @NotEmpty
        @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
                message = "유효하지 않은 휴대폰 번호 형식입니다")
        private String phoneNumber;

        private List<AgreementDTO> agreements;

        public User toEntity() {
            return User.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .phoneNumber(phoneNumber)
                    .role(Role.ROLE_USER)
                    .status(true)
                    .build();
        }
    }
}
