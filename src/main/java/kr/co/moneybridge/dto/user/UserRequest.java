package kr.co.moneybridge.dto.user;

import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserRequest {
    @Setter
    @Getter
    public static class LoginInDTO {
        @Pattern(regexp = "^[a-zA-Z0-9]{2,20}$", message = "영문/숫자 2~20자 이내로 작성해주세요")
        @NotEmpty
        private String name;
        @NotEmpty
        @Size(min = 4, max = 20)
        private String password;
    }

    @Setter
    @Getter
    public static class JoinInDTO {
        @Pattern(regexp = "^[a-zA-Z0-9]{2,20}$", message = "영문/숫자 2~20자 이내로 작성해주세요")
        @NotEmpty
        private String username;

        @NotEmpty
        @Size(min = 4, max = 20)
        private String password;

        @NotEmpty
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        private String email;


        public User toEntity() {
            return User.builder()
                    .name(username)
                    .password(password)
                    .email(email)
                    .role(Role.USER)
                    .status(true)
                    .build();
        }
    }
}
