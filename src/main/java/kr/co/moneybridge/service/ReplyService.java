package kr.co.moneybridge.service;

import kr.co.moneybridge.dto.board.ReplyRequest;
import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.board.Reply;
import kr.co.moneybridge.model.board.ReplyRepository;
import kr.co.moneybridge.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;

    //댓글 작성하기
    @Transactional
    public void postReply(ReplyRequest.ReplyInDTO replyInDTO, User user, Board board) {
        Reply reply = replyInDTO.toEntity(user, board);
        replyRepository.save(reply);
    }
}
