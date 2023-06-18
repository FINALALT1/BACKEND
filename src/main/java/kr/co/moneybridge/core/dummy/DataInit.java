package kr.co.moneybridge.core.dummy;

import kr.co.moneybridge.model.backoffice.*;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class DataInit extends DummyEntity{

    @Profile("dev")
    @Bean
    CommandLineRunner init(UserRepository userRepository,
                           CompanyRepository companyRepository,
                           BranchRepository branchRepository,
                           PBRepository pbRepository,
                           PortfolioRepository portfolioRepository,
                           PBAgreementRepository pbAgreementRepository,
                           AwardRepository awardRepository,
                           CareerRepository careerRepository,
                           UserAgreementRepository userAgreementRepository,
                           UserInvestInfoRepository userInvestInfoRepository,
                           BoardRepository boardRepository,
                           ReplyRepository replyRepository,
                           ReReplyRepository reReplyRepository,
                           UserBookmarkRepository userBookmarkRepository,
                           BoardBookmarkRepository boardBookmarkRepository,
                           PBBookmarkRepository pbBookmarkRepository,
                           ReservationRepository reservationRepository,
                           ReviewRepository reviewRepository,
                           StyleRepository styleRepository,
                           QuestionRepository questionRepository,
                           AnswerRepository answerRepository,
                           FrequentQuestionRepository frequentQuestionRepository,
                           NoticeRepository noticeRepository){
        return args -> {
            User user1 = userRepository.save(newUserWithPropensity("김투자"));
            User user2 = userRepository.save(newUserWithPropensity("이투자"));
            User user3 = userRepository.save(newUser("박투자"));
            User user4 = userRepository.save(newUser("최투자"));

            userAgreementRepository.save(newUserAgreement(user1, UserAgreementType.REQUIRED));
            userAgreementRepository.save(newUserAgreement(user1, UserAgreementType.OPTIONAL));
            userAgreementRepository.save(newUserAgreement(user2, UserAgreementType.REQUIRED));
            userAgreementRepository.save(newUserAgreement(user2, UserAgreementType.OPTIONAL));

            userInvestInfoRepository.save(newUserInvestInfo(user1));
            userInvestInfoRepository.save(newUserInvestInfo(user2));

            Company c1 = companyRepository.save(newCompany("미래에셋증권"));
            Company c2 = companyRepository.save(newCompany("키움증권"));
            Company c3 = companyRepository.save(newCompany("카카오페이증권"));
            Company c4 = companyRepository.save(newCompany("삼성증권"));
            Company c5 = companyRepository.save(newCompany("한국투자증권"));

            Branch b1 = branchRepository.save(newBranch(c1, 0));
            Branch b2 = branchRepository.save(newBranch(c2, 1));
            Double latitude = 36.36671;
            Double longitude = 127.34451;
            Branch b3 = branchRepository.save(Branch.builder()
                    .company(c1)
                    .name(c1.getName() + " 강남대로점")
                    .roadAddress("서울 강남구 강남대로 390")
                    .streetAddress("역삼동 825 미진프라자 1층 101호")
                    .latitude(latitude)
                    .longitude(longitude)
                    .build());
            branchRepository.save(newBranch(c3, 2));
            branchRepository.save(newBranch(c4, 3));
            branchRepository.save(newBranch(c5, 4));

            PB pb1 = pbRepository.save(newPB("김pb", b1));
            PB pb2 = pbRepository.save(newPBWithSpeciality("이pb", b1));
            PB pb3 = pbRepository.save(newPBWithSpeciality("박pb", b2));
            PB pb4 = pbRepository.save(newPBwithStatus("윤pb", b1, PBStatus.PENDING));
            PB pb5 = pbRepository.save(newPBwithStatus("나pb", b2, PBStatus.PENDING));

            portfolioRepository.save(newPortfolio(pb1));
            portfolioRepository.save(newPortfolio(pb2));
            portfolioRepository.save(newPortfolio(pb3));

            pbAgreementRepository.save(newPBAgreement(pb1, PBAgreementType.REQUIRED));
            pbAgreementRepository.save(newPBAgreement(pb1, PBAgreementType.OPTIONAL));
            pbAgreementRepository.save(newPBAgreement(pb2, PBAgreementType.REQUIRED));
            pbAgreementRepository.save(newPBAgreement(pb2, PBAgreementType.OPTIONAL));

            awardRepository.save(newAward(pb1));
            awardRepository.save(newAward(pb2));

            careerRepository.save(newCareer(pb1));
            careerRepository.save(newCareer(pb2));

            Board board1 = boardRepository.save(newBoard("제목1", pb1));
            Board board2 =boardRepository.save(newTempBoard("제목2", pb1));
            Board board3 =boardRepository.save(newBoard("제목3", pb2));
            Board board4 =boardRepository.save(newTempBoard("제목4", pb2));
            Board board5 =boardRepository.save(newBoard("제목5", pb3));
            Board board6 = boardRepository.save(newTempBoard("제목6", pb3));

            Reply reply1 = replyRepository.save(newUserReply(board1, user1));
            Reply reply2 = replyRepository.save(newUserReply(board1, user2));
            Reply reply3 = replyRepository.save(newPBReply(board1, pb1));
            Reply reply4 = replyRepository.save(newPBReply(board1, pb2));
            reReplyRepository.save(newUserReReply(reply3, user1));
            reReplyRepository.save(newUserReReply(reply4, user2));
            reReplyRepository.save(newUserReReply(reply4, user1));
            reReplyRepository.save(newPBReReply(reply2, pb1));
            reReplyRepository.save(newPBReReply(reply1, pb2));

            userBookmarkRepository.save(newUserBookmark(user1, pb1));
            userBookmarkRepository.save(newUserBookmark(user1, pb2));
            userBookmarkRepository.save(newUserBookmark(user2, pb1));
            userBookmarkRepository.save(newUserBookmark(user2, pb2));

            boardBookmarkRepository.save(newBoardBookmark(user1, board1));
            boardBookmarkRepository.save(newBoardBookmark(user1, board2));
            boardBookmarkRepository.save(newBoardBookmark(user1, board3));
            boardBookmarkRepository.save(newBoardBookmark(user2, board2));
            boardBookmarkRepository.save(newBoardBookmark(user2, board5));

            pbBookmarkRepository.save(newPBBookmark(pb1, user1));
            pbBookmarkRepository.save(newPBBookmark(pb1, user2));
            pbBookmarkRepository.save(newPBBookmark(pb2, user1));
            pbBookmarkRepository.save(newPBBookmark(pb2, user2));

            reservationRepository.save(newCallReservation(user1, pb1, ReservationProcess.APPLY));
            reservationRepository.save(newCallReservation(user1, pb1, ReservationProcess.CONFIRM));
            Reservation reservation1 = reservationRepository.save(newCallReservation(user2, pb1, ReservationProcess.COMPLETE));
            reservationRepository.save(newVisitReservation(user1, pb2, ReservationProcess.APPLY));
            reservationRepository.save(newVisitReservation(user2, pb2, ReservationProcess.CONFIRM));
            Reservation reservation2 = reservationRepository.save(newVisitReservation(user2, pb2, ReservationProcess.COMPLETE));
            reservationRepository.save(newVisitReservationCancel(user1, pb1));

            Review review1 = reviewRepository.save(newReview(reservation1));
            Review review2 = reviewRepository.save(newReview(reservation2));

            styleRepository.save(newStyle(review1, StyleStyle.FAST));
            styleRepository.save(newStyle(review2, StyleStyle.KIND));

            Question q1 = questionRepository.save(newUserQuestion(user1));
            Question q2 = questionRepository.save(newPBQuestion(pb1));
            Question q3 = questionRepository.save(newUserQuestion(user2));
            Question q4 = questionRepository.save(newPBQuestion(pb2));
            answerRepository.save(newAnswer(q1));
            answerRepository.save(newAnswer(q2));

            frequentQuestionRepository.save(newFrequentQuestion());
            noticeRepository.save(newNotice());
        };
    }
}
