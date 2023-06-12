package kr.co.moneybridge.dto.board;

import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.board.BoardStatus;
import kr.co.moneybridge.model.board.Reply;
import kr.co.moneybridge.model.pb.Company;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBSpeciality;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class BoardResponse {

    @Getter
    @Setter
    public static class BoardPageDTO {

        private Long id;
        private String title;
        private String pbName;
        private String companyLogo;
        private int career;
        private String tag1;
        private String tag2;
        private String msg;

        public BoardPageDTO(Board board, PB pb, Company company) {
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

    @Getter
    @Setter
    public static class BoardDetailDTO {

        private Long id;
        private String thumbnail;
        private String tag1;
        private String tag2;
        private LocalDateTime createdAt;
        private Long pbId;
        private String name;
        private String profile;
        private PBSpeciality speciality1;
        private PBSpeciality speciality2;
        private int career;
        private String content;
        private List<ReplyOutDTO> reply;

        public BoardDetailDTO(Board board, PB pb) {
            this.id = board.getId();
            this.thumbnail = board.getThumbnail();
            this.tag1 = board.getTag1();
            this.tag2 = board.getTag2();
            this.createdAt = board.getCreatedAt();
            this.pbId = pb.getId();
            this.name = pb.getName();
            this.profile = pb.getProfile();
            this.speciality1 = pb.getSpeciality1();
            this.speciality2 = pb.getSpeciality2();
            this.career = pb.getCareer();
            this.content = board.getContent();
        }
    }

    @Getter
    @Setter
    public static class ReplyOutDTO {

        private Long id;
        private String name;
        private String profile;
        private String replyContent;
        private LocalDateTime createdAt;
        private Long parentId;

        public ReplyOutDTO(Reply reply, User user) {
            this.id = reply.getId();
            this.name = user.getName();
            this.profile = user.getProfile();
            this.replyContent = reply.getContent();
            this.createdAt = reply.getCreatedAt();
//            this.parentId = reply.getParentId();
        }
    }

    @Getter
    @Setter
    public static class BoardTempDTO {

        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

    }

    @Getter
    @Setter
    public static class BoardOutDTO {

        private String title;
        private String content;
        private String tag1;
        private String tag2;
        private String thumbnail;
        private BoardStatus status;

    }
}
