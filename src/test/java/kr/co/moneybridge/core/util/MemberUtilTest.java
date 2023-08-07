package kr.co.moneybridge.core.util;

import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class MemberUtilTest extends MockDummyEntity {
    @InjectMocks
    private MemberUtil memberUtil;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private StyleRepository styleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAgreementRepository userAgreementRepository;

//    @Mock
//    private UserInvestInfoRepository userInvestInfoRepository;

    @Mock
    private UserBookmarkRepository userBookmarkRepository;

    @Mock
    private BoardBookmarkRepository boardBookmarkRepository;

    @Mock
    private PBRepository pbRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private PBAgreementRepository pbAgreementRepository;

    @Mock
    private AwardRepository awardRepository;

    @Mock
    private CareerRepository careerRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private ReReplyRepository reReplyRepository;

    @Mock
    private S3Util s3Util;

    @Test
    void findByEmail_pb_test() {
        // given
        String email = "김피비@nate.com";
        PB pb = newMockPB(1L, "lee", newMockBranch(1L,
                newMockCompany(1L, "미래에셋증권"), 0));

        // stub
        when(pbRepository.findByEmail(any())).thenReturn(Optional.of(pb));

        // when
        Member member = memberUtil.findByEmail(email, Role.PB);

        // then
        Assertions.assertThat(member).isEqualTo(pb);
    }

    @Test
    void findByEmail_user_test() {
        // given
        String email = "lee@nate.com";
        User user = newMockUser(1L, "lee");

        // stub
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        Member member = memberUtil.findByEmail(email, Role.USER);

        // then
        Assertions.assertThat(member).isEqualTo(user);
    }

    @Test
    void findByNameAndPhoneNumber_pb_test() {
        // given
        PB pb = newMockPB(1L, "lee", newMockBranch(1L,
                newMockCompany(1L, "미래에셋증권"), 0));
        String name = "lee";
        String phoneNumber = "01012345678";

        // stub
        when(pbRepository.findByPhoneNumber(any())).thenReturn(Arrays.asList(pb));

        // when
        List<Member> members = memberUtil.findByPhoneNumberWithoutException(phoneNumber, Role.PB);

        // then
        Assertions.assertThat(members.get(0)).isEqualTo(pb);
    }

    @Test
    void findByNameAndPhoneNumber_user_test() {
        // given
        User user = newMockUser(1L, "lee");
        String name = "lee";
        String phoneNumber = "01012345678";

        // stub
        when(userRepository.findByPhoneNumber(any())).thenReturn(Arrays.asList(user));

        // when
        List<Member> members = memberUtil.findByPhoneNumberWithoutException(phoneNumber, Role.USER);

        // then
        Assertions.assertThat(members.get(0)).isEqualTo(user);
    }

    @Test
    void findById_pb_test() {
        // given
        Long id = 1L;
        PB pb = newMockPB(1L, "lee", newMockBranch(1L,
                newMockCompany(1L, "미래에셋증권"), 0));

        // stub
        when(pbRepository.findById(any())).thenReturn(Optional.of(pb));

        // when
        Member member = memberUtil.findById(id, Role.PB);

        // then
        Assertions.assertThat(member).isEqualTo(pb);
    }

    @Test
    void findById_user_test() {
        // given
        Long id = 1L;
        User user = newMockUser(1L, "lee");

        // stub
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // when
        Member member = memberUtil.findById(1L, Role.USER);

        // then
        Assertions.assertThat(member).isEqualTo(user);
    }

    @Test
    public void deleteById_user_test() {
        // given
        Long id = 1L;
        User user = newMockUser(1L, "lee");
        User user2 = newMockUser(2L, "han");
        UserAgreement userAgreement1 = newMockUserAgreement(1L, user, UserAgreementType.REQUIRED);
        UserAgreement userAgreement2 = newMockUserAgreement(2L, user, UserAgreementType.OPTIONAL);

        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch1 = newMockBranch(1L, company, 0);
        Branch branch2 = newMockBranch(2L, company, 10);
        PB pb1 = newMockPB(1L, "kim", branch1);
        PB pb2 = newMockPB(2L, "choi", branch2);
        UserBookmark userBookmark1 = newMockUserBookmark(1L, user, pb1);
        UserBookmark userBookmark2 = newMockUserBookmark(2L, user2, pb2);

        Board board1 = newMockBoard(1L, "board1", pb1);
        Board board2 = newMockBoard(2L, "board2", pb2);
        BoardBookmark boardBookmark1 = newMockBoardBookmark(1L, user, board1);
        BoardBookmark boardBookmark2 = newMockBoardBookmark(2L, user, board2);

        Reply reply1 = newMockUserReply(1L, board1, user);
        Reply reply2 = newMockUserReply(2L, board2, user2);
        Reply reply3 = newMockUserReply(3L, board2, user);
        ReReply rereply1 = newMockUserReReply(1L, reply2, user);
        ReReply rereply2 = newMockUserReReply(2L, reply1, user2);
        List<Reply> replies = Arrays.asList(reply1, reply3);

        Reservation reservation1 = newMockCallReservation(1L, user, pb1, ReservationProcess.COMPLETE);
        Reservation reservation2 = newMockVisitReservation(2L, user, pb2, ReservationProcess.APPLY);
        List<Reservation> reservations = Arrays.asList(reservation1, reservation2);
        Review review = newMockReview(1L, reservation1);
        Optional<Review> reviewOP = Optional.ofNullable(review);
        Style style1 = newMockStyle(1L, review, StyleStyle.FAST);
        Style style2 = newMockStyle(2L, review, StyleStyle.KIND);

        // stub
        when(replyRepository.findAllByAuthor(id, ReplyAuthorRole.USER)).thenReturn(replies);
        when(reservationRepository.findAllByUserId(id)).thenReturn(reservations);
        when(reviewRepository.findByReservationId(reservation1.getId())).thenReturn(reviewOP);

        // when
        memberUtil.deleteById(id, Role.USER);

        // then
        verify(userRepository, times(1)).deleteById(id);
        verify(userAgreementRepository, times(1)).deleteByUserId(id);
        verify(userBookmarkRepository, times(1)).deleteByUserId(id);
        verify(boardBookmarkRepository, times(1)).deleteByBookmarker(id, BookmarkerRole.USER);
        verify(reReplyRepository, times(1)).deleteByReplyId(reply1.getId());
        verify(reReplyRepository, times(1)).deleteByReplyId(reply3.getId());
        verify(replyRepository, times(1)).deleteByAuthor(id, ReplyAuthorRole.USER);
        verify(reReplyRepository, times(1)).deleteByAuthor(id, ReplyAuthorRole.USER);
        verify(styleRepository, times(1)).deleteByReviewId(review.getId());
        verify(reviewRepository, times(1)).deleteByReservationId(reservation1.getId());
        verify(reservationRepository, times(1)).deleteByUserId(id);
    }

    @Test
    public void deleteById_pb_test() throws IOException {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch1 = newMockBranch(1L, company, 0);
        Branch branch2 = newMockBranch(2L, company, 10);
        PB pb = newMockPB(1L, "kim", branch1);
        PB pb2 = newMockPB(2L, "choi", branch2);

        Portfolio portfolio = newMockPortfolio(1L, pb);
        PBAgreement pbAgreement1 = newMockPBAgreement(1L, pb, PBAgreementType.REQUIRED);
        PBAgreement pbAgreement2 = newMockPBAgreement(2L, pb, PBAgreementType.OPTIONAL);
        Award award = newMockAward(1L, pb);
        Career career = newMockCareer(1L, pb);

        User user1 = newMockUser(1L, "lee");
        User user2 = newMockUser(2L, "han");
        UserBookmark userBookmark1 = newMockUserBookmark(1L, user1, pb);
        UserBookmark userBookmark2 = newMockUserBookmark(2L, user2, pb2);

        Board board1 = newMockBoard(1L, "board1", pb);
        Board board2 = newMockBoard(2L, "board2", pb);
        List<Board> boards = Arrays.asList(board1, board2);
        BoardBookmark boardBookmark1 = newMockBoardBookmark(1L, user1, board1);
        BoardBookmark boardBookmark2 = newMockBoardBookmarkByPB(2L, pb2, board2);
        Reply reply1 = newMockUserReply(1L, board1, user1);
        Reply reply2 = newMockUserReply(2L, board2, user2);
        Reply reply3 = newMockUserReply(3L, board2, user1);
        ReReply rereply1 = newMockUserReReply(1L, reply2, user1);
        ReReply rereply2 = newMockUserReReply(2L, reply1, user2);
        List<Reply> repliesOnBoard1 = Arrays.asList(reply1);
        List<Reply> repliesOnBoard2 = Arrays.asList(reply2, reply3);

        Board board3 = newMockBoard(3L, "board3", pb2);
        Board board4 = newMockBoard(4L, "board4", pb2);
        Reply reply4 = newMockPBReply(4L, board3, pb);
        Reply reply5 = newMockPBReply(5L, board4, pb2);
        Reply reply6 = newMockPBReply(6L, board4, pb);
        ReReply rereply3 = newMockPBReReply(3L, reply5, pb);
        ReReply rereply4 = newMockUserReReply(4L, reply4, user2);
        List<Reply> replies = Arrays.asList(reply4, reply6);

        Reservation reservation1 = newMockCallReservation(1L, user1, pb, ReservationProcess.COMPLETE);
        Reservation reservation2 = newMockVisitReservation(2L, user2, pb, ReservationProcess.APPLY);
        List<Reservation> reservations = Arrays.asList(reservation1, reservation2);
        Review review = newMockReview(1L, reservation1);
        Optional<Review> reviewOP = Optional.ofNullable(review);
        Style style1 = newMockStyle(1L, review, StyleStyle.FAST);
        Style style2 = newMockStyle(2L, review, StyleStyle.KIND);
        MockMultipartFile init = new MockMultipartFile(
                "init", "businessCard.png", "image/png"
                , new FileInputStream("./src/main/resources/businessCard.png"));
        String path = s3Util.upload(init, "test");

        // stub
        when(boardRepository.findAllByPBId(id)).thenReturn(boards);
        when(replyRepository.findAllByBoardId(board1.getId())).thenReturn(repliesOnBoard1);
        when(replyRepository.findAllByBoardId(board2.getId())).thenReturn(repliesOnBoard2);
        when(replyRepository.findAllByAuthor(id, ReplyAuthorRole.PB)).thenReturn(replies);
        when(reservationRepository.findAllByPBId(id)).thenReturn(reservations);
        when(reviewRepository.findByReservationId(reservation1.getId())).thenReturn(reviewOP);
        when(portfolioRepository.findFileByPBId(id)).thenReturn(Optional.empty());
        when(pbRepository.findBusinessCardById(id)).thenReturn(Optional.of("card.png"));
        when(pbRepository.findProfileById(id)).thenReturn(Optional.of("profile.png"));
//        when(s3Util.delete(path)).then(doNothing());

        // when
        memberUtil.deleteById(id, Role.PB);

        // then
        verify(portfolioRepository, times(1)).findFileByPBId(id);
        verify(pbRepository, times(1)).findBusinessCardById(id);
        verify(pbRepository, times(1)).findProfileById(id);
        verify(pbRepository, times(1)).deleteById(id);
        verify(portfolioRepository, times(1)).deleteByPBId(id);
        verify(pbAgreementRepository, times(1)).deleteByPBId(id);
        verify(awardRepository, times(1)).deleteByPBId(id);
        verify(careerRepository, times(1)).deleteByPBId(id);
        verify(userBookmarkRepository, times(1)).deleteByPBId(id);
        verify(boardBookmarkRepository, times(1)).deleteByBookmarker(id, BookmarkerRole.PB);
        verify(boardRepository, times(1)).deleteByPBId(id);
        verify(boardRepository, times(1)).deleteByPBId(id);
        verify(boardBookmarkRepository, times(1)).deleteByBoardId(board1.getId());
        verify(boardBookmarkRepository, times(1)).deleteByBoardId(board2.getId());
        verify(replyRepository, times(1)).deleteByBoardId(board1.getId());
        verify(replyRepository, times(1)).deleteByBoardId(board2.getId());
        verify(reReplyRepository, times(1)).deleteByReplyId(reply1.getId());
        verify(reReplyRepository, times(1)).deleteByReplyId(reply2.getId());
        verify(reReplyRepository, times(1)).deleteByReplyId(reply4.getId());
        verify(reReplyRepository, times(1)).deleteByReplyId(reply6.getId());
        verify(replyRepository, times(1)).deleteByAuthor(id, ReplyAuthorRole.PB);
        verify(reReplyRepository, times(1)).deleteByAuthor(id, ReplyAuthorRole.PB);
        verify(styleRepository, times(1)).deleteByReviewId(review.getId());
        verify(reviewRepository, times(1)).deleteByReservationId(reservation1.getId());
        verify(reservationRepository, times(1)).deleteByPBId(id);
    }
}
