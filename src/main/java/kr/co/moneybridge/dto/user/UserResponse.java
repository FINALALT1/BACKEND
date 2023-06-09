package kr.co.moneybridge.dto.user;

import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

public class UserResponse {
    @Getter @Setter
    public static class DetailOutDTO{
        private Long id;
        private String username;
        private String email;
        private Role role;

        public DetailOutDTO(User user) {
            this.id = user.getId();
            this.username = user.getName();
            this.email = user.getEmail();
            this.role = user.getRole();
        }
    }

    @Setter
    @Getter
    public static class PasswordOutDTO {
        private Long id;
        private Role role;
        private String name;
        private String phoneNumber;
        private String email;
        private String code;

        public PasswordOutDTO(Member member, String code) {
            this.id = member.getId();
            this.role = member.getRole();
            this.name = member.getName();
            this.email = member.getEmail();
            this.phoneNumber = member.getPhoneNumber();
            this.code = code;
        }
    }

    @Setter
    @Getter
    public static class EmailOutDTO {
        private String code;
        public EmailOutDTO(String code){
            this.code = code;
        }
    }

    @Setter
    @Getter
    public static class JoinOutDTO {
        @ApiModelProperty(example = "1")
        private Long id;

        public JoinOutDTO(User user) {
            this.id = user.getId();
        }
    }

    @Setter
    @Getter
    public static class LoginOutDTO {
        @ApiModelProperty(example = "1")
        private Long id;

        public LoginOutDTO(Member member) {
            this.id = member.getId();
        }
    }
}
