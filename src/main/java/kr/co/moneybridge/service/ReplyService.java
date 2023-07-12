package kr.co.moneybridge.service;

import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.dto.board.ReplyRequest;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
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

//    //댓글 작성하기(user)
//    @Transactional
//    public void postUserReply(ReplyRequest.ReplyInDTO replyInDTO, Long userId, Long boardId) {
//
//        User user = userRepository.findById(userId).orElseThrow(() -> new Exception400("user", "해당 유저 찾울 수 없습니다."));
//        Board board = boardRepository.findById(boardId).orElseThrow(() -> new Exception400("board", "해당 컨텐츠 찾울 수 없습니다."));
//
//        try {
//            Reply reply = replyInDTO.toEntity(user.getId(), board, ReplyAuthorRole.USER);
//            replyRepository.save(reply);
//        } catch (Exception e) {
//            throw new Exception500("댓글 저장 실패");
//        }
//    }
//
//    //댓글 작성하기(pb)
//    @Transactional
//    public void postPbReply(ReplyRequest.ReplyInDTO replyInDTO, Long pbId, Long boardId) {
//
//        PB pb = pbRepository.findById(pbId).orElseThrow(() -> new Exception400("pb", "해당 유저 찾울 수 없습니다."));
//        Board board = boardRepository.findById(boardId).orElseThrow(() -> new Exception400("board", "해당 컨텐츠 찾울 수 없습니다."));
//
//        try {
//            Reply reply = replyInDTO.toEntity(pb.getId(), board, ReplyAuthorRole.PB);
//            replyRepository.save(reply);
//        } catch (Exception e) {
//            throw new Exception500("댓글 저장 실패");
//        }
//    }

    //댓글 작성하기
    @Transactional
    public void postReply(MyUserDetails myUserDetails, Long boardId, ReplyRequest.ReplyInDTO replyInDTO) {

        Board board = boardRepository.findById(boardId).orElseThrow(() -> new Exception400("board", "해당 컨텐츠 찾울 수 없습니다."));
        Member member = myUserDetails.getMember();
        Reply reply;
        if (member.getRole().equals(Role.USER)) {
            User user = userRepository.findById(member.getId()).orElseThrow(() -> new Exception400("user", "해당 유저 찾울 수 없습니다."));
            reply = replyInDTO.toEntity(user.getId(), board, ReplyAuthorRole.USER);
        } else if (member.getRole().equals(Role.PB)) {
            PB pb = pbRepository.findById(member.getId()).orElseThrow(() -> new Exception400("pb", "해당 유저 찾울 수 없습니다."));
            reply = replyInDTO.toEntity(pb.getId(), board, ReplyAuthorRole.PB);
        } else {
            User user = userRepository.findById(member.getId()).orElseThrow(() -> new Exception400("user", "해당 유저 찾울 수 없습니다."));
            reply = replyInDTO.toEntity(user.getId(), board, ReplyAuthorRole.ADMIN);
        }

        try {
            replyRepository.save(reply);
        } catch (Exception e) {
            throw new Exception500("댓글 저장 실패");
        }
    }

    //대댓글 작성하기
    @Transactional
    public void postReReply(Long replyId, ReplyRequest.ReReplyInDTO reReplyInDTO, MyUserDetails myUserDetails) {

        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new Exception404("해당 댓글 찾을 수 없습니다."));
        Member member = myUserDetails.getMember();
        ReReply reReply;
        if (member.getRole().equals(Role.USER)) {
            User user = userRepository.findById(member.getId()).orElseThrow(() -> new Exception404("해당 유저 찾을 수 없습니다."));
            reReply = reReplyInDTO.toEntity(reply, user, ReplyAuthorRole.USER);
        } else if (member.getRole().equals(Role.PB)) {
            PB pb = pbRepository.findById(member.getId()).orElseThrow(() -> new Exception404("해당 PB 찾을 수 없습니다."));
            reReply = reReplyInDTO.toEntity(reply, pb, ReplyAuthorRole.PB);
        } else {
            throw new Exception404("댓글 권한 없습니다.");
        }

        try {
            reReplyRepository.save(reReply);
        } catch (Exception e) {
            throw new Exception500("대댓글 저장 실패");
        }
    }
}
