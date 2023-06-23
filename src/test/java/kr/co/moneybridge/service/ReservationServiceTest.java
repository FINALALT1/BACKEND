package kr.co.moneybridge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.reservation.ReservationRequest;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
import kr.co.moneybridge.dto.reservation.ReviewResponse;
import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.model.pb.Company;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBRepository;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest extends MockDummyEntity {
    @InjectMocks
    private ReservationService reservationService;
    @Mock
    private PBRepository pbRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private StyleRepository styleRepository;
    @Spy
    private ObjectMapper om;

    @Test
    public void get_my_consult_time_test() {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "이피비", branch);

        // stub
        when(pbRepository.findById(anyLong())).thenReturn(Optional.of(pb));

        // when
        ReservationResponse.MyConsultTimeDTO myConsultTimeDTO = reservationService.getMyConsultTime(pb.getId());

        // then
        assertThat(myConsultTimeDTO.getConsultStart()).isEqualTo("09:00");
        assertThat(myConsultTimeDTO.getConsultEnd()).isEqualTo("18:00");
        assertThat(myConsultTimeDTO.getConsultNotice()).isEqualTo("월요일 불가능합니다");
    }

    @Test
    public void get_my_review_test() {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "이피비", branch);
        User user = newMockUser(1L, "lee");
        Reservation reservation = newMockCallReservation(id, user, pb, ReservationProcess.COMPLETE);
        Review review = newMockReview(1L, reservation);
        List<Style> styleList = new ArrayList<>();

        // stub
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        when(styleRepository.findAllByReviewId(anyLong())).thenReturn(styleList);

        // when
        ReservationResponse.MyReviewDTO myReviewDTO = reservationService.getMyReview(id, user.getId());

        // then
        assertThat(myReviewDTO.getAdherence()).isEqualTo(ReviewAdherence.EXCELLENT);
        assertThat(myReviewDTO.getStyleList().size()).isEqualTo(0);
        assertThat(myReviewDTO.getContent()).isEqualTo("content 입니다");
    }

    @Test
    public void get_reservation_base_test() {
        // given
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");

        // stub
        when(pbRepository.findById(anyLong()))
                .thenReturn(Optional.of(pb));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        // when
        ReservationResponse.BaseDTO baseDTO = reservationService.getReservationBase(pbId, user.getId());

        // then
        assertThat(baseDTO.getPbInfo().getPbName()).isEqualTo(pb.getName());
        assertThat(baseDTO.getPbInfo().getBranchName()).isEqualTo(pb.getBranch().getName());
        assertThat(baseDTO.getPbInfo().getBranchAddress()).isEqualTo(pb.getBranch().getRoadAddress());
        assertThat(baseDTO.getPbInfo().getBranchLatitude()).isEqualTo(pb.getBranch().getLatitude());
        assertThat(baseDTO.getPbInfo().getBranchLongitude()).isEqualTo(pb.getBranch().getLongitude());
        assertThat(baseDTO.getConsultInfo().getConsultStart()).isEqualTo(MyDateUtil.localTimeToString(pb.getConsultStart()));
        assertThat(baseDTO.getConsultInfo().getConsultEnd()).isEqualTo(MyDateUtil.localTimeToString(pb.getConsultEnd()));
        assertThat(baseDTO.getConsultInfo().getNotice()).isEqualTo(pb.getConsultNotice());
        assertThat(baseDTO.getUserInfo().getUserName()).isEqualTo(user.getName());
        assertThat(baseDTO.getUserInfo().getUserPhoneNumber()).isEqualTo(user.getPhoneNumber());
        assertThat(baseDTO.getUserInfo().getUserEmail()).isEqualTo(user.getEmail());
    }

