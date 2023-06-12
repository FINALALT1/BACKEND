package kr.co.moneybridge.dto.user;

import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class UserResponse {

    @Setter
    @Getter
    public static class MyInfoOutDTO {
        private String name;
        private String phoneNumber;
        private String email;

        public MyInfoOutDTO(Member member) {
            this.name = member.getName();
            this.email = member.getEmail();
            this.phoneNumber = member.getPhoneNumber();
        }
    }

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
    }

    @Setter
    @Getter
    public static class PasswordOutDTO {
        @ApiModelProperty(example = "1")
        private Long id;

        @ApiModelProperty(example = "USER")
        private Role role;

        @ApiModelProperty(example = "김투자")
        private String name;

        @ApiModelProperty(example = "01012345678")
        private String phoneNumber;

        @ApiModelProperty(example = "김투자@nate.com")
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
    }

    @Setter
    @Getter
    public static class EmailOutDTO {
        @ApiModelProperty(example = "J46L4SBJ")
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
