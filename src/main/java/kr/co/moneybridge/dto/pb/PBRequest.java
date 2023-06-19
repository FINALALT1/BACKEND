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

    @ApiModel(description = "PB 프로필 수정 시 요청")
    @Getter
    @Setter
    public static class UpdateProfileInDTO {
        @ApiModelProperty(example = "미래에셋", value = "회사명")
        private String company;
        @ApiModelProperty(example = "용산점", value = "지점명")
        private String branchName;
        @ApiModelProperty(example = "10", value = "경력(연차)")
        private Integer career;
        @ApiModelProperty
        private List<CareerInDTO> careers;
        @ApiModelProperty
        private List<AwardInDTO> awards;
        @ApiModelProperty(example = "ETF", value = "전문분야1")
        private PBSpeciality speciality1;
        @ApiModelProperty(example = "BOND", value = "전문분야2")
        private PBSpeciality speciality2;
        @ApiModelProperty(example = "10.11", value = "누적수익률")
        private Double cumulativeReturn;
        @ApiModelProperty(example = "10.11", value = "최대자본인하율")
        private Double maxDrawdown;
        @ApiModelProperty(example = "1.1", value = "profitFactor")
        private Double profitFactor;
        @ApiModelProperty(example = "10.11", value = "평균손익률")
        private Double averageProfit;
        @ApiModelProperty(example = "안녕하세요, 김피비입니다.", value = "한줄소개")
        private String intro;
        @ApiModelProperty(example = "10", value = "한줄메세지")
        private String msg;
        @ApiModelProperty(example = "false", value = "포트폴리오삭제여부")
        private Boolean deletePortfolio;
        @ApiModelProperty(example = "false", value = "프로필삭제여부")
        private Boolean deleteProfile;

        public Portfolio portfolioEntity(PB pb) {
            return Portfolio.builder()
                    .pb(pb)
                    .cumulativeReturn(cumulativeReturn)
                    .maxDrawdown(maxDrawdown)
                    .profitFactor(profitFactor)
                    .averageProfit(averageProfit)
                    .build();
        }

    }

    @Getter
    @Setter
    public static class CareerInDTO {
        private String content;
        private Integer start;
        private Integer end;

        public Career toEntity(PB pb) {
            return Career.builder()
                    .pb(pb)
                    .startYear(start)
                    .endYear(end)
                    .career(content)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class AwardInDTO {
        private String record;
        private Integer awardYear;

        public Award toEntity(PB pb) {
            return Award.builder()
                    .pb(pb)
                    .record(record)
                    .awardYear(awardYear)
                    .build();
        }
    }

}
