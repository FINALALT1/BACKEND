package kr.co.moneybridge.dto.board;

import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.board.Reply;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;
import reactor.util.annotation.Nullable;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class BoardRequest {

    @Getter
    @Setter
    public static class BoardInDTO {

        @NotEmpty
        private String title;
        @Column(columnDefinition = "TEXT")
        private String content;
        @Size(max = 30)
        private String tag1;
        @Size(max = 30)
        private String tag2;
        private String thumbnail;

    }
}
