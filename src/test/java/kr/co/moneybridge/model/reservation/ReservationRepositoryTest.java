package kr.co.moneybridge.model.reservation;

import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class ReservationRepositoryTest extends DummyEntity {
    @Autowired
    private EntityManager em;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PBRepository pbRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchRepository branchRepository;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE review_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE reservation_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE pb_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE company_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE branch_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();

        User userPS = userRepository.save(newUser("lee"));
        Company companyPS = companyRepository.save(newCompany("미래에셋증권"));
        Branch branchPS = branchRepository.save(newBranch(companyPS, 1));
        PB pbPS = pbRepository.save(newPB("이피비", branchPS));
        Reservation reservation = reservationRepository.save(newVisitReservation(userPS, pbPS, ReservationProcess.COMPLETE));
        Reservation reservation2 = reservationRepository.save(newVisitReservation(userPS, pbPS, ReservationProcess.COMPLETE));
        Reservation reservation3 = reservationRepository.save(newVisitReservation(userPS, pbPS, ReservationProcess.COMPLETE));
        Reservation reservation4 = reservationRepository.save(newVisitReservation(userPS, pbPS, ReservationProcess.COMPLETE));
        Reservation reservation5 = reservationRepository.save(newVisitReservation(userPS, pbPS, ReservationProcess.COMPLETE));
        Reservation reservation6 = reservationRepository.save(newVisitReservationCancel(userPS, pbPS));
        reservationRepository.save(newCallReservation(userPS, pbPS, ReservationProcess.APPLY));
        reservationRepository.save(newCallReservation(userPS, pbPS, ReservationProcess.CONFIRM));
        reviewRepository.save(newReview(reservation));
        reviewRepository.save(newReview(reservation2));
        reviewRepository.save(newReview(reservation3));
        reviewRepository.save(newReview(reservation4));
        reviewRepository.save(newReview(reservation5));

        em.clear();
    }

    @Test
    public void find_all_pegeable_test() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

        // when
        Page<Reservation> page = reservationRepository.findAll(pageable);

        // then
        assertThat(page.getContent().size()).isGreaterThan(7);
        assertThat(page.getContent().get(0).getId()).isInstanceOf(Long.class);
        assertThat(page.getContent().get(0).getEmail()).isEqualTo("lee@nate.com");
        assertThat(page.getContent().get(0).getCandidateTime1()).isBefore(LocalDateTime.now());
        assertThat(page.getContent().get(0).getCandidateTime2()).isBefore(LocalDateTime.now().minusHours(1));
        assertThat(page.getContent().get(0).getTime()).isBefore(LocalDateTime.now());
        assertThat(page.getContent().get(0).getGoal()).isEqualTo(ReservationGoal.PROFIT);
        assertThat(page.getContent().get(0).getInvestor()).isEqualTo("lee");
        assertThat(page.getContent().get(0).getLocationAddress()).isEqualTo("강남구 강남중앙로 10");
        assertThat(page.getContent().get(0).getLocationName()).isEqualTo("kb증권 강남중앙점");
        assertThat(page.getContent().get(0).getPb().getId()).isEqualTo(1L);
        assertThat(page.getContent().get(0).getPhoneNumber()).isEqualTo("01012345678");
        assertThat(page.getContent().get(0).getQuestion()).isEqualTo("질문입니다...");
        assertThat(page.getContent().get(0).getUser().getId()).isEqualTo(1L);
        assertThat(page.getTotalElements()).isEqualTo(8);
        assertThat(page.getTotalPages()).isEqualTo(1);



    }

    @Test
    public void count_by_pb_id_and_process_test() {
        // given
        Long pbId = 1L;
        ReservationProcess process = ReservationProcess.COMPLETE;

        // when
        Integer count = reservationRepository.countByPBIdAndProcess(pbId, process);

        // then
        assertThat(count).isEqualTo(5);
    }

    @Test
    public void count_by_user_id_and_process_test() {
        // given
        Long userId = 1L;
        ReservationProcess process = ReservationProcess.COMPLETE;

        // when
        Integer count = reservationRepository.countByUserIdAndProcess(userId, process);

        // then
        assertThat(count).isEqualTo(5);
    }

    @Test
    public void count_recent_by_pb_id_and_process_test() {
        // given
        Long pbId = 1L;
        ReservationProcess process = ReservationProcess.COMPLETE;

        // when
        Integer count = reservationRepository.countRecentByPBIdAndProcess(pbId, process);

        // then
        assertThat(count).isEqualTo(5);
    }

    @Test
    public void count_recent_by_user_id_and_process_test() {
        // given
        Long userId = 1L;
        ReservationProcess process = ReservationProcess.COMPLETE;

        // when
        Integer count = reservationRepository.countRecentByUserIdAndProcess(userId, process);

        // then
        assertThat(count).isEqualTo(5);
    }

    @Test
    public void find_all_by_pb_id_and_process_test() {
        // given
        Long pbId = 1L;
        ReservationProcess process = ReservationProcess.COMPLETE;
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

        // when
        Page<ReservationResponse.RecentPagingDTO> page = reservationRepository.findAllByPbIdAndProcess(pbId, process, pageable);

        // then
        assertThat(page.getContent().get(0).getReservationId()).isInstanceOf(Long.class);
        assertThat(page.getContent().get(0).getUserId()).isEqualTo(1L);
        assertThat(page.getContent().get(0).getProfileImage()).isEqualTo("프로필.png");
        assertThat(page.getContent().get(0).getName()).isEqualTo("lee");
        assertThat(page.getContent().get(0).getCreatedAt().toLocalDate().toString()).matches("^\\d{4}-\\d{2}-\\d{2}$");
        assertThat(page.getContent().get(0).getType()).isEqualTo(ReservationType.VISIT);
    }

    @Test
    public void find_all_by_user_id_and_process_test() {
        // given
        Long userId = 1L;
        ReservationProcess process = ReservationProcess.COMPLETE;
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

        // when
        Page<ReservationResponse.RecentPagingByUserDTO> page = reservationRepository.findAllByUserIdAndProcess(userId, process, pageable);

        // then
        assertThat(page.getContent().get(0).getReservationId()).isInstanceOf(Long.class);
        assertThat(page.getContent().get(0).getPbId()).isEqualTo(1L);
        assertThat(page.getContent().get(0).getProfileImage()).isEqualTo("profile.png");
        assertThat(page.getContent().get(0).getName()).isEqualTo("이피비");
        assertThat(page.getContent().get(0).getCreatedAt().toLocalDate().toString()).matches("^\\d{4}-\\d{2}-\\d{2}$");
        assertThat(page.getContent().get(0).getType()).isEqualTo(ReservationType.VISIT);
    }

    @Test
    public void find_all_by_pb_id_and_status_test() {
        // given
        Long pbId = 1L;
        ReservationStatus status = ReservationStatus.CANCEL;
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

        // when
        Page<ReservationResponse.RecentPagingDTO> page = reservationRepository.findAllByPbIdAndStatus(pbId, status, pageable);

        // then
        assertThat(page.getContent().get(0).getReservationId()).isEqualTo(6L);
        assertThat(page.getContent().get(0).getUserId()).isEqualTo(1L);
        assertThat(page.getContent().get(0).getProfileImage()).isEqualTo("프로필.png");
        assertThat(page.getContent().get(0).getName()).isEqualTo("lee");
        assertThat(page.getContent().get(0).getCreatedAt().toLocalDate().toString()).matches("^\\d{4}-\\d{2}-\\d{2}$");
        assertThat(page.getContent().get(0).getType()).isEqualTo(ReservationType.VISIT);
    }

    @Test
    public void find_all_by_user_id_and_status_test() {
        // given
        Long userId = 1L;
        ReservationStatus status = ReservationStatus.CANCEL;
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

        // when
        Page<ReservationResponse.RecentPagingByUserDTO> page = reservationRepository.findAllByUserIdAndStatus(userId, status, pageable);

        // then
        assertThat(page.getContent().get(0).getReservationId()).isEqualTo(6L);
        assertThat(page.getContent().get(0).getPbId()).isEqualTo(1L);
        assertThat(page.getContent().get(0).getProfileImage()).isEqualTo("profile.png");
        assertThat(page.getContent().get(0).getName()).isEqualTo("이피비");
        assertThat(page.getContent().get(0).getCreatedAt().toLocalDate().toString()).matches("^\\d{4}-\\d{2}-\\d{2}$");
        assertThat(page.getContent().get(0).getType()).isEqualTo(ReservationType.VISIT);
    }

    @Test
    public void find_all_by_pb_id_without_cancel_test() {
        // given
        Long pbId = 1L;

        // when
        List<ReservationResponse.ReservationInfoDTO> response = reservationRepository.findAllByPbIdWithoutCancel(pbId);

        // then
        assertThat(response.get(0).getId()).isEqualTo(1L);
        assertThat(response.get(0).getUserName()).isEqualTo("lee");
        assertThat(response.get(0).getDay().toString()).matches("^\\d{4}-\\d{2}-\\d{2}$");
        assertThat(response.get(0).getType()).isEqualTo(ReservationType.VISIT);
        assertThat(response.get(0).getProcess()).isEqualTo(ReservationProcess.COMPLETE);
    }

    @Test
    public void countByProcess() {
        // when
        Long applyCount = reservationRepository.countByProcess(ReservationProcess.APPLY);
        Long confirmCount = reservationRepository.countByProcess(ReservationProcess.CONFIRM);
        Long completeCount = reservationRepository.countByProcess(ReservationProcess.COMPLETE);

        // then
        Assertions.assertThat(applyCount).isGreaterThan(0);
        Assertions.assertThat(confirmCount).isGreaterThan(0);
        Assertions.assertThat(completeCount).isGreaterThan(0);
    }
}