package kr.co.moneybridge.dto.pb;

import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.model.pb.Company;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBSpeciality;
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
        private String msg;
        private int career;
        private PBSpeciality speciality1;
        private PBSpeciality speciality2;
        private String roadAddress;
        private String streetAddress;
        private int reserveCount;
        private int reviewCount;

        public PBPageDTO(PB pb, Branch branch, Company company) {
            this.id = pb.getId();
            this.profile = pb.getProfile();
            this.name = pb.getName();
            this.companyName = company.getName();
            this.msg = pb.getMsg();
            this.career = pb.getCareer();
            this.speciality1 = pb.getSpeciality1();
            this.speciality2 = pb.getSpeciality2();
            this.roadAddress = branch.getRoadAddress();
            this.streetAddress = branch.getStreetAddress();
        }
    }
}
