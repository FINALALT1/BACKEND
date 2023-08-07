package kr.co.moneybridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.WithMockPB;
import kr.co.moneybridge.core.WithMockUser;
import kr.co.moneybridge.core.advice.LogAdvice;
import kr.co.moneybridge.core.advice.ValidAdvice;
import kr.co.moneybridge.core.config.FilterRegisterConfig;
import kr.co.moneybridge.core.config.SecurityConfig;
import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.core.util.DateUtil;
import kr.co.moneybridge.core.util.MemberUtil;
import kr.co.moneybridge.core.util.RedisUtil;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.reservation.ReservationRequest;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.model.pb.Company;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static kr.co.moneybridge.core.util.DateUtil.localDateTimeToStringV2;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@EnableAspectJAutoProxy
@Import({
        LogAdvice.class,
        ValidAdvice.class,
        FilterRegisterConfig.class,
        SecurityConfig.class,
        RedisUtil.class,
})
@WebMvcTest(
        controllers = {ReservationController.class}
)
public class ReservationControllerUnitTest extends MockDummyEntity {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean
    private ReservationService reservationService;
    @MockBean
    private RedisTemplate redisTemplate;
    @MockBean
    private MemberUtil memberUtil;

    @WithMockPB
    @Test
    public void get_my_consult_time_test() throws Exception {
        // given
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "이피비", branch);

        ReservationResponse.MyConsultTimeDTO myConsultTimeDTO = new ReservationResponse.MyConsultTimeDTO(pb);

        // stub
        Mockito.when(reservationService.getMyConsultTime(anyLong())).thenReturn(myConsultTimeDTO);

