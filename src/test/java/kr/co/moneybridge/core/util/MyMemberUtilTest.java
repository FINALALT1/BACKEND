package kr.co.moneybridge.core.util;

import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.backoffice.*;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class MyMemberUtilTest extends MockDummyEntity {
    @InjectMocks
    private MyMemberUtil myMemberUtil;

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

    @Mock
    private UserInvestInfoRepository userInvestInfoRepository;

    @Mock
    private PBBookmarkRepository pbBookmarkRepository;

    @Mock
    private BoardBookmarkRepository boardBookmarkRepository;

    @Mock
    private QuestionRepository questionRepository;

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
    private AnswerRepository answerRepository;

    @Test
    public void deleteById_withUserRole_test() {
        // given
        Long id = 1L;
        User user = newMockUser(1L, "lee");
        User user2 = newMockUser(2L, "han");
        UserAgreement userAgreement1 = newMockUserAgreement(1L, user, UserAgreementType.REQUIRED);
        UserAgreement userAgreement2 = newMockUserAgreement(2L, user, UserAgreementType.OPTIONAL);
        UserInvestInfo userInvestInfo = newMockUserInvestInfo(1L, user);

        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch1 = newMockBranch(1L, company, 0);
        Branch branch2 = newMockBranch(2L, company, 10);
        PB pb1 = newMockPB(1L, "kim", branch1);
        PB pb2 = newMockPB(2L, "choi", branch2);
        PBBookmark pbBookmark1 = newMockPBBookmark(1L, pb1, user);
        PBBookmark pbBookmark2 = newMockPBBookmark(2L, pb2, user);

        Board board1 = newMockBoard(1L, "board1", pb1);
        Board board2 = newMockBoard(2L, "board2", pb2);
        BoardBookmark boardBookmark1 = newMockBoardBookmark(1L, user, board1);
        BoardBookmark boardBookmark2 = newMockBoardBookmark(2L, user, board2);

        Question question1 = newMockUserQuestion(1L, user);
        Question question2 = newMockUserQuestion(2L, user);
        List<Question> questions = Arrays.asList(question1, question2);
        Answer answer = newMockAnswer(1L, question1);

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

        // when
        when(questionRepository.findAllByAuthor(id, QuestionAuthorRole.USER)).thenReturn(questions);
        when(replyRepository.findAllByAuthor(id, ReplyAuthorRole.USER)).thenReturn(replies);
        when(reservationRepository.findAllByUserId(id)).thenReturn(reservations);
        when(reviewRepository.findByReservationId(reservation1.getId())).thenReturn(reviewOP);

        myMemberUtil.deleteById(id, Role.USER);

        // then
        verify(userRepository, times(1)).deleteById(id);
        verify(userAgreementRepository, times(1)).deleteByUserId(id);
        verify(userInvestInfoRepository, times(1)).deleteByUserId(id);
        verify(pbBookmarkRepository, times(1)).deleteByUserId(id);
//        verify(boardBookmarkRepository, times(1)).deleteByUserId(id);
        verify(answerRepository, times(1)).deleteByQuestionId(question1.getId());
        verify(answerRepository, times(1)).deleteByQuestionId(question2.getId());
        verify(questionRepository, times(1)).deleteByAuthor(id, QuestionAuthorRole.USER);
        verify(reReplyRepository, times(1)).deleteByReplyId(reply1.getId());
        verify(reReplyRepository, times(1)).deleteByReplyId(reply3.getId());
        verify(replyRepository, times(1)).deleteByAuthor(id, ReplyAuthorRole.USER);
        verify(reReplyRepository, times(1)).deleteByAuthor(id, ReplyAuthorRole.USER);
        verify(styleRepository, times(1)).deleteByReviewId(review.getId());
        verify(reviewRepository, times(1)).deleteByReservationId(reservation1.getId());
        verify(reservationRepository, times(1)).deleteByUserId(id);
    }

    @Test
    public void deleteById_withPBRole_test() {
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
        PBBookmark pbBookmark1 = newMockPBBookmark(1L, pb, user1);
        PBBookmark pbBookmark2 = newMockPBBookmark(2L, pb, user2);

        Board board1 = newMockBoard(1L, "board1", pb);
        Board board2 = newMockBoard(2L, "board2", pb);
        List<Board> boards = Arrays.asList(board1, board2);
        BoardBookmark boardBookmark1 = newMockBoardBookmark(1L, user1, board1);
        BoardBookmark boardBookmark2 = newMockBoardBookmark(2L, user2, board2);
        Reply reply1 = newMockUserReply(1L, board1, user1);
        Reply reply2 = newMockUserReply(2L, board2, user2);
        Reply reply3 = newMockUserReply(3L, board2, user1);
        ReReply rereply1 = newMockUserReReply(1L, reply2, user1);
        ReReply rereply2 = newMockUserReReply(2L, reply1, user2);
        List<Reply> repliesOnBoard1 = Arrays.asList(reply1);
        List<Reply> repliesOnBoard2 = Arrays.asList(reply2, reply3);

        Question question1 = newMockPBQuestion(1L, pb);
        Question question2 = newMockPBQuestion(2L, pb);
        List<Question> questions = Arrays.asList(question1, question2);
        Answer answer = newMockAnswer(1L, question1);

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

        // when
        when(boardRepository.findAllByPBId(id)).thenReturn(boards);
        when(replyRepository.findAllByBoardId(board1.getId())).thenReturn(repliesOnBoard1);
        when(replyRepository.findAllByBoardId(board2.getId())).thenReturn(repliesOnBoard2);
        when(questionRepository.findAllByAuthor(id, QuestionAuthorRole.PB)).thenReturn(questions);
        when(replyRepository.findAllByAuthor(id, ReplyAuthorRole.PB)).thenReturn(replies);
        when(reservationRepository.findAllByPBId(id)).thenReturn(reservations);
        when(reviewRepository.findByReservationId(reservation1.getId())).thenReturn(reviewOP);

        myMemberUtil.deleteById(id, Role.PB);

        // then
        verify(pbRepository, times(1)).deleteById(id);
        verify(portfolioRepository, times(1)).deleteByPBId(id);
        verify(pbAgreementRepository, times(1)).deleteByPBId(id);
        verify(awardRepository, times(1)).deleteByPBId(id);
        verify(careerRepository, times(1)).deleteByPBId(id);
        verify(pbBookmarkRepository, times(1)).deleteByPBId(id);
        verify(boardRepository, times(1)).deleteByPBId(id);
        verify(boardRepository, times(1)).deleteByPBId(id);
        verify(boardBookmarkRepository, times(1)).deleteByBoardId(board1.getId());
        verify(boardBookmarkRepository, times(1)).deleteByBoardId(board2.getId());
        verify(replyRepository, times(1)).deleteByBoardId(board1.getId());
        verify(replyRepository, times(1)).deleteByBoardId(board2.getId());
        verify(reReplyRepository, times(1)).deleteByReplyId(reply1.getId());
        verify(reReplyRepository, times(1)).deleteByReplyId(reply2.getId());
        verify(questionRepository, times(1)).deleteByAuthor(id, QuestionAuthorRole.PB);
        verify(answerRepository, times(1)).deleteByQuestionId(question1.getId());
        verify(answerRepository, times(1)).deleteByQuestionId(question2.getId());
        verify(reReplyRepository, times(1)).deleteByReplyId(reply4.getId());
        verify(reReplyRepository, times(1)).deleteByReplyId(reply6.getId());
        verify(replyRepository, times(1)).deleteByAuthor(id, ReplyAuthorRole.PB);
        verify(reReplyRepository, times(1)).deleteByAuthor(id, ReplyAuthorRole.PB);
        verify(styleRepository, times(1)).deleteByReviewId(review.getId());
        verify(reviewRepository, times(1)).deleteByReservationId(reservation1.getId());
        verify(reservationRepository, times(1)).deleteByPBId(id);
    }
}
