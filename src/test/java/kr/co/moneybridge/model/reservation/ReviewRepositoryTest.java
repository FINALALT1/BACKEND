package kr.co.moneybridge.model.reservation;

import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class ReviewRepositoryTest extends DummyEntity {
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
        reviewRepository.save(newReview(reservation));
        reviewRepository.save(newReview(reservation2));
        reviewRepository.save(newReview(reservation3));
        reviewRepository.save(newReview(reservation4));
        reviewRepository.save(newReview(reservation5));

        em.clear();
    }

    @Test
    public void find_by_reservation_id_test() throws Exception {
        // given
        Long id = 1L;

        // when
        Optional<Review> review = reviewRepository.findByReservationId(id);

        // then
        assertThat(review.get().getId()).isEqualTo(id);
        assertThat(review.get().getAdherence()).isEqualTo(ReviewAdherence.EXCELLENT);
        assertThat(review.get().getContent()).isEqualTo("content 입니다");
        assertThat(review.get().getReservation().getId()).isInstanceOf(Long.class);
    }

    @Test
    public void count_test() {

        // when
        Long count = reviewRepository.count();

        // then
        assertThat(count).isEqualTo(5);
    }

    @Test
    public void find_by_id_test() throws Exception {
        // given
        Long id = 1L;

        // when
        Optional<Review> review = reviewRepository.findById(id);

        // then
        assertThat(review.get().getId()).isEqualTo(id);
        assertThat(review.get().getAdherence()).isEqualTo(ReviewAdherence.EXCELLENT);
        assertThat(review.get().getContent()).isEqualTo("content 입니다");
        assertThat(review.get().getReservation().getId()).isInstanceOf(Long.class);
    }

    @Test
    public void count_by_pbId_test() {
        // given
        Long pbId = 1L;

        // when
        Integer count = reviewRepository.countByPBId(pbId);
        System.out.println(count);
        // then
        assertThat(count).isEqualTo(5);
    }

    @Test
    public void find_all_by_pbId_and_process_test() throws Exception {
        // given
        Long pbId = 1L;
        ReservationProcess process = ReservationProcess.COMPLETE;
        int page = 0;
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<Review> reviewPG = reviewRepository.findAllByPbIdAndProcess(pbId, process, pageable);

        // then
        assertThat(reviewPG.getContent().get(0).getId()).isInstanceOf(Long.class);
        assertThat(reviewPG.getContent().get(0).getReservation().getId()).isInstanceOf(Long.class);
        assertThat(reviewPG.getContent().get(0).getContent()).isEqualTo("content 입니다");
        assertThat(reviewPG.getContent().get(0).getAdherence()).isEqualTo(ReviewAdherence.EXCELLENT);
        assertThat(reviewPG.getContent().get(0).getCreatedAt().toLocalDate().toString()).matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    @Test
    public void count_by_reservationId_test() {
        // given
        Long reservationId = 1L;

        // when
        Integer count = reviewRepository.countByReservationId(reservationId);

        // then
        assertThat(count).isEqualTo(1);
    }
}
