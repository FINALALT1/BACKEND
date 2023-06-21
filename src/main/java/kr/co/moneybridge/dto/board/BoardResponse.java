package kr.co.moneybridge.dto.board;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.board.BoardStatus;
import kr.co.moneybridge.model.board.ReReply;
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

    @ApiModel(description = "메인페이지 컨텐츠 데이터")
    @Getter
    @Setter
    public static class BoardPageDTO {
        @ApiModelProperty(example = "1", value = "컨텐츠 id")
        private Long id;
        @ApiModelProperty(example = "제목입니다", value = "컨텐츠 제목")
        private String title;
        @ApiModelProperty(example = "김피비", value = "PB 이름")
        private String pbName;
        @ApiModelProperty(example = "logo.png", value = "회사 로고")
        private String companyLogo;
        @ApiModelProperty(example = "5", value = "연차")
        private int career;
        @ApiModelProperty(example = "시장정보", value = "태그1")
        private String tag1;
        @ApiModelProperty(example = "쉽게읽혀요", value = "태그2")
        private String tag2;
        @ApiModelProperty(example = "안녕하세요", value = "한줄메세지")
        private String msg;
        @ApiModelProperty(example = "true", value = "북마크여부")
        private Boolean isBookmark = false; //set해줘야함

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
        private String title;
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
            this.title = board.getTitle();
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
        private String content;
        private LocalDateTime createdAt;
        private List<ReReplyOutDTO> reReply;

        public ReplyOutDTO(Reply reply, User user) {
            this.id = reply.getId();
            this.name = user.getName();
            this.profile = null;
            this.content = reply.getContent();
            this.createdAt = reply.getCreatedAt();
        }
        public ReplyOutDTO(Reply reply, PB pb) {
            this.id = reply.getId();
            this.name = pb.getName();
            this.profile = pb.getProfile();
            this.content = reply.getContent();
            this.createdAt = reply.getCreatedAt();
        }
    }

    @Getter
    @Setter
    public static class ReReplyOutDTO {

        private Long id;
        private String name;
        private String profile;
        private String content;
        private LocalDateTime createdAt;

        public ReReplyOutDTO(ReReply reReply, User user) {
            this.id = reReply.getId();
            this.name = user.getName();
            this.profile = null;
            this.content = reReply.getContent();
            this.createdAt = reReply.getCreatedAt();
        }
        public ReReplyOutDTO(ReReply reReply, PB pb) {
            this.id = reReply.getId();
            this.name = pb.getName();
            this.profile = pb.getProfile();
            this.content = reReply.getContent();
            this.createdAt = reReply.getCreatedAt();
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

    @Getter
    @Setter
    public static class BoardListOutDTO {
        private List<BoardResponse.BoardPageDTO> list;

        public BoardListOutDTO(List<BoardPageDTO> list) {
            this.list = list;
        }
    }
}
