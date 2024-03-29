package kr.co.moneybridge.core.dummy;

import kr.co.moneybridge.core.util.DateUtil;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.backoffice.*;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class MockDummyEntity {
    public User newMockUser(Long id, String username) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .name(username)
                .password(passwordEncoder.encode("password1234"))
                .email(username + "@nate.com")
                .phoneNumber("01012345678")
                .role(Role.USER)
                .profile("profile.png")
                .hasDoneReview(false)
                .hasDoneReservation(false)
                .hasDoneBoardBookmark(false)
                .createdAt(LocalDateTime.now())
                .propensity(UserPropensity.AGGRESSIVE)
                .build();
    }
    public User newMockUserWithoutPropensity(Long id, String username) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .name(username)
                .password(passwordEncoder.encode("password1234"))
                .email(username + "@nate.com")
                .phoneNumber("01012345678")
                .role(Role.USER)
                .profile("profile.png")
                .hasDoneReview(false)
                .hasDoneReservation(false)
                .hasDoneBoardBookmark(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
    public User newMockUserADMIN(Long id, String username) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .name(username)
                .password(passwordEncoder.encode("password1234"))
                .email("jisu8496@naver.com")
                .phoneNumber("01012345678")
                .role(Role.ADMIN)
                .profile("profile.png")
                .hasDoneReview(false)
                .hasDoneReservation(false)
                .hasDoneBoardBookmark(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public User newMockUserWithPropensity(Long id, String username) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .name(username)
                .password(passwordEncoder.encode("password1234"))
                .email(username + "@nate.com")
                .phoneNumber("01012345678")
                .propensity(UserPropensity.AGGRESSIVE)
                .role(Role.USER)
                .profile("profile.png")
                .hasDoneReview(false)
                .hasDoneReservation(false)
                .hasDoneBoardBookmark(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public PB newMockPB(Long id, String username, Branch branch) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return PB.builder()
                .id(id)
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
                .consultStart(DateUtil.StringToLocalTime("09:00"))
                .consultEnd(DateUtil.StringToLocalTime("18:00"))
                .consultNotice("월요일 불가능합니다")
                .role(Role.PB)
                .status(PBStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public PB newMockPBWithStatus(Long id, String username, Branch branch, PBStatus pbStatus) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return PB.builder()
                .id(id)
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
                .consultStart(DateUtil.StringToLocalTime("09:00"))
                .consultEnd(DateUtil.StringToLocalTime("18:00"))
                .consultNotice("월요일 불가능합니다")
                .role(Role.PB)
                .status(pbStatus)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public PB newPBWithSpeciality(Long id, String username, Branch branch) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return PB.builder()
                .id(id)
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
                .consultStart(DateUtil.StringToLocalTime("09:00"))
                .consultEnd(DateUtil.StringToLocalTime("18:00"))
                .consultNotice("월요일 불가능합니다")
                .role(Role.PB)
                .status(PBStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Board newMockBoard(Long id, String title, PB pb) {
        return Board.builder()
                .id(id)
                .pb(pb)
                .title(title)
                .thumbnail("thumbnail.png")
                .content("content 입니다")
                .tag1("시장정보")
                .tag2("쉽게읽혀요")
                .clickCount(0L)
                .viewCount(0L)
                .status(BoardStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Board newMockTempBoard(Long id, String title, PB pb) {
        return Board.builder()
                .id(id)
                .pb(pb)
                .title(title)
                .thumbnail("thumbnail.png")
                .content("content 입니다")
                .tag1("시장정보")
                .tag2("쉽게읽혀요")
                .clickCount(0L)
                .status(BoardStatus.TEMP)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Reply newMockUserReply(Long id, Board board, User user) {
        return Reply.builder()
                .id(id)
                .board(board)
                .authorId(user.getId())
                .authorRole(ReplyAuthorRole.USER)
                .content("댓글입니다")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public ReReply newMockUserReReply(Long id, Reply reply, User user) {
        return ReReply.builder()
                .id(id)
                .reply(reply)
                .authorId(user.getId())
                .authorRole(ReplyAuthorRole.USER)
                .content("대댓글입니다")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Reply newMockPBReply(Long id, Board board, PB pb) {
        return Reply.builder()
                .id(id)
                .board(board)
                .authorId(pb.getId())
                .authorRole(ReplyAuthorRole.PB)
                .content("댓글입니다")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public ReReply newMockPBReReply(Long id, Reply reply, PB pb) {
        return ReReply.builder()
                .id(id)
                .reply(reply)
                .authorId(pb.getId())
                .authorRole(ReplyAuthorRole.PB)
                .content("대댓글입니다")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Reservation newMockVisitReservation(Long id, User user, PB pb, ReservationProcess process) {
        return Reservation.builder()
                .id(id)
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
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Reservation newMockVisitReservationCancel(Long id, User user, PB pb) {
        return Reservation.builder()
                .id(id)
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
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Reservation newMockCallReservation(Long id, User user, PB pb, ReservationProcess process) {
        return Reservation.builder()
                .id(id)
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
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Review newMockReview(Long id, Reservation reservation) {
        return Review.builder()
                .id(id)
                .reservation(reservation)
                .content("content 입니다")
                .adherence(ReviewAdherence.EXCELLENT)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Style newMockStyle(Long id, Review review, StyleStyle styleStyle) {
        return Style.builder()
                .id(id)
                .review(review)
                .style(styleStyle)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public UserBookmark newMockUserBookmark(Long id, User user, PB pb) {
        return UserBookmark.builder()
                .id(id)
                .user(user)
                .pb(pb)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public BoardBookmark newMockBoardBookmark(Long id, User user, Board board) {
        return BoardBookmark.builder()
                .id(id)
                .bookmarkerId(user.getId())
                .bookmarkerRole(BookmarkerRole.USER)
                .board(board)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public BoardBookmark newMockBoardBookmarkByPB(Long id, PB pb, Board board) {
        return BoardBookmark.builder()
                .id(id)
                .bookmarkerId(pb.getId())
                .bookmarkerRole(BookmarkerRole.PB)
                .board(board)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Career newMockCareer(Long id, PB pb) {
        return Career.builder()
                .id(id)
                .pb(pb)
                .career("키움증권")
                .startYear(2020)
                .endYear(2022)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Award newMockAward(Long id, PB pb) {
        return Award.builder()
                .id(id)
                .pb(pb)
                .record("수상이력입니다.")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public PBAgreement newMockPBAgreement(Long id, PB pb, PBAgreementType type) {
        return PBAgreement.builder()
                .id(id)
                .pb(pb)
                .title("약관1")
                .isAgreed(true)
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Portfolio newMockPortfolio(Long id, PB pb) {
        return Portfolio.builder()
                .id(id)
                .pb(pb)
                .cumulativeReturn(90.0)
                .maxDrawdown(101.1)
                .averageProfit(14.4)
                .profitFactor(1.32)
                .file("file1.pdf")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Company newMockCompany(Long id, String name) {
        return Company.builder()
                .id(id)
                .name(name)
                .logo("logo.png")
//                .createdAt(LocalDateTime.now())

                .build();
    }

    public Branch newMockBranch(Long id, Company company, int num) {
        Double latitude = 36.36671 + num;
        Double longitude = 127.34451 + num;
        return Branch.builder()
                .id(id)
                .company(company)
                .name(company.getName() + " 여의도점")
                .roadAddress(company.getName() + " 도로명주소")
                .streetAddress(company.getName() + " 지번주소")
                .latitude(latitude)
                .longitude(longitude)

//                .createdAt(LocalDateTime.now())
                .build();
    }

    public UserAgreement newMockUserAgreement(Long id, User user, UserAgreementType type) {
        return UserAgreement.builder()
                .id(id)
                .user(user)
                .title("약관1")
                .type(type)
                .isAgreed(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public FrequentQuestion newMockFrequentQuestion(Long id) {
        return FrequentQuestion.builder()
                .id(id)
                .label("회원")
                .title("이메일이 주소가 변경되었어요.")
                .content("가입 이메일은 회원 식별 고유 키로 가입 후 변경이 불가능 하므로 개인 이메일로 가입하기를 권유드립니다.")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Notice newMockNotice(Long id) {
        return Notice.builder()
                .id(id)
                .title("서버 점검 안내")
                .content("보다 나은 환경을 제공하기 위해 개발진에서 발견한 문제 복구 및 업데이트 점검을 진행할 예정입니다.\n" +
                        "업데이트 점검을 진행하는 동안에는 접속할 수 없으니 불필요한 손해가 발생치 않도록 주의해 주세요.\n" +
                        "이로 인해 불편을 끼쳐 드려 죄송합니다.")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
