package kr.co.moneybridge.dto.board;

import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.board.ReReply;
import kr.co.moneybridge.model.board.Reply;
import kr.co.moneybridge.model.board.ReplyAuthorRole;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

public class ReplyRequest {

    @Getter
    @Setter
    public static class ReplyInDTO {

        @ApiModelProperty(example = "댓글입니다.")
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

        @ApiModelProperty(example = "대댓글입니다.")
        private String content;

        public ReReply toEntity(Reply reply, User user, ReplyAuthorRole role) {
            return ReReply.builder()
                    .authorId(user.getId())
                    .reply(reply)
                    .authorRole(role)
                    .content(content)
                    .uniqueValue(new Random().nextInt((999999 - 100000) + 1) + 100000)
                    .build();
        }

        public ReReply toEntity(Reply reply, PB pb, ReplyAuthorRole role) {
            return ReReply.builder()
                    .authorId(pb.getId())
                    .reply(reply)
                    .authorRole(role)
                    .content(content)
                    .uniqueValue(new Random().nextInt((999999 - 100000) + 1) + 100000)
                    .build();
        }

    }
}
