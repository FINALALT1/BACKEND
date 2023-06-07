package kr.co.moneybridge.dto.board;

import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.board.Reply;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

public class ReplyRequest {

    @Getter
    @Setter
    public static class ReplyInDTO {

        private Long parentId;
        private String content;

        public Reply toEntity(User user, Board board) {
            return Reply.builder()
                    .user(user)
                    .board(board)
                    .parentId(parentId)
                    .content(content)
                    .status(true)
                    .build();
        }
    }
}
