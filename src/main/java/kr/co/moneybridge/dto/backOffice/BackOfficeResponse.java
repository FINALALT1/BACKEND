package kr.co.moneybridge.dto.backOffice;

import kr.co.moneybridge.model.backoffice.FrequentQuestion;
import lombok.Getter;
import lombok.Setter;

public class BackOfficeResponse {
    @Getter
    @Setter
    public static class FAQDTO {
        private Long id;
        private String label;
        private String title;
        private String content;

        public FAQDTO(FrequentQuestion frequentQuestion) {
            this.id = frequentQuestion.getId();
            this.label = frequentQuestion.getLabel();
            this.title = frequentQuestion.getTitle();
            this.content = frequentQuestion.getContent();
        }
    }
}
