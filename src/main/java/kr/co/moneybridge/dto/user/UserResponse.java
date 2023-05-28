package kr.co.moneybridge.dto.user;

import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

public class UserResponse {
    @Getter @Setter
    public static class DetailOutDTO{
        private Long id;
        private String username;
        private String email;
        private String role;

        public DetailOutDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.role = user.getRole();
        }
    }

    @Setter
    @Getter
    public static class JoinOutDTO {
        private Long id;
        private String username;

        public JoinOutDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
        }
    }
}
