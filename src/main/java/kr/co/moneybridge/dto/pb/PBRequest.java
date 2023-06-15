package kr.co.moneybridge.dto.pb;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.pb.*;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

public class PBRequest {
    @Setter
    @Getter
    public static class AgreementDTO {
        @ApiModelProperty(example = "돈줄 이용약관 동의")
        @NotEmpty
        private String title;

        @ApiModelProperty(example = "REQUIRED", value = "필수, 선택")
        @NotNull
        private PBAgreementType type;

        @ApiModelProperty(example = "true")
        private Boolean isAgreed;

        public PBAgreement toEntity(PB pb) {
            return PBAgreement.builder()
                    .pb(pb)
                    .title(title)
                    .type(type)
                    .isAgreed(isAgreed)
                    .build();
        }
    }

    @ApiModel(description = "PB 회원가입시 요청")
    @Setter
    @Getter
    public static class JoinInDTO {
        @ApiModelProperty(example = "jisu3148496@naver.com", value = "이메일 형식, 30바이트 초과하면 안됨, PB내 중복 불가")
        @NotEmpty
        @Email(message = "이메일 형식으로 작성해주세요")
        @Size(max = 30, message = "이메일은 30바이트를 초과할 수 없습니다")
        private String email;

        @ApiModelProperty(example = "password1234", value = "공백없이 영문(대소문자), 숫자 포함해서 8자 이상")
        @NotEmpty
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$", message = "공백없이 영문(대소문자), 숫자 포함해서 8자 이상으로 작성해주세요")
        private String password;

        @ApiModelProperty(example = "김이름", value = "20바이트 초과하면 안됨")
        @NotEmpty
        @Size(max = 20, message = "이름은 20바이트를 초과할 수 없습니다")
        private String name;

        @ApiModelProperty(example = "01011119999", value = "-없이 입력, 01로 시작 + 6-9사이 숫자하나 + 숫자 3개나 4개 + 숫자 4개")
        @NotEmpty
        @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
                message = "유효하지 않은 휴대폰 번호 형식입니다")
        private String phoneNumber;

        @ApiModelProperty(example = "1")
        @NotNull
        private Long branchId;

        @ApiModelProperty(example = "10", value = "경력(연차)")
        @NotNull
        @PositiveOrZero(message = "값은 양수 또는 0이어야 합니다")
        private Integer career;

        @ApiModelProperty(example = "BOND", value = "전문분야1")
        @NotNull
        private PBSpeciality speciality1;

        @ApiModelProperty(example = "FUND", value = "전문분야2")
        private PBSpeciality speciality2;

        @ApiModelProperty
        private List<AgreementDTO> agreements;

        public PB toEntity(Branch branch, String businessCard) {
            return PB.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .phoneNumber(phoneNumber)
                    .branch(branch)
                    .career(career)
                    .speciality1(speciality1)
                    .speciality2(speciality2)
                    .businessCard(businessCard)
                    .profile("person.png") // 기본 이미지
                    .role(Role.PB)
                    .status(PBStatus.PENDING)
                    .build();
        }
    }
}
