package kr.co.moneybridge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.model.pb.Company;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBRepository;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
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
    public void get_reservation_base_test() {
        // given
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");

        // stub
        Mockito.when(pbRepository.findById(anyLong()))
                .thenReturn(Optional.of(pb));
        Mockito.when(userRepository.findById(anyLong()))
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
        Mockito.when(pbRepository.findById(anyLong()))
                .thenReturn(Optional.of(pb));
        Mockito.when(reviewRepository.findAllByPbIdAndProcess(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>(Arrays.asList(
                        review5,
                        review4,
                        review3,
                        review2,
                        review)))
                );
        Mockito.when(userRepository.findUserByReviewId(anyLong()))
                .thenReturn(user);
        List<Style> styles = new ArrayList<>();
        styles.add(style);
        styles.add(style2);
        Mockito.when(styleRepository.findAllByReviewId(anyLong()))
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
}
