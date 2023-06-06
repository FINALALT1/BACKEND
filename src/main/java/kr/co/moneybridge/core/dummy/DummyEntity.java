package kr.co.moneybridge.core.dummy;

import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.board.BoardBookmark;
import kr.co.moneybridge.model.board.BoardStatus;
import kr.co.moneybridge.model.board.Reply;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DummyEntity {
    public User newUser(String username) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .name(username)
                .password(passwordEncoder.encode("Password1234"))
                .email(username + "@nate.com")
                .phoneNumber("01012345678")
                .role(Role.ROLE_USER)
                .status(true)
                .build();
    }

    public User newUserWithPropensity(String username) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .name(username)
                .password(passwordEncoder.encode("Password1234"))
                .email(username + "@nate.com")
                .phoneNumber("01012345678")
                .propensity(UserPropensity.AGGRESSIVE)
                .role(Role.ROLE_USER)
                .status(true)
                .build();
    }

    public PB newPB(String username, Branch branch) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return PB.builder()
                .name(username)
                .password(passwordEncoder.encode("Password1234"))
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
                .role(Role.ROLE_PB)
                .status(PBStatus.ACTIVE)
                .build();
    }

    public PB newPBWithSpeciality(String username, Branch branch) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return PB.builder()
                .name(username)
                .password(passwordEncoder.encode("Password1234"))
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
                .role(Role.ROLE_PB)
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

    public Reply newReply(Board board, User user) {
        return Reply.builder()
                .board(board)
                .user(user)
                .content("댓글입니다")
                .status(true)
                .build();
    }

    public Reply newParentReply(Board board, User user, Reply reply) {
        return Reply.builder()
                .board(board)
                .user(user)
                .content("댓글입니다")
                .parentId(reply.getId())
                .status(true)
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
                .goal1(ReservationGoal.PROFIT)
                .goal2(ReservationGoal.PRESERVATION)
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
                .goal1(ReservationGoal.PROFIT)
                .goal2(ReservationGoal.PRESERVATION)
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
                .goal1(ReservationGoal.PROFIT)
                .goal2(ReservationGoal.PRESERVATION)
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
                .status(true)
                .build();
    }

    public Style newStyle(Review review, StyleStyle styleStyle) {
        return Style.builder()
                .review(review)
                .style(styleStyle)
                .status(true)
                .build();
    }

    public UserBookmark newUserBookmark(User user, PB pb) {
        return UserBookmark.builder()
                .user(user)
                .pb(pb)
                .status(true)
                .build();
    }

    public PBBookmark newPBBookmark(PB pb, User user) {
        return PBBookmark.builder()
                .user(user)
                .pb(pb)
                .status(true)
                .build();
    }

    public BoardBookmark newBoardBookmark(User user, Board board) {
        return BoardBookmark.builder()
                .user(user)
                .board(board)
                .status(true)
                .build();
    }

    public Career newCareer(PB pb) {
        return Career.builder()
                .pb(pb)
                .career("키움증권")
                .startYear(2020)
                .endYear(2022)
                .status(true)
                .build();
    }

    public Award newAward(PB pb) {
        return Award.builder()
                .pb(pb)
                .record("수상이력입니다.")
                .status(true)
                .build();
    }

    public PBAgreement newPBAgreement(PB pb, PBAgreementType type) {
        return PBAgreement.builder()
                .pb(pb)
                .title("약관1")
                .isAgreed(true)
                .type(type)
                .status(true)
                .build();
    }

    public Portfolio newPortfolio(PB pb) {
        return Portfolio.builder()
                .pb(pb)
                .highestReturn(90)
                .startDate(LocalDate.now().minusYears(1))
                .endDate(LocalDate.now().minusMonths(1))
                .propensity(PBPropensity.AGGRESSIVE)
                .dangerRate(1)
                .file("file1.pdf")
                .status(true)
                .build();
    }

    public Company newCompany(String name) {
        return Company.builder()
                .name(name)
                .logo("logo.png")
                .status(true)
                .build();
    }

    public Branch newBranch(Company company, int num) {
        String latitude = "36.36671";
        String longitude = "127.34451";
        double lat = Double.parseDouble(latitude) + num;
        double lon = Double.parseDouble(longitude) + num;
        return Branch.builder()
                .company(company)
                .name(company.getName() + " 여의도점")
                .roadAddress(company.getName() + " 도로명주소")
                .streetAddress(company.getName() + " 지번주소")
                .latitude(String.valueOf(lat))
                .longitude(String.valueOf(lon))
                .status(true)
                .build();
    }

    public UserAgreement newUserAgreement(User user, UserAgreementType type) {
        return UserAgreement.builder()
                .user(user)
                .title("약관1")
                .type(type)
                .isAgreed(true)
                .status(true)
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
                .status(true)
                .build();
    }

    public User newMockUser(Long id, String username) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .name(username)
                .password(passwordEncoder.encode("Password1234"))
                .email(username + "@nate.com")
                .phoneNumber("01012345678")
                .propensity(UserPropensity.AGGRESSIVE)
                .role(Role.ROLE_USER)
                .status(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
