package kr.co.moneybridge.dto.pb;

import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.model.pb.Company;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBSpeciality;
import kr.co.moneybridge.model.user.UserPropensity;
import lombok.Getter;
import lombok.Setter;

public class PBResponse {
    @Setter
    @Getter
    public static class JoinOutDTO {
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
