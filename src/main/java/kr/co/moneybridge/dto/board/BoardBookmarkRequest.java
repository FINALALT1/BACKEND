package kr.co.moneybridge.dto.board;

import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class BoardBookmarkRequest {

    @Getter
    @Setter
    public static class BoardBookmarkInDTO {

        private User user;
        private Board board;

        public BoardBookmarkInDTO(User user, Board board) {
            this.user = user;
            this.board = board;
        }
    }
}
