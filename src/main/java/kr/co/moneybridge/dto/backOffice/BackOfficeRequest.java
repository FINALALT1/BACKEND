package kr.co.moneybridge.dto.backOffice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.backoffice.FrequentQuestion;
import kr.co.moneybridge.model.backoffice.Notice;
import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.model.pb.Company;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class BackOfficeRequest {
    @ApiModel
    @Getter
    @Setter
    public static class UpdateBranchDTO {
        @ApiModelProperty(example = "1", value = "증권사 id")
        private Long companyId;

        @ApiModelProperty(example = "서울영업부", value = "지점명")
        private String name;

        @ApiModelProperty(example = "서울특별시 영등포구 국제금융로2길 24 BNK금융타워", value = "검색할 주소(도로명/지번)- 건물명까지만 검색됨. 몇 층 몇 호인지는 상세주소에 적기")
        private String address; // 주소

        @ApiModelProperty(example = "6층", value = "건물명 이후 주소(몇 층 몇 호)")
        private String specificAddress; // 상세주소
    }

    @ApiModel
    @Getter
    @Setter
    public static class AddFAQDTO {
        @ApiModelProperty(example = "회원", value = "유형")
        @NotEmpty
        private String label;

        @ApiModelProperty(example = "비밀번호를 잊어버렸어요", value = "제목")
        @NotEmpty
        private String title;

        @ApiModelProperty(example = "이메일 인증이 되면 새로운 비밀번호 설정이 가능합니다.", value = "내용")
        @NotEmpty
        private String content;

        public FrequentQuestion toEntity() {
            return FrequentQuestion.builder()
                    .label(label)
                    .title(title)
                    .content(content)
                    .build();
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class UpdateFAQDTO {
        @ApiModelProperty(example = "회원", value = "유형")
        @NotEmpty
        private String label;

        @ApiModelProperty(example = "비밀번호를 잊어버렸어요", value = "제목")
        @NotEmpty
        private String title;

        @ApiModelProperty(example = "이메일 인증이 되면 새로운 비밀번호 설정이 가능합니다.", value = "내용")
        @NotEmpty
        private String content;
    }

    @ApiModel
    @Getter
    @Setter
    public static class AddNoticeDTO {
        @ApiModelProperty(example = "서버 점검 안내", value = "제목")
        @NotEmpty
        private String title;

        @ApiModelProperty(example = "0시부터 3시까지 서버 점검시간입니다.", value = "내용")
        @NotEmpty
        private String content;

        public Notice toEntity() {
            return Notice.builder()
                    .title(title)
                    .content(content)
                    .build();
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class UpdateNoticeDTO {
        @ApiModelProperty(example = "서버 점검 안내", value = "제목")
        @NotEmpty
        private String title;

        @ApiModelProperty(example = "0시부터 3시까지 서버 점검시간입니다.", value = "내용")
        @NotEmpty
        private String content;
    }

    @ApiModel
    @Getter
    @Setter
    public static class BranchInDTO {
        @ApiModelProperty(example = "1", value = "증권사 id")
        @NotNull
        private Long companyId;

        @ApiModelProperty(example = "서울영업부", value = "지점명")
        @NotEmpty
        private String name;

        @ApiModelProperty(example = "서울특별시 영등포구 국제금융로2길 24 BNK금융타워", value = "검색할 주소(도로명/지번)- 건물명까지만 검색됨. 몇 층 몇 호인지는 상세주소에 적기")
        @NotEmpty
        private String address; // 주소

        @ApiModelProperty(example = "6층", value = "건물명 이후 주소(몇 층 몇 호)")
        private String specificAddress; // 상세주소

        public Branch toEntity(Company company, FullAddress address) {
            return Branch.builder()
                    .company(company)
                    .name(company.getName() + " " + name)
                    .roadAddress(address.getRoadAddress() + ", " + specificAddress)
                    .streetAddress(address.getStreetAddress() + ", " + specificAddress)
                    .latitude(address.getLatitude())
                    .longitude(address.getLongitude())
                    .build();
        }

        public Branch toDefaultEntity(Company company) { // 지점없이 온라인으로만 운영되는 증권사의 경우
            return Branch.builder()
                    .company(company)
                    .name(name)
                    .roadAddress("")
                    .streetAddress("")
                    .latitude(0.0)
                    .longitude(0.0)
                    .build();
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class CompanyInDTO {
        @ApiModelProperty(example = "KB증권", value = "증권사 이름")
        @NotBlank
        private String companyName;

        public Company toEntity(String logo) {
            return Company.builder()
                    .name(companyName)
                    .logo(logo)
                    .build();
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class UpdateCompanyDTO {
        @ApiModelProperty(example = "KB증권", value = "증권사 이름")
        private String companyName;
    }
}