//    @Test
//    public void apply_reservation_test() throws Exception {
//        // given
//        Long pbId = 1L;
//        Company company = newMockCompany(1L, "미래에셋");
//        Branch branch = newMockBranch(1L, company, 1);
//        PB pb = newMockPB(pbId, "이피비", branch);
//        User user = newMockUser(1L, "lee");
//        ReservationRequest.ApplyReservationInDTO applyReservationInDTO = new ReservationRequest.ApplyReservationInDTO();
//        applyReservationInDTO.setGoal1(ReservationGoal.PROFIT);
//        applyReservationInDTO.setGoal2(ReservationGoal.RISK);
//        applyReservationInDTO.setReservationType(ReservationType.VISIT);
//        applyReservationInDTO.setLocationType(LocationType.BRANCH);
//        applyReservationInDTO.setLocationName("미래에셋증권 용산wm점");
//        applyReservationInDTO.setLocationAddress("서울특별시 용산구 한강로동 한강대로 92");
//        applyReservationInDTO.setCandidateTime1("2023-05-15T09:00:00");
//        applyReservationInDTO.setCandidateTime2("2023-05-15T10:00:00");
//        applyReservationInDTO.setQuestion("2023-05-15T10:00:00");
//        applyReservationInDTO.setUserName("lee");
//        applyReservationInDTO.setUserPhoneNumber("01012345678");
//        applyReservationInDTO.setUserEmail("lee@nate.com");
//        String requestBody = om.writeValueAsString(applyReservationInDTO);
//
//        // stub
//        Mockito.when(pbRepository.findById(anyLong()))
//                .thenReturn(Optional.of(pb));
//        Mockito.when(userRepository.findById(anyLong()))
//                .thenReturn(Optional.of(user));
//        Mockito.when(reservationRepository.save(any()))
//                .thenReturn(Reservation.builder()
//                        .id(1L)
//                        .user(user)
//                        .pb(pb)
//                        .type(applyReservationInDTO.getReservationType())
//                        .locationName(applyReservationInDTO.getLocationName())
//                        .locationAddress(applyReservationInDTO.getLocationAddress())
//                        .candidateTime1(MyDateUtil.StringToLocalDateTime(applyReservationInDTO.getCandidateTime1()))
//                        .candidateTime2(MyDateUtil.StringToLocalDateTime(applyReservationInDTO.getCandidateTime2()))
//                        .question(applyReservationInDTO.getQuestion())
//                        .goal1(applyReservationInDTO.getGoal1())
//                        .goal2(applyReservationInDTO.getGoal2())
//                        .process(ReservationProcess.APPLY)
//                        .investor(applyReservationInDTO.getUserName())
//                        .phoneNumber(applyReservationInDTO.getUserPhoneNumber())
//                        .email(applyReservationInDTO.getUserEmail())
//                        .status(ReservationStatus.ACTIVE)
//                        .build());
//        // when
//        Reservation reservation = reservationService.applyReservation(pbId, applyReservationInDTO, new MyUserDetails(user));
//
//        // then
//        assertThat(reservation.getId()).isEqualTo(reservation.getId());
//        assertThat(reservation.getUser()).isEqualTo(user);
//        assertThat(reservation.getPb()).isEqualTo(pb);
//        assertThat(reservation.getType()).isEqualTo(applyReservationInDTO.getReservationType());
//        assertThat(reservation.getLocationName()).isEqualTo(applyReservationInDTO.getLocationName());
//        assertThat(reservation.getLocationAddress()).isEqualTo(applyReservationInDTO.getLocationAddress());
//        assertThat(reservation.getCandidateTime1()).isEqualTo(MyDateUtil.StringToLocalDateTime(applyReservationInDTO.getCandidateTime1()));
//        assertThat(reservation.getCandidateTime2()).isEqualTo(MyDateUtil.StringToLocalDateTime(applyReservationInDTO.getCandidateTime2()));
//        assertThat(reservation.getQuestion()).isEqualTo(applyReservationInDTO.getQuestion());
//        assertThat(reservation.getGoal1()).isEqualTo(applyReservationInDTO.getGoal1());
//        assertThat(reservation.getGoal2()).isEqualTo(applyReservationInDTO.getGoal2());
//        assertThat(reservation.getProcess()).isEqualTo(ReservationProcess.APPLY);
//        assertThat(reservation.getInvestor()).isEqualTo(applyReservationInDTO.getUserName());
//        assertThat(reservation.getPhoneNumber()).isEqualTo(applyReservationInDTO.getUserPhoneNumber());
//        assertThat(reservation.getEmail()).isEqualTo(applyReservationInDTO.getUserEmail());
//        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.ACTIVE);
//    }

    @Test
    public void get_reviews_test() {
        // given
        int page = 0;
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");
        Reservation reservation = newMockVisitReservation(1L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation2 = newMockVisitReservation(2L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation3 = newMockVisitReservation(3L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation4 = newMockVisitReservation(4L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation5 = newMockVisitReservation(5L, user, pb, ReservationProcess.COMPLETE);
        Review review = newMockReview(1L, reservation);
        Review review2 = newMockReview(2L, reservation2);
        Review review3 = newMockReview(3L, reservation3);
        Review review4 = newMockReview(4L, reservation4);
        Review review5 = newMockReview(5L, reservation5);
        Style style = newMockStyle(1L, review, StyleStyle.FAST);
        Style style2 = newMockStyle(2L, review, StyleStyle.KIND);
        Style style3 = newMockStyle(3L, review, StyleStyle.DIRECTIONAL);

        // stub
        when(pbRepository.findById(anyLong()))
                .thenReturn(Optional.of(pb));
        when(reviewRepository.findAllByPbIdAndProcess(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>(Arrays.asList(
                        review5,
                        review4,
                        review3,
                        review2,
                        review)))
                );
        when(userRepository.findUserByReviewId(anyLong()))
                .thenReturn(user);
        List<Style> styles = new ArrayList<>();
        styles.add(style);
        styles.add(style2);
        when(styleRepository.findAllByReviewId(anyLong()))
                .thenReturn(styles);

        // when
        PageDTO<ReservationResponse.ReviewDTO> reviewsOutDTO = reservationService.getReviews(pbId, 0);

        // then
        assertThat(reviewsOutDTO.getList().get(0).getReviewId()).isEqualTo(5L);
        assertThat(reviewsOutDTO.getList().get(0).getUsername()).isEqualTo("lee");
        assertThat(reviewsOutDTO.getList().get(0).getContent()).isEqualTo("content 입니다");
        assertThat(reviewsOutDTO.getList().get(0).getCreatedAt()).matches("^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");
        assertThat(reviewsOutDTO.getList().get(0).getList().get(0).getStyle()).isEqualTo(StyleStyle.FAST);
        assertThat(reviewsOutDTO.getList().get(0).getList().get(1).getStyle()).isEqualTo(StyleStyle.KIND);
        assertThat(reviewsOutDTO.getList().get(1).getReviewId()).isEqualTo(4L);
        assertThat(reviewsOutDTO.getList().get(1).getUsername()).isEqualTo("lee");
        assertThat(reviewsOutDTO.getList().get(1).getContent()).isEqualTo("content 입니다");
        assertThat(reviewsOutDTO.getList().get(1).getCreatedAt()).matches("^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");
        assertThat(reviewsOutDTO.getList().get(1).getList().get(0).getStyle()).isEqualTo(StyleStyle.FAST);
        assertThat(reviewsOutDTO.getList().get(1).getList().get(1).getStyle()).isEqualTo(StyleStyle.KIND);
        assertThat(reviewsOutDTO.getList().get(2).getReviewId()).isEqualTo(3L);
        assertThat(reviewsOutDTO.getList().get(2).getUsername()).isEqualTo("lee");
        assertThat(reviewsOutDTO.getList().get(2).getContent()).isEqualTo("content 입니다");
        assertThat(reviewsOutDTO.getList().get(2).getCreatedAt()).matches("^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");
        assertThat(reviewsOutDTO.getList().get(3).getReviewId()).isEqualTo(2L);
        assertThat(reviewsOutDTO.getList().get(3).getUsername()).isEqualTo("lee");
        assertThat(reviewsOutDTO.getList().get(3).getContent()).isEqualTo("content 입니다");
        assertThat(reviewsOutDTO.getList().get(3).getCreatedAt()).matches("^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");
        assertThat(reviewsOutDTO.getList().get(4).getReviewId()).isEqualTo(1L);
        assertThat(reviewsOutDTO.getList().get(4).getUsername()).isEqualTo("lee");
        assertThat(reviewsOutDTO.getList().get(4).getContent()).isEqualTo("content 입니다");
        assertThat(reviewsOutDTO.getList().get(4).getCreatedAt()).matches("^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");
    }

    @Test
    public void get_recent_reservation_info_test() {
        // given
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");
        Reservation reservation = newMockVisitReservation(1L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation2 = newMockVisitReservation(2L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation3 = newMockVisitReservation(3L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation4 = newMockVisitReservation(4L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation5 = newMockVisitReservation(5L, user, pb, ReservationProcess.COMPLETE);

        // stub
        when(pbRepository.findById(anyLong()))
                .thenReturn(Optional.of(pb));
        when(reservationRepository.countByPBIdAndProcess(anyLong(), any()))
                .thenReturn(10);
        when(reservationRepository.countRecentByPBIdAndProcess(anyLong(), any()))
                .thenReturn(1);


        // when
        ReservationResponse.RecentInfoDTO recentInfoDTO = reservationService.getRecentReservationInfo(pbId);

        // then
        assertThat(recentInfoDTO.getApplyCount()).isEqualTo(10);
        assertThat(recentInfoDTO.getIsNewApply()).isEqualTo(true);
        assertThat(recentInfoDTO.getConfirmCount()).isEqualTo(10);
        assertThat(recentInfoDTO.getIsNewConfirm()).isEqualTo(true);
        assertThat(recentInfoDTO.getCompleteCount()).isEqualTo(10);
        assertThat(recentInfoDTO.getIsNewComplete()).isEqualTo(true);
    }

    @Test
    public void get_recent_reservation_info_by_user_test() {
        // given
        Long userId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "이피비", branch);
        User user = newMockUser(userId, "lee");
        Reservation reservation = newMockVisitReservation(1L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation2 = newMockVisitReservation(2L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation3 = newMockVisitReservation(3L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation4 = newMockVisitReservation(4L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation5 = newMockVisitReservation(5L, user, pb, ReservationProcess.COMPLETE);

        // stub
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(reservationRepository.countByUserIdAndProcess(anyLong(), any()))
                .thenReturn(10);
        when(reservationRepository.countRecentByUserIdAndProcess(anyLong(), any()))
                .thenReturn(1);


        // when
        ReservationResponse.RecentInfoDTO recentInfoDTO = reservationService.getRecentReservationInfoByUser(userId);

        // then
        assertThat(recentInfoDTO.getApplyCount()).isEqualTo(10);
        assertThat(recentInfoDTO.getIsNewApply()).isEqualTo(true);
        assertThat(recentInfoDTO.getConfirmCount()).isEqualTo(10);
        assertThat(recentInfoDTO.getIsNewConfirm()).isEqualTo(true);
        assertThat(recentInfoDTO.getCompleteCount()).isEqualTo(10);
        assertThat(recentInfoDTO.getIsNewComplete()).isEqualTo(true);
    }

    @Test
    public void get_recent_reservations_test() {
        // given
        String type = "COMPLETE";
        int page = 0;
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");
        Reservation reservation = newMockVisitReservation(1L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation2 = newMockVisitReservation(2L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation3 = newMockVisitReservation(3L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation4 = newMockVisitReservation(4L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation5 = newMockVisitReservation(5L, user, pb, ReservationProcess.COMPLETE);

        // stub
        when(pbRepository.findById(anyLong()))
                .thenReturn(Optional.of(pb));
        when(reservationRepository.findAllByPbIdAndProcess(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>(Arrays.asList(
                        new ReservationResponse.RecentPagingDTO(reservation5, user),
                        new ReservationResponse.RecentPagingDTO(reservation4, user),
                        new ReservationResponse.RecentPagingDTO(reservation3, user),
                        new ReservationResponse.RecentPagingDTO(reservation2, user),
                        new ReservationResponse.RecentPagingDTO(reservation, user)
                ))));

        // when
        PageDTO<ReservationResponse.RecentReservationDTO> recentReservationsDTO = reservationService.getRecentReservations(type, page, pbId);

        // then
        assertThat(recentReservationsDTO.getList().get(0).getReservationId()).isEqualTo(5L);
        assertThat(recentReservationsDTO.getList().get(0).getIsNewReservation()).isEqualTo(true);
        assertThat(recentReservationsDTO.getList().get(0).getUserId()).isEqualTo(1L);
        assertThat(recentReservationsDTO.getList().get(0).getProfileImage()).isEqualTo("profile.png");
        assertThat(recentReservationsDTO.getList().get(0).getName()).isEqualTo("lee");
        assertThat(recentReservationsDTO.getList().get(0).getCreatedAt()).matches("^\\d{4}년 \\d{1,2}월 \\d{1,2}일 (오전|오후) \\d{1,2}시 \\d{1,2}분$");
        assertThat(recentReservationsDTO.getList().get(0).getType()).isEqualTo(ReservationType.VISIT);
    }

    @Test
    public void get_recent_reservations_by_user_test() {
        // given
        String type = "COMPLETE";
        int page = 0;
        Long userId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "이피비", branch);
        User user = newMockUser(userId, "lee");
        Reservation reservation = newMockVisitReservation(1L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation2 = newMockVisitReservation(2L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation3 = newMockVisitReservation(3L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation4 = newMockVisitReservation(4L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation5 = newMockVisitReservation(5L, user, pb, ReservationProcess.COMPLETE);

        // stub
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(reservationRepository.findAllByUserIdAndProcess(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>(Arrays.asList(
                        new ReservationResponse.RecentPagingByUserDTO(reservation5, pb),
                        new ReservationResponse.RecentPagingByUserDTO(reservation4, pb),
                        new ReservationResponse.RecentPagingByUserDTO(reservation3, pb),
                        new ReservationResponse.RecentPagingByUserDTO(reservation2, pb),
                        new ReservationResponse.RecentPagingByUserDTO(reservation, pb)
                ))));

        // when
        PageDTO<ReservationResponse.RecentReservationByUserDTO> recentReservationsDTO = reservationService.getRecentReservationsByUser(type, page, userId);

        // then
        assertThat(recentReservationsDTO.getList().get(0).getReservationId()).isEqualTo(5L);
        assertThat(recentReservationsDTO.getList().get(0).getIsNewReservation()).isEqualTo(true);
        assertThat(recentReservationsDTO.getList().get(0).getPbId()).isEqualTo(1L);
        assertThat(recentReservationsDTO.getList().get(0).getProfileImage()).isEqualTo("profile.png");
        assertThat(recentReservationsDTO.getList().get(0).getName()).isEqualTo("이피비");
        assertThat(recentReservationsDTO.getList().get(0).getCreatedAt()).matches("^\\d{4}년 \\d{1,2}월 \\d{1,2}일 (오전|오후) \\d{1,2}시 \\d{1,2}분$");
        assertThat(recentReservationsDTO.getList().get(0).getType()).isEqualTo(ReservationType.VISIT);
    }

    @Test
    public void get_reservation_detail_test() {
        // given
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");
        Reservation reservation = newMockVisitReservation(1L, user, pb, ReservationProcess.COMPLETE);

        // stub
        when(reservationRepository.findById(anyLong()))
                .thenReturn(Optional.of(reservation));
        when(pbRepository.findById(anyLong()))
                .thenReturn(Optional.of(pb));
        when(reviewRepository.countByReservationId(anyLong()))
                .thenReturn(0);

        // when
        ReservationResponse.DetailByPBDTO detailByPBDTO = reservationService.getReservationDetailByPB(reservation.getId(), pbId);

        // then
        assertThat(detailByPBDTO.getUserId()).isEqualTo(1L);
        assertThat(detailByPBDTO.getProfileImage()).isEqualTo("profile.png");
        assertThat(detailByPBDTO.getName()).isEqualTo("lee");
        assertThat(detailByPBDTO.getPhoneNumber()).isEqualTo("01012345678");
        assertThat(detailByPBDTO.getEmail()).isEqualTo("lee@nate.com");
        assertThat(detailByPBDTO.getReservationId()).isEqualTo(1L);
        assertThat(detailByPBDTO.getCandidateTime1()).matches("^\\d{4}년 \\d{1,2}월 \\d{1,2}일 (오전|오후) \\d{1,2}시 \\d{1,2}분$");
        assertThat(detailByPBDTO.getCandidateTime2()).matches("^\\d{4}년 \\d{1,2}월 \\d{1,2}일 (오전|오후) \\d{1,2}시 \\d{1,2}분$");
        assertThat(detailByPBDTO.getTime()).matches("^\\d{4}년 \\d{1,2}월 \\d{1,2}일 (오전|오후) \\d{1,2}시 \\d{1,2}분$");
        assertThat(detailByPBDTO.getType()).isEqualTo(ReservationType.VISIT);
        assertThat(detailByPBDTO.getLocation()).isEqualTo("kb증권 강남중앙점");
        assertThat(detailByPBDTO.getLocationAddress()).isEqualTo("강남구 강남중앙로 10");
        assertThat(detailByPBDTO.getGoal()).isEqualTo(ReservationGoal.PROFIT);
        assertThat(detailByPBDTO.getQuestion()).isEqualTo("질문입니다...");
        assertThat(detailByPBDTO.getReviewCheck()).isEqualTo(false);
    }

    @Test
    public void get_reservation_detail_by_user_test() {
        // given
        Long userId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "이피비", branch);
        User user = newMockUser(userId, "lee");
        Reservation reservation = newMockVisitReservation(1L, user, pb, ReservationProcess.COMPLETE);

        // stub
        when(reservationRepository.findById(anyLong()))
                .thenReturn(Optional.of(reservation));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(reviewRepository.countByReservationId(anyLong()))
                .thenReturn(0);

        // when
        ReservationResponse.DetailByUserDTO detailByUserDTO = reservationService.getReservationDetailByUser(reservation.getId(), userId);

        // then
        assertThat(detailByUserDTO.getPbId()).isEqualTo(1L);
        assertThat(detailByUserDTO.getProfileImage()).isEqualTo("profile.png");
        assertThat(detailByUserDTO.getName()).isEqualTo("이피비");
        assertThat(detailByUserDTO.getPhoneNumber()).isEqualTo("01012345678");
        assertThat(detailByUserDTO.getEmail()).isEqualTo("이피비@nate.com");
        assertThat(detailByUserDTO.getReservationId()).isEqualTo(1L);
        assertThat(detailByUserDTO.getCandidateTime1()).matches("^\\d{4}년 \\d{1,2}월 \\d{1,2}일 (오전|오후) \\d{1,2}시 \\d{1,2}분$");
        assertThat(detailByUserDTO.getCandidateTime2()).matches("^\\d{4}년 \\d{1,2}월 \\d{1,2}일 (오전|오후) \\d{1,2}시 \\d{1,2}분$");
        assertThat(detailByUserDTO.getTime()).matches("^\\d{4}년 \\d{1,2}월 \\d{1,2}일 (오전|오후) \\d{1,2}시 \\d{1,2}분$");
        assertThat(detailByUserDTO.getType()).isEqualTo(ReservationType.VISIT);
        assertThat(detailByUserDTO.getLocation()).isEqualTo("kb증권 강남중앙점");
        assertThat(detailByUserDTO.getLocationAddress()).isEqualTo("강남구 강남중앙로 10");
        assertThat(detailByUserDTO.getGoal()).isEqualTo(ReservationGoal.PROFIT);
        assertThat(detailByUserDTO.getQuestion()).isEqualTo("질문입니다...");
        assertThat(detailByUserDTO.getReviewCheck()).isEqualTo(false);
    }

//    @Test
//    public void update_reservation_test() {
//        // given
//        Long pbId = 1L;
//        Company company = newMockCompany(1L, "미래에셋");
//        Branch branch = newMockBranch(1L, company, 1);
//        PB pb = newMockPB(pbId, "이피비", branch);
//        User user = newMockUser(1L, "lee");
//        Reservation reservation = newMockVisitReservation(1L, user, pb, ReservationProcess.APPLY);
//        ReservationRequest.UpdateDTO updateDTO = new ReservationRequest.UpdateDTO();
//        updateDTO.setTime("2024년 6월 1일 오전 9시 20분");
//        updateDTO.setType(ReservationType.CALL);
//
//        // stub
//        Mockito.when(reservationRepository.findById(anyLong()))
//                .thenReturn(Optional.of(reservation));
//        Mockito.when(userRepository.findById(anyLong()))
//                .thenReturn(Optional.of(user));
//
//        // when
//        LocalDateTime localDateTime = reservationService.updateReservation(reservation.getId(), updateDTO, new MyUserDetails(user));
//
//        // then
//        assertThat(localDateTime).isEqualTo(StringToLocalDateTime("2024년 6월 1일 오전 9시 20분"));
//    }

//    @Test
//    public void cancel_reservation_test() {
//        // given
//        Long pbId = 1L;
//        Company company = newMockCompany(1L, "미래에셋");
//        Branch branch = newMockBranch(1L, company, 1);
//        PB pb = newMockPB(pbId, "이피비", branch);
//        User user = newMockUser(1L, "lee");
//        Reservation reservation = newMockVisitReservation(1L, user, pb, ReservationProcess.APPLY);
//
//        // stub
//        Mockito.when(reservationRepository.findById(anyLong()))
//                .thenReturn(Optional.of(reservation));
//        Mockito.when(userRepository.findById(anyLong()))
//                .thenReturn(Optional.of(user));
//
//        // when
//        Reservation cancelReservation = reservationService.cancelReservation(reservation.getId(), new MyUserDetails(user));
//
//        // then
//        assertThat(cancelReservation.getStatus()).isEqualTo(ReservationStatus.CANCEL);
//    }

//    @Test
//    public void confirm_reservation_test() {
//        // given
//        Long pbId = 1L;
//        Company company = newMockCompany(1L, "미래에셋");
//        Branch branch = newMockBranch(1L, company, 1);
//        PB pb = newMockPB(pbId, "이피비", branch);
//        User user = newMockUser(1L, "lee");
//        Reservation reservation = newMockVisitReservation(1L, user, pb, ReservationProcess.APPLY);
//        ReservationRequest.ConfirmDTO confirmDTO = new ReservationRequest.ConfirmDTO();
//        confirmDTO.setTime("2024년 6월 1일 오전 9시 20분");
//
//        // stub
//        Mockito.when(reservationRepository.findById(anyLong()))
//                .thenReturn(Optional.of(reservation));
//        Mockito.when(pbRepository.findById(anyLong()))
//                .thenReturn(Optional.of(pb));
//
//        // when
//        Reservation confirmReservation = reservationService.confirmReservation(reservation.getId(), pbId, confirmDTO);
//
//        // then
//        assertThat(confirmReservation.getTime()).isEqualTo(StringToLocalDateTime("2024년 6월 1일 오전 9시 20분"));
//        assertThat(confirmReservation.getProcess()).isEqualTo(ReservationProcess.CONFIRM);
//    }

//    @Test
//    public void complete_reservation_test() {
//        // given
//        Long pbId = 1L;
//        Company company = newMockCompany(1L, "미래에셋");
//        Branch branch = newMockBranch(1L, company, 1);
//        PB pb = newMockPB(pbId, "이피비", branch);
//        User user = newMockUser(1L, "lee");
//        Reservation reservation = newMockVisitReservation(1L, user, pb, ReservationProcess.CONFIRM);
//
//        // stub
//        Mockito.when(reservationRepository.findById(anyLong()))
//                .thenReturn(Optional.of(reservation));
//        Mockito.when(pbRepository.findById(anyLong()))
//                .thenReturn(Optional.of(pb));
//
//        // when
//        Reservation completeReservation = reservationService.completeReservation(reservation.getId(), new MyUserDetails(pb));
//
//        // then
//        assertThat(completeReservation.getProcess()).isEqualTo(ReservationProcess.COMPLETE);
//    }

    @Test
    public void write_review_test() {
        // given
        Long userId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "이피비", branch);
        User user = newMockUser(userId, "lee");
        Reservation reservation = newMockVisitReservation(1L, user, pb, ReservationProcess.COMPLETE);
        Review review = newMockReview(1L, reservation);
        ReservationRequest.ReviewDTO reviewDTO = new ReservationRequest.ReviewDTO();
        reviewDTO.setReservationId(reservation.getId());
        reviewDTO.setAdherence(ReviewAdherence.EXCELLENT);
        reviewDTO.setStyleList(
                new ArrayList<>(
                        Arrays.asList(
                                StyleStyle.HONEST,
                                StyleStyle.EXPERIENCED
                        )
                )
        );

        // stub
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(reservationRepository.findById(anyLong()))
                .thenReturn(Optional.of(reservation));
        when(reviewRepository.countByReservationId(anyLong()))
                .thenReturn(0);
        when(reviewRepository.save(any()))
                .thenReturn(review);
        when(styleRepository.save(any()))
                .thenReturn(newMockStyle(1L, review, StyleStyle.HONEST));

        // when
        ReservationResponse.ReviewIdDTO reviewIdDTO = reservationService.writeReview(reviewDTO, userId);

        // then
        assertThat(reviewIdDTO.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("PB의 상담후기 탑3 가져오기")
    void getPBStyles() {
        //given
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "이피비", branch);
        List<StyleStyle> styleList = Arrays.asList(StyleStyle.FAST, StyleStyle.KIND, StyleStyle.HONEST);

        //stub
        when(pbRepository.findById(1L)).thenReturn(Optional.of(pb));
        when(styleRepository.findStylesByPbId(1L)).thenReturn(styleList);

        //when
        ReviewResponse.PBTopStyleDTO result = reservationService.getPBStyles(1L);

        //then
        assertThat(result.getStyle1()).isEqualTo(styleList.get(0));
        assertThat(result.getStyle2()).isEqualTo(styleList.get(1));
        assertThat(result.getStyle3()).isEqualTo(styleList.get(2));
    }

    @Test
    public void get_reservations_by_date_test() {
        // given
        int year = 2023;
        int month = 6;
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");
        Reservation reservation = newMockVisitReservation(1L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation2 = newMockVisitReservation(2L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation3 = newMockVisitReservation(3L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation4 = newMockVisitReservation(4L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation5 = newMockVisitReservationCancel(5L, user, pb);
        List<ReservationResponse.ReservationInfoDTO> reservations = new ArrayList<>(
                Arrays.asList(
                        new ReservationResponse.ReservationInfoDTO(reservation, user),
                        new ReservationResponse.ReservationInfoDTO(reservation2, user),
                        new ReservationResponse.ReservationInfoDTO(reservation3, user),
                        new ReservationResponse.ReservationInfoDTO(reservation4, user),
                        new ReservationResponse.ReservationInfoDTO(reservation5, user)
                )
        );


        // stub
        Mockito.when(pbRepository.findById(anyLong()))
                .thenReturn(Optional.of(pb));
        Mockito.when(reservationRepository.findAllByPbIdWithoutCancel(anyLong()))
                .thenReturn(reservations);

        // when
        List<ReservationResponse.ReservationInfoDTO> response = reservationService.getReservationsByDate(year, month, pbId);

        // then
        assertThat(response.get(0).getId()).isEqualTo(1L);
        assertThat(response.get(0).getDay().toString()).matches("^\\d{4}-\\d{2}-\\d{2}$");
        assertThat(response.get(0).getTime()).matches("^\\d{2}:\\d{2}$");
        assertThat(response.get(0).getType()).isEqualTo(ReservationType.VISIT);
        assertThat(response.get(0).getProcess()).isEqualTo(ReservationProcess.COMPLETE);
    }
}
