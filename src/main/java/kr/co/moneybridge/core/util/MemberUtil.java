package kr.co.moneybridge.core.util;

import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.reservation.ReservationRepository;
import kr.co.moneybridge.model.reservation.Review;
import kr.co.moneybridge.model.reservation.ReviewRepository;
import kr.co.moneybridge.model.reservation.StyleRepository;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserAgreementRepository;
import kr.co.moneybridge.model.user.UserBookmarkRepository;
import kr.co.moneybridge.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberUtil {
    private String defaultProfile = "https://moneybridge.s3.ap-northeast-2.amazonaws.com/default/profile.svg";
    private final UserRepository userRepository;
    private final UserAgreementRepository userAgreementRepository;
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
    private final StibeeUtil stibeeUtil;
    private final Environment environment;

    @Transactional
    public void deleteById(Long id, Role role) {
        if (role.equals(Role.USER) || role.equals(Role.ADMIN)) {
            try {
                // 스티비 주소록 구독 취소
                if (environment.acceptsProfiles("prod")) {
                    stibeeUtil.withdraw(role.name(), findById(id, role).getEmail());
                }

                reservationRepository.findAllByUserId(id).stream().forEach(reservation -> {
                    Optional<Review> review = reviewRepository.findByReservationId(reservation.getId());
                    if (review.isPresent()) {
                        // review를 지우니, review를 연관관계로 가지고 있는 style삭제
                        styleRepository.deleteByReviewId(review.get().getId());
                        // reservation을 지우니, reservation을 연관관계로 가지고 있는 review도 삭제
                        reviewRepository.deleteByReservationId(reservation.getId());
                    }
                });
                reservationRepository.deleteByUserId(id);

                replyRepository.findAllByAuthor(id, ReplyAuthorRole.USER).stream().forEach(reply -> {
                    // reply를 지우니, reply를 연관관계로 가지고 있는 reReply도 삭제
                    reReplyRepository.deleteByReplyId(reply.getId());
                });
                replyRepository.deleteByAuthor(id, ReplyAuthorRole.USER); // 댓글 삭제
                reReplyRepository.deleteByAuthor(id, ReplyAuthorRole.USER); // 대댓글 삭제

                boardBookmarkRepository.deleteByBookmarker(id, BookmarkerRole.USER);
                userBookmarkRepository.deleteByUserId(id);
                userAgreementRepository.deleteByUserId(id);
                userRepository.deleteById(id);
            } catch (Exception e) {
                throw new Exception500("투자자 계정 삭제 실패했습니다" + e.getMessage());
            }
        } else if (role.equals(Role.PB)) {
            // s3에서 액셀데이터도 삭제
            // s3에서 명함사진도 삭제
            // s3에서 프로필 사진도 삭제
            // s3에서 컨텐츠 썸네일도 삭제
            deleteFiles(portfolioRepository.findFileByPBId(id), pbRepository.findBusinessCardById(id),
                    pbRepository.findProfileById(id), boardRepository.findThumbnailsByPBId(id));
            System.out.println("2");
            try {
                // 스티비 주소록 구독 취소
                if (environment.acceptsProfiles("prod")) {
                    stibeeUtil.withdraw(role.name(), findById(id, role).getEmail());
                }

                reservationRepository.findAllByPBId(id).stream().forEach(reservation -> {
                    Optional<Review> review = reviewRepository.findByReservationId(reservation.getId());
                    if (review.isPresent()) {
                        styleRepository.deleteByReviewId(review.get().getId());
                        reviewRepository.deleteByReservationId(reservation.getId());
                    }
                });
                reservationRepository.deleteByPBId(id);

                replyRepository.findAllByAuthor(id, ReplyAuthorRole.PB).stream().forEach(reply -> {
                    reReplyRepository.deleteByReplyId(reply.getId());
                });
                replyRepository.deleteByAuthor(id, ReplyAuthorRole.PB);
                reReplyRepository.deleteByAuthor(id, ReplyAuthorRole.PB);

                boardRepository.findAllByPBId(id).stream().forEach(board -> {
                    // board를 지우니, board를 연관관계로 가지고 있는 boardBookmark삭제
                    boardBookmarkRepository.deleteByBoardId(board.getId());
                    replyRepository.findAllByBoardId(board.getId()).stream().forEach(reply -> {
                        // reply를 지우니, reply를 연관관계로 가지고 있는 reReply도 삭제
                        reReplyRepository.deleteByReplyId(reply.getId());
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
            } catch (Exception e) {
                throw new Exception500("PB 계정 삭제 실패했습니다" + e.getMessage());
            }
        }
    }

    private void deleteFiles(Optional<String> file, Optional<String> businessCard, Optional<String> profile, List<String> thumbnails) {
        if (file.isPresent()) {
            s3Util.delete(file.get());
        }
        if (profile.get() != defaultProfile) {
            s3Util.delete(profile.get());
        }
        s3Util.delete(businessCard.get());
        thumbnails.stream().forEach(thumbnail -> s3Util.delete(thumbnail));
    }

    public List<Member> findByPhoneNumberWithoutException(String phoneNumber, Role role) {
        List<Member> members = null;
        if (role.equals(Role.USER) || role.equals(Role.ADMIN)) {
            List<User> users = userRepository.findByPhoneNumber(phoneNumber);
            if (users.isEmpty()) {
                return null;
            }
            members = new ArrayList<>(users);
        } else if (role.equals(Role.PB)) {
            List<PB> pbs = pbRepository.findByPhoneNumber(phoneNumber);
            if (pbs.isEmpty()) {
                return null;
            }
            members = new ArrayList<>(pbs);
        }
        return members;
    }

    public Member findByEmailWithoutException(String email, Role role) {
        Member member = null;
        if (role.equals(Role.USER) || role.equals(Role.ADMIN)) {
            Optional<User> userOP = userRepository.findByEmail(email);
            if (userOP.isEmpty()) return null;
            member = userOP.get();
        } else if (role.equals(Role.PB)) {
            Optional<PB> pbOP = pbRepository.findByEmail(email);
            if (pbOP.isEmpty()) return null;
            member = pbOP.get();
        }
        return member;
    }

    public Member findByEmail(String email, Role role) {
        Member member = null;
        if (role.equals(Role.USER) || role.equals(Role.ADMIN)) {
            User userPS = userRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception404("해당하는 투자자 계정이 없습니다"));
            member = userPS;
        } else if (role.equals(Role.PB)) {
            PB pbPS = pbRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception404("해당하는 PB 계정이 없습니다"));
            if (pbPS.getStatus().equals(PBStatus.PENDING)) {
                throw new Exception404("아직 승인되지 않은 PB 계정입니다");
            }
            member = pbPS;
        }
        return member;
    }

    public Member findById(Long id, Role role) {
        Member member = null;
        if (role.equals(Role.USER) || role.equals(Role.ADMIN)) {
            User userPS = userRepository.findById(id)
                    .orElseThrow(() -> new Exception404("해당하는 투자자 계정이 없습니다"));
            member = userPS;
        } else if (role.equals(Role.PB)) {
            PB pbPS = pbRepository.findById(id)
                    .orElseThrow(() -> new Exception404("해당하는 PB 계정이 없습니다"));
            if (pbPS.getStatus().equals(PBStatus.PENDING)) {
                throw new Exception404("아직 승인되지 않은 PB 계정입니다");
            }
            member = pbPS;
        }
        return member;
    }
}