        // when
        ResultActions resultActions = mvc.perform(get("/pb/consultTime"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.consultStart").value("09:00"));
        resultActions.andExpect(jsonPath("$.data.consultEnd").value("18:00"));
        resultActions.andExpect(jsonPath("$.data.consultNotice").value("월요일 불가능합니다"));
        resultActions.andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    public void get_my_review_test() throws Exception {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "이피비", branch);
        User user = newMockUser(1L, "lee");
        Reservation reservation = newMockCallReservation(id, user, pb, ReservationProcess.COMPLETE);
        Review review = newMockReview(1L, reservation);
        List<ReservationResponse.StyleDTO> styleList = new ArrayList<>();

        ReservationResponse.MyReviewDTO myReviewDTO = new ReservationResponse.MyReviewDTO(
                review, styleList);

        // stub
        Mockito.when(reservationService.getMyReview(anyLong(), any())).thenReturn(myReviewDTO);

        // when
        ResultActions resultActions = mvc.perform(get("/user/review/{id}", id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.adherence").value("EXCELLENT"));
        resultActions.andExpect(jsonPath("$.data.styleList").isEmpty());
        resultActions.andExpect(jsonPath("$.data.content").value("content 입니다"));
        resultActions.andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    public void get_reservation_base_test() throws Exception {
        // given
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");

        // stub
        Mockito.when(reservationService.getReservationBase(anyLong(), any()))
                .thenReturn(new ReservationResponse.BaseDTO(
                        new ReservationResponse.PBInfoDTO(
                                pb.getName(),
                                pb.getBranch().getName(),
                                pb.getBranch().getRoadAddress(),
                                pb.getBranch().getLatitude(),
                                pb.getBranch().getLongitude()
                        ),
                        new ReservationResponse.ConsultInfoDTO(
                                DateUtil.localTimeToString(pb.getConsultStart()),
                                DateUtil.localTimeToString(pb.getConsultEnd()),
                                pb.getConsultNotice()
                        ),
                        new ReservationResponse.UserInfoDTO(
                                user.getName(),
                                user.getPhoneNumber(),
                                user.getEmail()
                        )
                ));

        // when
        ResultActions resultActions = mvc.perform(get("/user/reservation/base/{pbId}", pbId));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.pbInfo.pbName").value(pb.getName()));
        resultActions.andExpect(jsonPath("$.data.pbInfo.branchName").value(pb.getBranch().getName()));
        resultActions.andExpect(jsonPath("$.data.pbInfo.branchAddress").value(pb.getBranch().getRoadAddress()));
        resultActions.andExpect(jsonPath("$.data.pbInfo.branchLatitude").value(pb.getBranch().getLatitude()));
        resultActions.andExpect(jsonPath("$.data.pbInfo.branchLongitude").value(pb.getBranch().getLongitude()));
        resultActions.andExpect(jsonPath("$.data.consultInfo.consultStart").value(DateUtil.localTimeToString(pb.getConsultStart())));
        resultActions.andExpect(jsonPath("$.data.consultInfo.consultEnd").value(DateUtil.localTimeToString(pb.getConsultEnd())));
        resultActions.andExpect(jsonPath("$.data.consultInfo.notice").value(pb.getConsultNotice()));
        resultActions.andExpect(jsonPath("$.data.userInfo.userName").value(user.getName()));
        resultActions.andExpect(jsonPath("$.data.userInfo.userPhoneNumber").value(user.getPhoneNumber()));
        resultActions.andExpect(jsonPath("$.data.userInfo.userEmail").value(user.getEmail()));

        resultActions.andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    public void add_reservation_test() throws Exception {
        // given
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");
        ReservationRequest.ApplyDTO applyDTO = new ReservationRequest.ApplyDTO();
        applyDTO.setGoal(ReservationGoal.PROFIT);
        applyDTO.setReservationType(ReservationType.VISIT);
        applyDTO.setLocationType(LocationType.BRANCH);
        applyDTO.setCandidateTime1(LocalDateTime.now().plusHours(10));
        applyDTO.setCandidateTime2(LocalDateTime.now().plusHours(10));
        applyDTO.setQuestion("question입니다.");
        applyDTO.setUserName("lee");
        applyDTO.setUserPhoneNumber("01012345678");
        applyDTO.setUserEmail("lee@nate.com");
        String requestBody = om.writeValueAsString(applyDTO);

        // stub

        // when
        ResultActions resultActions = mvc.perform(post("/user/reservation/{pbId}", pbId)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
    }

    @WithMockPB
    @Test
    public void get_recent_reservation_info_test() throws Exception {
        // given
        ReservationResponse.RecentInfoDTO recentInfoDTO = new ReservationResponse.RecentInfoDTO(
                2L,
                true,
                3L,
                false,
                0L,
                false
        );

        // stub
        Mockito.when(reservationService.getRecentReservationInfo(anyLong())).thenReturn(recentInfoDTO);

        // when
        ResultActions resultActions = mvc.perform(get("/pb/management/recent"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.applyCount").value(2L));
        resultActions.andExpect(jsonPath("$.data.isNewApply").value(true));
        resultActions.andExpect(jsonPath("$.data.confirmCount").value(3L));
        resultActions.andExpect(jsonPath("$.data.isNewConfirm").value(false));
        resultActions.andExpect(jsonPath("$.data.completeCount").value(0L));
        resultActions.andExpect(jsonPath("$.data.isNewComplete").value(false));
    }

    @WithMockUser
    @Test
    public void get_recent_reservation_info_by_user_test() throws Exception {
        // given
        ReservationResponse.RecentInfoDTO recentInfoDTO = new ReservationResponse.RecentInfoDTO(
                2L,
                true,
                3L,
                false,
                0L,
                false
        );

        // stub
        Mockito.when(reservationService.getRecentReservationInfoByUser(anyLong())).thenReturn(recentInfoDTO);

        // when
        ResultActions resultActions = mvc.perform(get("/user/reservations/recent"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.applyCount").value(2L));
        resultActions.andExpect(jsonPath("$.data.isNewApply").value(true));
        resultActions.andExpect(jsonPath("$.data.confirmCount").value(3L));
        resultActions.andExpect(jsonPath("$.data.isNewConfirm").value(false));
        resultActions.andExpect(jsonPath("$.data.completeCount").value(0L));
        resultActions.andExpect(jsonPath("$.data.isNewComplete").value(false));
    }

    @WithMockPB
    @Test
    public void get_recent_reservations_test() throws Exception {
        // given
        String type = "COMPLETE";
        int page = 0;
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");
        Reservation reservation1 = newMockVisitReservation(1L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation2 = newMockVisitReservation(2L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation3 = newMockVisitReservation(3L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation4 = newMockVisitReservation(4L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation5 = newMockVisitReservation(5L, user, pb, ReservationProcess.COMPLETE);

        Page<ReservationResponse.RecentPagingDTO> reservations = new PageImpl<>(
                new ArrayList<>(Arrays.asList(
                        new ReservationResponse.RecentPagingDTO(reservation5, user),
                        new ReservationResponse.RecentPagingDTO(reservation4, user),
                        new ReservationResponse.RecentPagingDTO(reservation3, user),
                        new ReservationResponse.RecentPagingDTO(reservation2, user),
                        new ReservationResponse.RecentPagingDTO(reservation1, user)
                )));
        List<ReservationResponse.RecentReservationDTO> reservationDTOs = new ArrayList<>();
        for (ReservationResponse.RecentPagingDTO reservation : reservations) {
            reservationDTOs.add(
                    new ReservationResponse.RecentReservationDTO(
                            reservation.getReservationId(),
                            Duration.between(LocalDateTime.now().minusHours(24),
                                    reservation.getCreatedAt()).toHours() <= 24,
                            reservation.getUserId(),
                            reservation.getProfileImage(),
                            reservation.getName(),
                            localDateTimeToStringV2(reservation.getCreatedAt()),
                            reservation.getType()
                    )
            );
        }
        PageDTO<ReservationResponse.RecentReservationDTO> recentReservationsDTO = new PageDTO<>(
                reservationDTOs,
                reservations,
                ReservationResponse.RecentPagingDTO.class
        );

        // stub
        Mockito.when(reservationService.getRecentReservations(any(), anyInt(), anyLong())).thenReturn(recentReservationsDTO);

        // when
        ResultActions resultActions = mvc.perform(
                get("/pb/management/reservations")
                        .param("type", type)
                        .param("page", String.valueOf(page))
        );
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].reservationId").value(5L));
        resultActions.andExpect(jsonPath("$.data.list[0].isNewReservation").value(true));
        resultActions.andExpect(jsonPath("$.data.list[0].userId").value(1L));
        resultActions.andExpect(jsonPath("$.data.list[0].profileImage").value("profile.png"));
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("lee"));
    }

    @WithMockUser
    @Test
    public void get_recent_reservations_by_user_test() throws Exception {
        // given
        String type = "COMPLETE";
        int page = 0;
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");
        Reservation reservation1 = newMockVisitReservation(1L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation2 = newMockVisitReservation(2L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation3 = newMockVisitReservation(3L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation4 = newMockVisitReservation(4L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation5 = newMockVisitReservation(5L, user, pb, ReservationProcess.COMPLETE);

        Page<ReservationResponse.RecentPagingByUserDTO> reservations = new PageImpl<>(
                new ArrayList<>(Arrays.asList(
                        new ReservationResponse.RecentPagingByUserDTO(reservation5, pb),
                        new ReservationResponse.RecentPagingByUserDTO(reservation4, pb),
                        new ReservationResponse.RecentPagingByUserDTO(reservation3, pb),
                        new ReservationResponse.RecentPagingByUserDTO(reservation2, pb),
                        new ReservationResponse.RecentPagingByUserDTO(reservation1, pb)
                )));
        List<ReservationResponse.RecentReservationByUserDTO> reservationDTOs = new ArrayList<>();
        for (ReservationResponse.RecentPagingByUserDTO reservation : reservations) {
            reservationDTOs.add(
                    new ReservationResponse.RecentReservationByUserDTO(
                            reservation.getReservationId(),
                            Duration.between(LocalDateTime.now().minusHours(24),
                                    reservation.getCreatedAt()).toHours() <= 24,
                            reservation.getPbId(),
                            reservation.getProfileImage(),
                            reservation.getName(),
                            localDateTimeToStringV2(reservation.getCreatedAt()),
                            reservation.getType()
                    )
            );
        }
        PageDTO<ReservationResponse.RecentReservationByUserDTO> recentReservationsDTO = new PageDTO<>(
                reservationDTOs,
                reservations,
                ReservationResponse.RecentPagingByUserDTO.class
        );

        // stub
        Mockito.when(reservationService.getRecentReservationsByUser(any(), anyInt(), anyLong())).thenReturn(recentReservationsDTO);

        // when
        ResultActions resultActions = mvc.perform(
                get("/user/reservations")
                        .param("type", type)
                        .param("page", String.valueOf(page))
        );
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].reservationId").value(5L));
        resultActions.andExpect(jsonPath("$.data.list[0].isNewReservation").value(true));
        resultActions.andExpect(jsonPath("$.data.list[0].pbId").value(1L));
        resultActions.andExpect(jsonPath("$.data.list[0].profileImage").value("profile.png"));
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("이피비"));
    }

    @WithMockPB
    @Test
    public void get_reservation_detail_test() throws Exception {
        // given
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");
        Reservation reservation1 = newMockVisitReservation(1L, user, pb, ReservationProcess.COMPLETE);
        ReservationResponse.DetailByPBDTO detailByPBDTO = new ReservationResponse.DetailByPBDTO(
                reservation1.getUser().getId(),
                reservation1.getUser().getProfile(),
                reservation1.getUser().getName(),
                reservation1.getUser().getPhoneNumber(),
                reservation1.getUser().getEmail(),
                reservation1.getId(),
                localDateTimeToStringV2(reservation1.getCandidateTime1()),
                localDateTimeToStringV2(reservation1.getCandidateTime2()),
                localDateTimeToStringV2(reservation1.getTime()),
                reservation1.getType(),
                reservation1.getLocationName(),
                reservation1.getLocationAddress(),
                reservation1.getGoal(),
                reservation1.getQuestion(),
                reservation1.getPb().getConsultStart().toString(),
                reservation1.getPb().getConsultEnd().toString(),
                reservation1.getPb().getConsultNotice(),
                false
        );

        // stub
        Mockito.when(reservationService.getReservationDetail(reservation1.getId(), pbId)).thenReturn(detailByPBDTO);

        // when
        ResultActions resultActions = mvc.perform(get("/pb/reservation/{id}", reservation1.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.userId").value(1L));
        resultActions.andExpect(jsonPath("$.data.profileImage").value("profile.png"));
        resultActions.andExpect(jsonPath("$.data.name").value("lee"));
        resultActions.andExpect(jsonPath("$.data.phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.reservationId").value(1L));
    }

    @WithMockUser
    @Test
    public void get_reservation_detail_by_user_test() throws Exception {
        // given
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");
        Reservation reservation1 = newMockVisitReservation(1L, user, pb, ReservationProcess.COMPLETE);
        ReservationResponse.DetailByUserDTO detailByUserDTO = new ReservationResponse.DetailByUserDTO(
                reservation1.getPb().getId(),
                reservation1.getPb().getProfile(),
                reservation1.getPb().getName(),
                reservation1.getPb().getPhoneNumber(),
                reservation1.getPb().getEmail(),
                reservation1.getId(),
                localDateTimeToStringV2(reservation1.getCandidateTime1()),
                localDateTimeToStringV2(reservation1.getCandidateTime2()),
                localDateTimeToStringV2(reservation1.getTime()),
                reservation1.getType(),
                reservation1.getLocationName(),
                reservation1.getLocationAddress(),
                reservation1.getGoal(),
                reservation1.getQuestion(),
                reservation1.getPb().getConsultStart().toString(),
                reservation1.getPb().getConsultEnd().toString(),
                reservation1.getPb().getConsultNotice(),
                false
        );

        // stub
        Mockito.when(reservationService.getReservationDetailByUser(reservation1.getId(), pbId)).thenReturn(detailByUserDTO);

        // when
        ResultActions resultActions = mvc.perform(get("/user/reservation/{id}", reservation1.getId()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.pbId").value(1L));
        resultActions.andExpect(jsonPath("$.data.profileImage").value("profile.png"));
        resultActions.andExpect(jsonPath("$.data.name").value("이피비"));
        resultActions.andExpect(jsonPath("$.data.phoneNumber").value("01012345678"));
        resultActions.andExpect(jsonPath("$.data.reservationId").value(1L));
    }

    @WithMockPB
    @Test
    public void update_reservation_test() throws Exception {
        // given
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");
        Reservation reservation1 = newMockVisitReservation(1L, user, pb, ReservationProcess.COMPLETE);
        ReservationRequest.UpdateDTO updateDTO = new ReservationRequest.UpdateDTO();
        updateDTO.setTime("2024년 7월 4일 오전 9시 20분");
        updateDTO.setType(ReservationType.VISIT);
        updateDTO.setCategory(LocationType.BRANCH);
        String requestBody = om.writeValueAsString(updateDTO);

        // stub
        Mockito.doNothing().when(reservationService).updateReservation(anyLong(), any(), anyLong());

        // when
        ResultActions resultActions = mvc.perform(patch("/pb/reservation/{id}", reservation1.getId())
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockPB
    @Test
    public void cancel_reservation_test() throws Exception {
        // given
        Long reservationId = 1L;

        // stub
        Mockito.doNothing().when(reservationService).cancelReservation(anyLong(), any());

        // when
        ResultActions resultActions = mvc.perform(delete("/auth/reservation/{id}", reservationId));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockPB
    @Test
    public void confirm_reservation_test() throws Exception {
        // given
        Long reservationId = 1L;
        ReservationRequest.ConfirmDTO confirmDTO = new ReservationRequest.ConfirmDTO();
        confirmDTO.setTime("2024년 7월 4일 오전 9시 20분");
        String requestBody = om.writeValueAsString(confirmDTO);

        // stub
        Mockito.doNothing().when(reservationService).confirmReservation(anyLong(), anyLong(), any());

        // when
        ResultActions resultActions = mvc.perform(
                patch("/pb/reservation/{id}/confirmed", reservationId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockPB
    @Test
    public void complete_reservation_test() throws Exception {
        // given
        Long reservationId = 1L;

        // stub
        Mockito.doNothing().when(reservationService).completeReservation(anyLong(), any());

        // when
        ResultActions resultActions = mvc.perform(patch("/auth/reservation/{id}/completed", reservationId));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockPB
    @Test
    public void get_reservation_by_date_test() throws Exception {
        // given
        Long pbId = 1L;
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(pbId, "이피비", branch);
        User user = newMockUser(1L, "lee");
        Reservation reservation1 = newMockVisitReservation(1L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation2 = newMockVisitReservation(2L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation3 = newMockVisitReservation(3L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation4 = newMockVisitReservation(4L, user, pb, ReservationProcess.COMPLETE);
        Reservation reservation5 = newMockVisitReservation(5L, user, pb, ReservationProcess.COMPLETE);

        // stub
        Mockito.when(reservationService.getReservationsByDate(anyInt(), anyInt(), any()))
                .thenReturn(
                        new ArrayList<>(Arrays.asList(
                                new ReservationResponse.ReservationInfoDTO(reservation1, user),
                                new ReservationResponse.ReservationInfoDTO(reservation2, user),
                                new ReservationResponse.ReservationInfoDTO(reservation3, user),
                                new ReservationResponse.ReservationInfoDTO(reservation4, user),
                                new ReservationResponse.ReservationInfoDTO(reservation5, user)
                        ))
                );

        // when
        ResultActions resultActions = mvc.perform(get("/pb/reservation"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data[0].id").value(1L));
        resultActions.andExpect(jsonPath("$.data[0].userName").value("lee"));
        resultActions.andExpect(jsonPath("$.data[0].day").value(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        resultActions.andExpect(jsonPath("$.data[0].time").value(LocalTime.now().truncatedTo(ChronoUnit.MINUTES).toString()));
        resultActions.andExpect(jsonPath("$.data[0].type").value(ReservationType.VISIT.toString()));
        resultActions.andExpect(jsonPath("$.data[0].process").value(ReservationProcess.COMPLETE.toString()));
    }

    @WithMockPB
    @Test
    public void update_consult_time_test() throws Exception {
        // given
        ReservationRequest.UpdateTimeDTO updateTimeDTO = new ReservationRequest.UpdateTimeDTO();
        updateTimeDTO.setConsultStart("09:00");
        updateTimeDTO.setConsultEnd("10:00");
        updateTimeDTO.setConsultNotice("안녕하세요.");
        String requestBody = om.writeValueAsString(updateTimeDTO);

        // stub
        Mockito.doNothing().when(reservationService).updateConsultTime(any(), anyLong());

        // when
        ResultActions resultActions = mvc.perform(
                post("/pb/consultTime")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockPB
    @Test
    public void get_reviews_test() throws Exception {
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
        Review review1 = newMockReview(1L, reservation);
        Review review2 = newMockReview(2L, reservation2);
        Review review3 = newMockReview(3L, reservation3);
        Review review4 = newMockReview(4L, reservation4);
        Review review5 = newMockReview(5L, reservation5);
        Style style = newMockStyle(1L, review1, StyleStyle.FAST);
        Style style2 = newMockStyle(2L, review1, StyleStyle.KIND);
        Style style3 = newMockStyle(3L, review1, StyleStyle.DIRECTIONAL);

        List<Style> styles = new ArrayList<>(Arrays.asList(
                style,
                style2,
                style3
        ));
        Page<Review> reviews = new PageImpl<>(
                new ArrayList<>(Arrays.asList(
                        review5,
                        review4,
                        review3,
                        review2,
                        review1
                )));
        List<ReservationResponse.ReviewDTO> reviewDTOs = new ArrayList<>();
        for (Review review : reviews) {
            reviewDTOs.add(new ReservationResponse.ReviewDTO(review, user, styles));
        }
        PageDTO<ReservationResponse.ReviewDTO> reviewsDTO = new PageDTO<>(
                reviewDTOs,
                reviews,
                Review.class
        );

        // stub
        Mockito.when(reservationService.getReviews(anyLong(), anyInt())).thenReturn(reviewsDTO);

        // when
        ResultActions resultActions = mvc.perform(
                get("/pb/reviews")
                        .param("page", "0")
        );
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.list[0].reviewId").value(5L));
        resultActions.andExpect(jsonPath("$.data.list[0].userName").value("lee"));
        resultActions.andExpect(jsonPath("$.data.list[0].content").value("content 입니다"));
        resultActions.andExpect(jsonPath("$.data.list[0].createdAt").value(LocalDate.now().toString()));
        resultActions.andExpect(jsonPath("$.data.list[0].list[0].style").value(StyleStyle.FAST.toString()));
        resultActions.andExpect(jsonPath("$.data.list[0].list[1].style").value(StyleStyle.KIND.toString()));
        resultActions.andExpect(jsonPath("$.data.list[0].list[2].style").value(StyleStyle.DIRECTIONAL.toString()));
    }
}
