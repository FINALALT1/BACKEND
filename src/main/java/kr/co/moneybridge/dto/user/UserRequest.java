package kr.co.moneybridge.dto.user;

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
    public static class WithdrawInDTO {
        @NotEmpty
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$", message = "공백없이 영문(대소문자), 숫자 포함해서 8자 이상으로 작성해주세요")
        private String password;
    }

    @Setter
    @Getter
    public static class AgreementDTO {
        @NotEmpty
        private String title;

        @NotNull
        private UserAgreementType type;

        @NotNull
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
    public static class JoinInDTO {
        @NotEmpty
        @Email(message = "이메일 형식으로 작성해주세요")
        @Size(max = 30, message = "이메일은 30바이트를 초과할 수 없습니다")
        private String email;

        @NotEmpty
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$", message = "공백없이 영문(대소문자), 숫자 포함해서 8자 이상으로 작성해주세요")
        private String password;

        @NotEmpty
        @Size(max = 20, message = "이름은 20바이트를 초과할 수 없습니다")
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
                    .role(Role.USER)
                    .profile("profile.png")
                    .status(true)
                    .build();
        }
    }

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
}
