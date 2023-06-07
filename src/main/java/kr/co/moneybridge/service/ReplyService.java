package kr.co.moneybridge.service;

import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.dto.board.ReplyRequest;
import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.board.BoardRepository;
import kr.co.moneybridge.model.board.Reply;
import kr.co.moneybridge.model.board.ReplyRepository;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    //댓글 작성하기
    @Transactional
    public void postReply(ReplyRequest.ReplyInDTO replyInDTO, Long userId, Long boardId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new Exception400("user", "해당 유저 찾울 수 없습니다."));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new Exception400("board", "해당 컨텐츠 찾울 수 없습니다."));
        Reply reply = replyInDTO.toEntity(user, board);
        replyRepository.save(reply);
    }
}
