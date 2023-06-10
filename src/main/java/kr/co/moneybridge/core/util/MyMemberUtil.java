package kr.co.moneybridge.core.util;

import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.backoffice.AnswerRepository;
import kr.co.moneybridge.model.backoffice.Question;
import kr.co.moneybridge.model.backoffice.QuestionAuthorRole;
import kr.co.moneybridge.model.backoffice.QuestionRepository;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.*;

import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBRepository;
import kr.co.moneybridge.model.pb.PBStatus;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class MyMemberUtil {
    private final UserRepository userRepository;
    private final UserAgreementRepository userAgreementRepository;
    private final UserInvestInfoRepository userInvestInfoRepository;
    private final PBBookmarkRepository pbBookmarkRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final QuestionRepository questionRepository;
    private final PBRepository pbRepository;
    private final PortfolioRepository portfolioRepository;
    private final PBAgreementRepository pbAgreementRepository;
    private final AwardRepository awardRepository;
    private final CareerRepository careerRepository;
    private final BoardRepository boardRepository;
    private final ReservationRepository reservationRepository;
    private final ReplyRepository replyRepository;
    private final ReReplyRepository reReplyRepository;
    private final ReviewRepository reviewRepository;
    private final AnswerRepository answerRepository;
    private final StyleRepository styleRepository;

    public void deleteById(Long id, Role role) {
        if(role.equals(Role.USER) || role.equals(Role.ADMIN)){
            try{
                List<Reservation> reservations = reservationRepository.findAllByUserId(id);
                reservations.stream().forEach(reservation -> {
                    Optional<Review> review = reviewRepository.findByReservationId(reservation.getId());
                    if(review.isPresent()){
                        // review를 지우니, review를 연관관계로 가지고 있는 style삭제
                        styleRepository.deleteByReviewId(review.get().getId());
                        // reservation을 지우니, reservation을 연관관계로 가지고 있는 review도 삭제
                        reviewRepository.deleteByReservationId(reservation.getId());
                    }
                });
                reservationRepository.deleteByUserId(id);

                List<Reply> replies = replyRepository.findAllByAuthor(id, ReplyAuthorRole.USER);
                replies.stream().forEach(reply -> {
                    // reply를 지우니, reply를 연관관계로 가지고 있는 reReply도 삭제
                    reReplyRepository.deleteByReplyId(reply.getId());
                });
                replyRepository.deleteByAuthor(id, ReplyAuthorRole.USER); // 댓글 삭제
                reReplyRepository.deleteByAuthor(id, ReplyAuthorRole.USER); // 대댓글 삭제

                List<Question> questions = questionRepository.findAllByAuthor(id, QuestionAuthorRole.USER);
                questions.stream().forEach(question -> {
                    // question을 지우니, question을 연관관계로 가지고 있는 answer 삭제
                    answerRepository.deleteByQuestionId(question.getId());
                });
                questionRepository.deleteByAuthor(id, QuestionAuthorRole.USER);

//                boardBookmarkRepository.deleteByUserId(id);
                pbBookmarkRepository.deleteByUserId(id);
                userInvestInfoRepository.deleteByUserId(id);
                userAgreementRepository.deleteByUserId(id);
                userRepository.deleteById(id);
            }catch (Exception e){
                new Exception500("투자자 계정 삭제 실패했습니다");
            }
        } else if(role.equals(Role.PB)){
            try{
                List<Reservation> reservations = reservationRepository.findAllByPBId(id);
                reservations.stream().forEach(reservation -> {
                    Optional<Review> review = reviewRepository.findByReservationId(reservation.getId());
                    if(review.isPresent()){
                        styleRepository.deleteByReviewId(review.get().getId());
                        reviewRepository.deleteByReservationId(reservation.getId());
                    }
                });
                reservationRepository.deleteByPBId(id);

                List<Reply> replies = replyRepository.findAllByAuthor(id, ReplyAuthorRole.PB);
                replies.stream().forEach(reply -> {
                    reReplyRepository.deleteByReplyId(reply.getId());
                });
                replyRepository.deleteByAuthor(id, ReplyAuthorRole.PB);
                reReplyRepository.deleteByAuthor(id, ReplyAuthorRole.PB);

                List<Question> questions = questionRepository.findAllByAuthor(id, QuestionAuthorRole.PB);
                questions.stream().forEach(question -> {
                    answerRepository.deleteByQuestionId(question.getId());
                });
                questionRepository.deleteByAuthor(id, QuestionAuthorRole.PB);

                List<Board> boards = boardRepository.findAllByPBId(id);
                boards.stream().forEach(board -> {
                    // board를 지우니, board를 연관관계로 가지고 있는 boardBookmark삭제
                    boardBookmarkRepository.deleteByBoardId(board.getId());
                    List<Reply> repliesOnBoard = replyRepository.findAllByBoardId(board.getId());
                    repliesOnBoard.stream().forEach(replyOnBoard -> {
                        // reply를 지우니, reply를 연관관계로 가지고 있는 reReply도 삭제
                        reReplyRepository.deleteByReplyId(replyOnBoard.getId());
                    });
                    // board를 지우니, board를 연관관계로 가지고 있는 reply삭제
                    replyRepository.deleteByBoardId(board.getId());
                });
                boardRepository.deleteByPBId(id);

                pbBookmarkRepository.deleteByPBId(id);
                careerRepository.deleteByPBId(id);
                awardRepository.deleteByPBId(id);
                pbAgreementRepository.deleteByPBId(id);
                portfolioRepository.deleteByPBId(id);
                pbRepository.deleteById(id);
            }catch (Exception e){
                new Exception500("PB 계정 삭제 실패했습니다");
            }
        }
    }
    public List<Member> findByNameAndPhoneNumber(String name, String phoneNumber, Role role) {
        List<Member> members = null;
        if(role.equals(Role.USER) || role.equals(Role.ADMIN)){
            List<User> users = userRepository.findByNameAndPhoneNumber(name, phoneNumber);
            if(users.isEmpty()){
                throw new Exception404("사용자가 존재하지 않습니다");
            }
            members = new ArrayList<>(users);
        } else if(role.equals(Role.PB)) {
            List<PB> pbs = pbRepository.findByNameAndPhoneNumber(name, phoneNumber);
            if (pbs.isEmpty()) {
                throw new Exception404("사용자가 존재하지 않습니다");
            }
            members = new ArrayList<>(pbs);
        }
        return members;
    }

    public Member findByEmail(String email, Role role) {
        Member member = null;
        if(role.equals(Role.USER) || role.equals(Role.ADMIN)){
            User userPS = userRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception404("해당하는 투자자 계정이 없습니다"));
            member = userPS;
        } else if(role.equals(Role.PB)){
            PB pbPS = pbRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception404("해당하는 PB 계정이 없습니다"));
            if(pbPS.getStatus().equals(PBStatus.PENDING)){
                throw new Exception404("아직 승인되지 않은 PB 계정입니다");
            }
            member = pbPS;
        }
        return member;
    }

    public Member findById(Long id, Role role) {
        Member member = null;
        if(role.equals(Role.USER) || role.equals(Role.ADMIN)){
            User userPS = userRepository.findById(id)
                    .orElseThrow(() -> new Exception404("해당하는 투자자 계정이 없습니다"));
            member = userPS;
        } else if(role.equals(Role.PB)){
            PB pbPS = pbRepository.findById(id)
                    .orElseThrow(() -> new Exception404("해당하는 PB 계정이 없습니다"));
            if(pbPS.getStatus().equals(PBStatus.PENDING)){
                throw new Exception404("아직 승인되지 않은 PB 계정입니다");
            }
            member = pbPS;
        }
        return member;
    }
}
