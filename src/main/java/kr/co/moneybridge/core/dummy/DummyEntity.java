package kr.co.moneybridge.core.dummy;

import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.backoffice.Answer;
import kr.co.moneybridge.model.backoffice.Question;
import kr.co.moneybridge.model.backoffice.QuestionAuthorRole;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DummyEntity {
    public User newUser(String username) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .name(username)
                .password(passwordEncoder.encode("password1234"))
                .email(username + "@nate.com")
                .phoneNumber("01012345678")
                .role(Role.USER)
                .profile("프로필.png")
                .hasDoneReview(false)
                .hasDoneReservation(false)
                .hasDoneBoardBookmark(false)
                .build();
    }

    public User newUserWithPropensity(String username) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .name(username)
                .password(passwordEncoder.encode("password1234"))
                .email(username + "@nate.com")
                .phoneNumber("01012345678")
                .propensity(UserPropensity.AGGRESSIVE)
                .role(Role.USER)
                .profile("프로필.png")
                .hasDoneReview(false)
                .hasDoneReservation(false)
                .hasDoneBoardBookmark(false)
                .build();
    }

    public PB newPB(String username, Branch branch) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return PB.builder()
                .name(username)
                .password(passwordEncoder.encode("password1234"))
                .email(username + "@nate.com")
                .phoneNumber("01012345678")
                .branch(branch)
                .profile("profile.png")
                .businessCard("card.png")
                .career(10)
                .speciality1(PBSpeciality.BOND)
                .intro(username + " 입니다")
                .msg("한줄메시지..")
                .reservationInfo("10분 미리 도착해주세요")
                .consultStart(MyDateUtil.StringToLocalTime("09:00"))
                .consultEnd(MyDateUtil.StringToLocalTime("18:00"))
                .consultNotice("월요일 불가능합니다")
                .role(Role.PB)
                .status(PBStatus.ACTIVE)
                .build();
    }

    public PB newPBwithStatus(String username, Branch branch, PBStatus pbStatus) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return PB.builder()
                .name(username)
                .password(passwordEncoder.encode("password1234"))
                .email(username + "@nate.com")
                .phoneNumber("01012345678")
                .branch(branch)
                .profile("profile.png")
                .businessCard("card.png")
                .career(10)
                .speciality1(PBSpeciality.BOND)
                .intro(username + " 입니다")
                .msg("한줄메시지..")
                .reservationInfo("10분 미리 도착해주세요")
                .consultStart(MyDateUtil.StringToLocalTime("09:00"))
                .consultEnd(MyDateUtil.StringToLocalTime("18:00"))
                .consultNotice("월요일 불가능합니다")
                .role(Role.PB)
                .status(pbStatus)
                .build();
    }

    public PB newPBWithSpeciality(String username, Branch branch) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return PB.builder()
                .name(username)
                .password(passwordEncoder.encode("password1234"))
                .email(username + "@nate.com")
                .phoneNumber("01012345678")
                .branch(branch)
                .profile("profile.png")
                .businessCard("card.png")
                .career(10)
                .speciality1(PBSpeciality.BOND)
                .speciality2(PBSpeciality.ETF)
                .intro(username + " 입니다")
                .msg("한줄메시지..")
                .reservationInfo("10분 미리 도착해주세요")
                .consultStart(MyDateUtil.StringToLocalTime("09:00"))
                .consultEnd(MyDateUtil.StringToLocalTime("18:00"))
                .consultNotice("월요일 불가능합니다")
                .role(Role.PB)
                .status(PBStatus.ACTIVE)
                .build();
    }

    public Board newBoard(String title, PB pb) {
        return Board.builder()
                .pb(pb)
                .title(title)
                .thumbnail("thumbnail.png")
                .content("content 입니다")
                .tag1("시장정보")
                .tag2("쉽게읽혀요")
                .clickCount(0L)
                .status(BoardStatus.ACTIVE)
                .build();
    }

    public Board newTempBoard(String title, PB pb) {
        return Board.builder()
                .pb(pb)
                .title(title)
                .thumbnail("thumbnail.png")
                .content("content 입니다")
                .tag1("시장정보")
                .tag2("쉽게읽혀요")
                .clickCount(0L)
                .status(BoardStatus.TEMP)
                .build();
    }
    public Reply newUserReply(Board board, User user) {
        return Reply.builder()
                .board(board)
                .authorId(user.getId())
                .authorRole(ReplyAuthorRole.USER)
                .content("댓글입니다")
                .build();
    }

    public ReReply newUserReReply(Reply reply, User user) {
        return ReReply.builder()
                .reply(reply)
                .authorId(user.getId())
                .authorRole(ReplyAuthorRole.USER)
                .content("대댓글입니다")
                .build();
    }

    public Reply newPBReply(Board board, PB pb) {
        return Reply.builder()
                .board(board)
                .authorId(pb.getId())
                .authorRole(ReplyAuthorRole.PB)
                .content("댓글입니다")
                .build();
    }

    public ReReply newPBReReply(Reply reply, PB pb) {
        return ReReply.builder()
                .reply(reply)
                .authorId(pb.getId())
                .authorRole(ReplyAuthorRole.PB)
                .content("대댓글입니다")
                .build();
    }

    public Reservation newVisitReservation(User user, PB pb, ReservationProcess process) {
        return Reservation.builder()
                .user(user)
                .pb(pb)
                .type(ReservationType.VISIT)
                .locationName("kb증권 강남중앙점")
                .locationAddress("강남구 강남중앙로 10")
                .candidateTime1(LocalDateTime.now())
                .candidateTime2(LocalDateTime.now().minusHours(1))
                .time(LocalDateTime.now())
                .question("질문입니다...")
                .goal(ReservationGoal.PROFIT)
                .process(process)
                .investor(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .status(ReservationStatus.ACTIVE)
                .build();
    }

    public Reservation newVisitReservationCancel(User user, PB pb) {
        return Reservation.builder()
                .user(user)
                .pb(pb)
                .type(ReservationType.VISIT)
                .locationName("kb증권 강남중앙점")
                .locationAddress("강남구 강남중앙로 10")
                .candidateTime1(LocalDateTime.now())
                .candidateTime2(LocalDateTime.now().minusHours(1))
                .time(LocalDateTime.now())
                .question("질문입니다...")
                .process(ReservationProcess.CONFIRM)
                .goal(ReservationGoal.PROFIT)
                .investor(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .status(ReservationStatus.CANCEL)
                .build();
    }

    public Reservation newCallReservation(User user, PB pb, ReservationProcess process) {
        return Reservation.builder()
                .user(user)
                .pb(pb)
                .type(ReservationType.CALL)
                .locationName("kb증권 강남중앙점")
                .locationAddress("강남구 강남중앙로 10")
                .candidateTime1(LocalDateTime.now())
                .candidateTime2(LocalDateTime.now().minusHours(1))
                .time(LocalDateTime.now())
                .question("질문입니다...")
                .goal(ReservationGoal.PROFIT)
                .process(process)
                .investor(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .status(ReservationStatus.ACTIVE)
                .build();
    }

    public Review newReview(Reservation reservation) {
        return Review.builder()
                .reservation(reservation)
                .content("content 입니다")
                .adherence(ReviewAdherence.EXCELLENT)
                .build();
    }

    public Style newStyle(Review review, StyleStyle styleStyle) {
        return Style.builder()
                .review(review)
                .style(styleStyle)
                .build();
    }

    public UserBookmark newUserBookmark(User user, PB pb) {
        return UserBookmark.builder()
                .user(user)
                .pb(pb)
                .build();
    }

    public PBBookmark newPBBookmark(PB pb, User user) {
        return PBBookmark.builder()
                .user(user)
                .pb(pb)
                .build();
    }

    public BoardBookmark newBoardBookmark(User user, Board board) {
        return BoardBookmark.builder()
                .bookmarkerId(user.getId())
                .bookmarkerRole(BookmarkerRole.USER)
                .board(board)
                .build();
    }

    public Career newCareer(PB pb) {
        return Career.builder()
                .pb(pb)
                .career("키움증권")
                .startYear(2020)
                .endYear(2022)
                .build();
    }

    public Award newAward(PB pb) {
        return Award.builder()
                .pb(pb)
                .record("수상이력입니다.")
                .build();
    }

    public PBAgreement newPBAgreement(PB pb, PBAgreementType type) {
        return PBAgreement.builder()
                .pb(pb)
                .title("약관1")
                .isAgreed(true)
                .type(type)
                .build();
    }

    public Portfolio newPortfolio(PB pb) {
        return Portfolio.builder()
                .pb(pb)
                .highestReturn(90.0)
                .averageProfit(10.1)
                .profitFactor(1.54)
                .maxDrawdown(101.1)
                .file("file1.pdf")
                .build();
    }

    public Company newCompany(String name) {
        return Company.builder()
                .name(name)
                .logo("logo.png")
                .build();
    }

    public Branch newBranch(Company company, int num) {
        Double latitude = 36.36671 + num;
        Double longitude = 127.34451 + num;
        return Branch.builder()
                .company(company)
                .name(company.getName() + " 여의도점")
                .roadAddress(company.getName() + " 도로명주소")
                .streetAddress(company.getName() + " 지번주소")
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    public UserAgreement newUserAgreement(User user, UserAgreementType type) {
        return UserAgreement.builder()
                .user(user)
                .title("약관1")
                .type(type)
                .isAgreed(true)
                .build();
    }

    public UserInvestInfo newUserInvestInfo(User user) {
        return UserInvestInfo.builder()
                .user(user)
                .q1(5)
                .q2(4)
                .q3(5)
                .q4(5)
                .q5(5)
                .q6(5)
                .build();
    }

    public Question newUserQuestion(User user) {
        return Question.builder()
                .authorId(user.getId())
                .authorRole(QuestionAuthorRole.USER)
                .title("제목")
                .content("1대1문의사항")
                .build();
    }

    public Question newPBQuestion(PB pb) {
        return Question.builder()
                .authorId(pb.getId())
                .authorRole(QuestionAuthorRole.PB)
                .title("제목")
                .content("1대1문의사항")
                .build();
    }

    public Answer newAnswer(Question question) {
        return Answer.builder()
                .question(question)
                .content("답변")
                .build();
    }
}
