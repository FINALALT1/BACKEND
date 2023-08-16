package kr.co.moneybridge.dto.board;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.Company;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBSpeciality;
import kr.co.moneybridge.model.user.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
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
        @ApiModelProperty(example = "삼성증권", value = "회사명")
        private String companyName;
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
        private Boolean isBookmarked = false; //set해줘야함

        public BoardPageDTO(Board board, PB pb, Company company) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.pbName = pb.getName();
            this.companyName = company.getName();
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
        private LocalDateTime updatedAt;
        private Long pbId;
        private String name;
        private String profile;
        private PBSpeciality speciality1;
        private PBSpeciality speciality2;
        private int career;
        private Long viewCount;
        private String content;
        private Boolean isBookmarked;
        private List<ReplyOutDTO> reply;

        public BoardDetailDTO(Board board, PB pb) {
            this.id = board.getId();
            this.thumbnail = board.getThumbnail();
            this.tag1 = board.getTag1();
            this.tag2 = board.getTag2();
            this.title = board.getTitle();
            this.createdAt = board.getCreatedAt();
            this.updatedAt = board.getUpdatedAt();
            this.pbId = pb.getId();
            this.name = pb.getName();
            this.profile = pb.getProfile();
            this.speciality1 = pb.getSpeciality1();
            this.speciality2 = pb.getSpeciality2();
            this.career = pb.getCareer();
            this.viewCount = board.getViewCount();
            this.content = board.getContent();
        }
    }

    @ApiModel
    @Getter
    @Setter
    public static class BoardDetailByAnonDTO {

        @ApiModelProperty(example = "1", value = "컨텐츠 식별자")
        private Long id;
        @ApiModelProperty(value = "썸네일 URL")
        private String thumbnail;
        @ApiModelProperty(value = "태그 1")
        private String tag1;
        @ApiModelProperty(value = "태그 2")
        private String tag2;
        @ApiModelProperty(value = "제목")
        private String title;
        @ApiModelProperty(example = "2023-01-01T01:01:01", value = "생성일")
        private LocalDateTime createdAt;
        @ApiModelProperty(example = "2023-01-01T01:01:01", value = "수정일")
        private LocalDateTime updatedAt;
        @ApiModelProperty(example = "1", value = "PB 식별자")
        private Long pbId;
        @ApiModelProperty(value = "이름")
        private String name;
        @ApiModelProperty(value = "프로필 URL")
        private String profile;
        @ApiModelProperty(value = "전문분야 1")
        private PBSpeciality speciality1;
        @ApiModelProperty(value = "전문분야 2")
        private PBSpeciality speciality2;
        @ApiModelProperty(value = "경력")
        private int career;
        @ApiModelProperty(value = "조회수")
        private Long viewCount;
        @ApiModelProperty(value = "내용")
        private String content;
        @ApiModelProperty(value = "댓글 목록")
        private List<ReplyOutDTO> reply;

        public BoardDetailByAnonDTO(Board board, PB pb) {
            this.id = board.getId();
            this.thumbnail = board.getThumbnail();
            this.tag1 = board.getTag1();
            this.tag2 = board.getTag2();
            this.title = board.getTitle();
            this.createdAt = board.getCreatedAt();
            this.updatedAt = board.getUpdatedAt();
            this.pbId = pb.getId();
            this.name = pb.getName();
            this.profile = pb.getProfile();
            this.speciality1 = pb.getSpeciality1();
            this.speciality2 = pb.getSpeciality2();
            this.career = pb.getCareer();
            this.viewCount = board.getViewCount();
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
        private Long authorId;
        private ReplyAuthorRole role;
        private List<ReReplyOutDTO> reReply;

        public ReplyOutDTO(Reply reply, User user) {
            this.id = reply.getId();
            this.name = user.getName();
            this.profile = null;
            this.content = reply.getContent();
            this.createdAt = reply.getCreatedAt();
            this.authorId = user.getId();
            this.role = reply.getAuthorRole();
        }

        public ReplyOutDTO(Reply reply, PB pb) {
            this.id = reply.getId();
            this.name = pb.getName();
            this.profile = pb.getProfile();
            this.content = reply.getContent();
            this.createdAt = reply.getCreatedAt();
            this.authorId = pb.getId();
            this.role = reply.getAuthorRole();
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
        private Long authorId;
        private ReplyAuthorRole role;
        private Integer uniqueValue;

        public ReReplyOutDTO(ReReply reReply, User user) {
            this.id = reReply.getId();
            this.name = user.getName();
            this.profile = null;
            this.content = reReply.getContent();
            this.createdAt = reReply.getCreatedAt();
            this.authorId = user.getId();
            this.role = reReply.getAuthorRole();
            this.uniqueValue = reReply.getUniqueValue();
        }

        public ReReplyOutDTO(ReReply reReply, PB pb) {
            this.id = reReply.getId();
            this.name = pb.getName();
            this.profile = pb.getProfile();
            this.content = reReply.getContent();
            this.createdAt = reReply.getCreatedAt();
            this.authorId = pb.getId();
            this.role = reReply.getAuthorRole();
            this.uniqueValue = reReply.getUniqueValue();
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
    public static class BoardListOutDTO<T> {
        private List<T> list;

        public BoardListOutDTO(List<T> list) {
            this.list = list;
        }
    }

    @Getter
    @Setter
    public static class BoardThumbnailDTO {
        private String thumbnail;
    }

    @ApiModel
    @Getter
    @Setter
    public static class PhotoPathDTO {
        @ApiModelProperty(value = "S3 이미지 경로")
        private String path;

        public PhotoPathDTO(String path) {
            this.path = path;
        }
    }
}
