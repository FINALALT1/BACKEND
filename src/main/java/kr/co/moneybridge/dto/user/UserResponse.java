package kr.co.moneybridge.dto.user;

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
    public static class LoginOutDTO {
        private Long id;

        public LoginOutDTO(User user) {
            this.id = user.getId();
        }
    }

    @Setter
    @Getter
    public static class JoinUserOutDTO {
        private Long id;

        public JoinUserOutDTO(User user) {
            this.id = user.getId();
        }
    }
}
