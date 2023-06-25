package kr.co.moneybridge.dto.backOffice;

import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.model.pb.Company;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BackOfficeRequest {

    @Getter
    @Setter
    public static class BranchInDTO {
        @ApiModelProperty(example = "1")
        @NotNull
        private Long companyId;
        @ApiModelProperty(example = "서울영업부")
        @NotEmpty
        private String name;
        @ApiModelProperty(example = "서울특별시 영등포구 국제금융로2길 24, BNK금융타워 6층")
        private String address; // 주소

        public Branch toEntity(Company company, FullAddress address) {
            return Branch.builder()
                    .company(company)
                    .name(name)
                    .roadAddress(address.getRoadAddress())
                    .streetAddress(address.getStreetAddress())
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
}
