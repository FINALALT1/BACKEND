package kr.co.moneybridge.dto.pb;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.model.pb.Company;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBSpeciality;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PBResponse {
    @ApiModel(description = "PB 회원가입 완료시 응답 데이터")
    @Setter
    @Getter
    public static class CompanyNameDTO {
        private Long id;
        private String name;

        public CompanyNameDTO(Company company) {

            this.id = company.getId();
            this.name = company.getName();
        }
    }

    @Setter
    @Getter
    public static class CompanyNameOutDTO {
        private List<CompanyNameDTO> list;

        public CompanyNameOutDTO(List<CompanyNameDTO> list) {

            this.list = list;
        }
    }

    @Setter
    @Getter
    public static class CompanyDTO {
        private Long id;
        private String logo;
        private String name;

        public CompanyDTO(Company company) {

            this.id = company.getId();
            this.logo = company.getLogo();
            this.name = company.getName();
        }
    }

    @Setter
    @Getter
    public static class CompanyOutDTO {
        private List<CompanyDTO> list;

        public CompanyOutDTO(List<CompanyDTO> list) {

            this.list = list;
        }
    }

    @Setter
    @Getter
    public static class JoinOutDTO {
        @ApiModelProperty(example = "1", value = "PB의 id")
        private Long id;

        public JoinOutDTO(PB pb) {
            this.id = pb.getId();
        }
    }

    @Getter
    @Setter
    public static class PBPageDTO {

        private Long id;
        private String profile;
        private String name;
        private String companyName;
        private String branchName;
        private String msg;
        private PBSpeciality speciality1;
        private PBSpeciality speciality2;
        private int career;
        private int reserveCount;
        private int reviewCount;
        private Double branchLat;
        private Double branchLon;

        public PBPageDTO(PB pb, Branch branch, Company company) {
            this.id = pb.getId();
            this.profile = pb.getProfile();
            this.name = pb.getName();
            this.companyName = company.getName();
            this.branchName = branch.getName();
            this.msg = pb.getMsg();
            this.career = pb.getCareer();
            this.speciality1 = pb.getSpeciality1();
            this.speciality2 = pb.getSpeciality2();
            this.branchLat = branch.getLatitude();
            this.branchLon = branch.getLongitude();
        }
    }
}
