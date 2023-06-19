package kr.co.moneybridge.dto.backOffice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.model.backoffice.FrequentQuestion;
import kr.co.moneybridge.model.backoffice.Notice;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBSpeciality;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class BackOfficeResponse {
    @ApiModel
    @Getter
    @Setter
    public static class PBPendingDTO {
        @ApiModelProperty(example = "4", value = "승인 대기 중인 pb id")
        private Long id;
        @ApiModelProperty(example = "윤pb@nate.com", value = "이메일")
        private String email;
        @ApiModelProperty(example = "윤pb", value = "이름")
        private String name;
        @ApiModelProperty(example = "01012345678", value = "핸드폰 번호")
        private String phoneNumber;
        @ApiModelProperty(example = "미래에셋증권 여의도점", value = "지점명")
        private String branchName;
        @ApiModelProperty(example = "10", value = "경력(연차)")
        private Integer career;
        @ApiModelProperty(example = "BOND", value = "전문분야1")
        private PBSpeciality speciality1;
        @ApiModelProperty(example = "null", value = "전문분야2")
        private PBSpeciality speciality2;
        @ApiModelProperty(example = "https://d23znr2pczcvf6.cloudfront.net/5fb1a367-f935-4905-a643-bbf8cd7afc19_%EB%8B%AC%ED%8C%BD%EC%9D%B4.jpg", value = "명함 사진")
        private String businessCard;

        public PBPendingDTO(PB pb, String branchName) {
            this.id = pb.getId();
            this.email = pb.getEmail();
            this.name = pb.getName();
            this.phoneNumber = pb.getPhoneNumber();
            this.branchName = branchName;
            this.career = pb.getCareer();
            this.speciality1 = pb.getSpeciality1();
            this.speciality2 = pb.getSpeciality2();
            this.businessCard = pb.getBusinessCard();
        }
    }

    @ApiModel(description = "PB회원 가입 요청 승인 페이지 전체 가져오기 응답 데이터")
    @Getter
    @Setter
    public static class PBPendingOutDTO {
        @ApiModelProperty(example = "2", value = "PB 승인 대기 건수")
        private Integer count;
        @ApiModelProperty
        private PageDTO<PBPendingDTO> page;

        public PBPendingOutDTO(Integer count, PageDTO<PBPendingDTO> page) {
            this.count = count;
            this.page = page;
        }
    }

    @ApiModel(description = "공지사항 목록 가져오기 응답 데이터")
    @Getter
    @Setter
    public static class NoticeDTO {
        @ApiModelProperty(example = "1", value = "자주 묻는 질문 id")
        private Long id;
        @ApiModelProperty(example = "이메일이 주소가 변경되었어요.", value = "제목")
        private String title;
        @ApiModelProperty(example ="보다 나은 환경을 제공하기 위해 개발진에서 발견한 문제 복구 및 업데이트 점검을 진행할 예정입니다.\n" +
                "업데이트 점검을 진행하는 동안에는 접속할 수 없으니 불필요한 손해가 발생치 않도록 주의해 주세요.\n" +
                "이로 인해 불편을 끼쳐 드려 죄송합니다.", value = "내용")
        private String content;
        @ApiModelProperty(example = "2023-06-18", value = "작성 날짜")
        private LocalDate date;

        public NoticeDTO(Notice notice) {
            this.id = notice.getId();
            this.title = notice.getTitle();
            this.content = notice.getContent();
            this.date = notice.getCreatedAt().toLocalDate();
        }
    }

    @ApiModel(description = "자주 묻는 질문 목록 가져오기 응답 데이터")
    @Getter
    @Setter
    public static class FAQDTO {
        @ApiModelProperty(example = "1", value = "자주 묻는 질문 id")
        private Long id;
        @ApiModelProperty(example = "회원", value = "라벨")
        private String label;
        @ApiModelProperty(example = "이메일이 주소가 변경되었어요.", value = "제목")
        private String title;
        @ApiModelProperty(example = "가입 이메일은 회원 식별 고유 키로 가입 후 변경이 불가능 하므로 개인 이메일로 가입하기를 권유드립니다.", value = "내용")
        private String content;

        public FAQDTO(FrequentQuestion frequentQuestion) {
            this.id = frequentQuestion.getId();
            this.label = frequentQuestion.getLabel();
            this.title = frequentQuestion.getTitle();
            this.content = frequentQuestion.getContent();
        }
    }
}
