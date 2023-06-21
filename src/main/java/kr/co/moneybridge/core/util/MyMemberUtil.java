package kr.co.moneybridge.core.util;

import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MyMemberUtil {
    @Value("${DEFAULT_PROFILE}")
    private String defaultProfile;
    private final UserRepository userRepository;
    private final UserAgreementRepository userAgreementRepository;
    private final UserInvestInfoRepository userInvestInfoRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
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
    private final StyleRepository styleRepository;
    private final UserBookmarkRepository userBookmarkRepository;
    private final S3Util s3Util;

    @Transactional
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

                boardBookmarkRepository.deleteByBookmarker(id, BookmarkerRole.USER);
                userBookmarkRepository.deleteByUserId(id);
                userInvestInfoRepository.deleteByUserId(id);
                userAgreementRepository.deleteByUserId(id);
                userRepository.deleteById(id);
            }catch (Exception e){
                new Exception500("투자자 계정 삭제 실패했습니다" + e.getMessage());
            }
        } else if(role.equals(Role.PB)){
            // s3에서 액셀데이터도 삭제
            // s3에서 명함사진도 삭제
            // s3에서 프로필 사진도 삭제
            deleteFiles(portfolioRepository.findFileByPBId(id), pbRepository.findBusinessCardById(id).get(),
                    pbRepository.findProfileById(id).get());
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

                boardBookmarkRepository.deleteByBookmarker(id, BookmarkerRole.PB);
                userBookmarkRepository.deleteByPBId(id);

                careerRepository.deleteByPBId(id);
                awardRepository.deleteByPBId(id);
                pbAgreementRepository.deleteByPBId(id);
                portfolioRepository.deleteByPBId(id);
                pbRepository.deleteById(id);

            }catch (Exception e){
                new Exception500("PB 계정 삭제 실패했습니다" + e.getMessage());
            }
        }
    }

    private void deleteFiles(Optional<String> file, String businessCard, String profile){
        if(file.isPresent()){
            s3Util.delete(file.get());
        }
        if(profile != defaultProfile){
            s3Util.delete(profile);
        }
        s3Util.delete(businessCard);
    }

    public List<Member> findByNameAndPhoneNumberWithoutException(String name, String phoneNumber, Role role) {
        List<Member> members = null;
        if(role.equals(Role.USER) || role.equals(Role.ADMIN)){
            List<User> users = userRepository.findByNameAndPhoneNumber(name, phoneNumber);
            if(users.isEmpty()){
                return null;
            }
            members = new ArrayList<>(users);
        } else if(role.equals(Role.PB)) {
            List<PB> pbs = pbRepository.findByNameAndPhoneNumber(name, phoneNumber);
            if (pbs.isEmpty()) {
                return null;
            }
            members = new ArrayList<>(pbs);
        }
        return members;
    }

    public Member findByEmailWithoutException(String email, Role role) {
        Member member = null;
        if(role.equals(Role.USER) || role.equals(Role.ADMIN)){
            Optional<User> userOP = userRepository.findByEmail(email);
            if(userOP.isEmpty()) return null;
            member = userOP.get();
        } else if(role.equals(Role.PB)){
            Optional<PB> pbOP = pbRepository.findByEmail(email);
            if(pbOP.isEmpty()) return null;
             member = pbOP.get();
        }
        return member;
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
