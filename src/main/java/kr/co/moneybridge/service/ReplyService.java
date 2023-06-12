package kr.co.moneybridge.service;

import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.dto.board.ReplyRequest;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBRepository;
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
    private final ReReplyRepository reReplyRepository;
    private final UserRepository userRepository;
    private final PBRepository pbRepository;
    private final BoardRepository boardRepository;

    //댓글 작성하기(user)
    @Transactional
    public void postUserReply(ReplyRequest.ReplyInDTO replyInDTO, Long userId, Long boardId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new Exception400("user", "해당 유저 찾울 수 없습니다."));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new Exception400("board", "해당 컨텐츠 찾울 수 없습니다."));

        try {
            Reply reply = replyInDTO.toEntity(user.getId(), board, ReplyAuthorRole.USER);
            replyRepository.save(reply);
        } catch (Exception e) {
            throw new Exception500("댓글 저장 실패");
        }
    }

    //댓글 작성하기(pb)
    @Transactional
    public void postPbReply(ReplyRequest.ReplyInDTO replyInDTO, Long pbId, Long boardId) {

        PB pb = pbRepository.findById(pbId).orElseThrow(() -> new Exception400("pb", "해당 유저 찾울 수 없습니다."));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new Exception400("board", "해당 컨텐츠 찾울 수 없습니다."));

        try {
            Reply reply = replyInDTO.toEntity(pb.getId(), board, ReplyAuthorRole.PB);
            replyRepository.save(reply);
        } catch (Exception e) {
            throw new Exception500("댓글 저장 실패");
        }
    }

    //대댓글 작성하기
    @Transactional
    public void postReReply(Long replyId, ReplyRequest.ReReplyInDTO reReplyInDTO) {

        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new Exception400("reply", "해당 댓글 찾을 수 없습니다."));
        try {
            ReReply reReply = reReplyInDTO.toEntity(reply);
            reReplyRepository.save(reReply);
        } catch (Exception e) {
            throw new Exception500("대댓글 저장 실패");
        }
    }
}
