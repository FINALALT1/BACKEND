package kr.co.moneybridge.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class UserResponse {
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

    @ApiModel(description = "로그인시 응답 데이터")
    @Setter
    @Getter
    public static class LoginOutDTO {
        @ApiModelProperty(example = "1")
        private Long id;

        @ApiModelProperty(example = "J46L4SBJ")
        private String code;

        public LoginOutDTO(Member member) {
            this.id = member.getId();
        }
        public LoginOutDTO(Member member, String code) {
            this.id = member.getId();
            this.code = code;
        }
    }
}
