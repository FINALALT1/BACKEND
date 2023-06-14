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
    @ApiModel(description = "증권사 리스트 목록 로고 불포함 응답 데이터")
    @Setter
    @Getter
    public static class BranchDTO {
        private Long id;
        private String name;
        private String roadAddress;
        private String streetAddress;

        public BranchDTO(Branch branch) {

            this.id = branch.getId();
            this.name = branch.getName();
            this.roadAddress = branch.getRoadAddress();
            this.streetAddress = branch.getStreetAddress();
        }
    }

    @Setter
    @Getter
    public static class BranchOutDTO {
        private List<BranchDTO> list;

        public BranchOutDTO(List<BranchDTO> list) {
            this.list = list;
        }
    }

    @Setter
    @Getter
    public static class CompanyNameDTO {
        @ApiModelProperty(example = "1", value = "증권사의 id")
        private Long id;
        @ApiModelProperty(example = "미래에셋증권", value = "증권사명")
        private String name;

        public CompanyNameDTO(Company company) {
            this.id = company.getId();
            this.name = company.getName();
        }
    }

    @ApiModel(description = "증권사 리스트 목록 로고 불포함 응답 데이터")
    @Setter
    @Getter
    public static class CompanyNameOutDTO {
        @ApiModelProperty
        private List<CompanyNameDTO> list;

        public CompanyNameOutDTO(List<CompanyNameDTO> list) {
            this.list = list;
        }
    }

    @ApiModel(description = "증권사 리스트 목록 로고 포함 응답 데이터")
    @Setter
    @Getter
    public static class CompanyDTO {
        @ApiModelProperty(example = "1", value = "증권사의 id")
        private Long id;
        @ApiModelProperty(example = "logo.png", value = "로고 이미지 주소")
        private String logo;
        @ApiModelProperty(example = "미래에셋증권", value = "증권사명")
        private String name;

        public CompanyDTO(Company company) {
            this.id = company.getId();
            this.logo = company.getLogo();
            this.name = company.getName();
        }
    }

    @ApiModel(description = "증권사 리스트 목록 로고 포함 응답 데이터")
    @Setter
    @Getter
    public static class CompanyOutDTO {
        @ApiModelProperty
        private List<CompanyDTO> list;

        public CompanyOutDTO(List<CompanyDTO> list) {
            this.list = list;
        }
    }

    @ApiModel(description = "PB 회원가입 성공시 응답 데이터")
    @Setter
    @Getter
    public static class JoinOutDTO {
        @ApiModelProperty(example = "1", value = "PB의 id")
        private Long id;

        public JoinOutDTO(PB pb) {
            this.id = pb.getId();
        }
    }

    @ApiModel(description = "PB 리스트 응답 데이터")
    @Getter
    @Setter
    public static class PBPageDTO {

        @ApiModelProperty(example = "1", value = "PB의 id")
        private Long id;
        @ApiModelProperty(example = "profile.png", value = "PB의 프로필사진")
        private String profile;
        @ApiModelProperty(example = "김피비", value = "PB의 이름")
        private String name;
        @ApiModelProperty(example = "미래에셋", value = "PB의 회사명")
        private String companyName;
        @ApiModelProperty(example = "여의도점", value = "PB의 지점명")
        private String branchName;
        @ApiModelProperty(example = "주식전문가 김피비입니다", value = "한줄메세지")
        private String msg;
        @ApiModelProperty(example = "ETF", value = "PB의 전문분야1")
        private PBSpeciality speciality1;
        @ApiModelProperty(example = "BOND", value = "PB의 전문분야2")
        private PBSpeciality speciality2;
        @ApiModelProperty(example = "5", value = "PB의 경력")
        private int career;
        @ApiModelProperty(example = "11", value = "PB 예약건수")
        private int reserveCount;
        @ApiModelProperty(example = "6", value = "PB 후기건수")
        private int reviewCount;
        @ApiModelProperty(example = "127.0000", value = "지점 latitude")
        private Double branchLat;
        @ApiModelProperty(example = "84.1111", value = "지점 longitude")
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

    @ApiModel(description = "메인페이지 PB 리스트 응답 데이터")
    @Getter
    public static class PBSimpleDTO {

        @ApiModelProperty(example = "1", value = "PB의 id")
        private Long id;
        @ApiModelProperty(example = "1", value = "PB의 이름")
        private String name;
        @ApiModelProperty(example = "1", value = "PB 한줄메세지")
        private String msg;
        @ApiModelProperty(example = "1", value = "PB의 프로필")
        private String profile;

        public PBSimpleDTO(PBPageDTO pbPageDTO) {
            this.id = pbPageDTO.getId();
            this.name = pbPageDTO.getName();
            this.msg = pbPageDTO.getMsg();
            this.profile = pbPageDTO.getProfile();
        }
    }
}
