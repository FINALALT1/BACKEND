package kr.co.moneybridge.dto.pb;

import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.pb.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

public class PBRequest {
    @Setter
    @Getter
    @Builder
    public static class AgreementDTO {
        @NotEmpty
        private String title;

        @NotNull
        private PBAgreementType type;

        private Boolean isAgreed;

        public PBAgreement toEntity(PB pb) {
            return PBAgreement.builder()
                    .pb(pb)
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

        @NotNull
        private Long branchId;

        @NotNull
        @PositiveOrZero(message = "값은 양수 또는 0이어야 합니다")
        private Integer career;

        @NotNull
        private PBSpeciality speciality1;

        private PBSpeciality speciality2;

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
