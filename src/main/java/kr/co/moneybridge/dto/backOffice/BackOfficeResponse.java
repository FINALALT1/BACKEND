package kr.co.moneybridge.dto.backOffice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.backoffice.FrequentQuestion;
import kr.co.moneybridge.model.backoffice.Notice;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class BackOfficeResponse {
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
