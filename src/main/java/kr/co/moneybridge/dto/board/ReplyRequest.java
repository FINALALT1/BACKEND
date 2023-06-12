package kr.co.moneybridge.dto.board;

import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.board.ReReply;
import kr.co.moneybridge.model.board.Reply;
import kr.co.moneybridge.model.board.ReplyAuthorRole;
import lombok.Getter;
import lombok.Setter;

public class ReplyRequest {

    @Getter
    @Setter
    public static class ReplyInDTO {

        private String content;

        public Reply toEntity(Long id, Board board, ReplyAuthorRole role) {
            return Reply.builder()
                    .authorId(id)
                    .board(board)
                    .authorRole(role)
                    .content(content)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class ReReplyInDTO {

        private String content;

        public ReReply toEntity(Reply reply) {
            return ReReply.builder()
                    .authorId(reply.getId())
                    .reply(reply)
                    .authorRole(reply.getAuthorRole())
                    .content(content)
                    .build();
        }
    }
}
