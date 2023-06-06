package kr.co.moneybridge.dto.board;

import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.pb.Company;
import kr.co.moneybridge.model.pb.PB;
import lombok.Getter;
import lombok.Setter;

public class BoardResponse {

    @Getter
    @Setter
    public static class BoardOutDTO {

        private Long id;
        private String title;
        private String pbName;
        private String companyLogo;
        private int career;
        private String tag1;
        private String tag2;
        private String msg;

        public BoardOutDTO(Board board, PB pb, Company company) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.pbName = pb.getName();
            this.companyLogo = company.getLogo();
            this.career = pb.getCareer();
            this.tag1 = board.getTag1();
            this.tag2 = board.getTag2();
            this.msg = pb.getMsg();
        }
    }
}
