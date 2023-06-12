package kr.co.moneybridge.model.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
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
    @Autowired
    private ObjectMapper om;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE review_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE reservation_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE pb_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();

        User userPS = userRepository.save(newUser("lee"));
        Company companyPS = companyRepository.save(newCompany("미래에셋증권"));
        Branch branchPS = branchRepository.save(newBranch(companyPS, 1));
        PB pbPS = pbRepository.save(newPB("이피비", branchPS));
        Reservation reservation = reservationRepository.save(newVisitReservation(userPS, pbPS, ReservationProcess.COMPLETE));
        reviewRepository.save(newReview(reservation));

        em.clear();
    }

    @Test
    public void findAll_test() throws Exception {
        // given
        Long pbId = 1L;
        ReservationProcess process = ReservationProcess.COMPLETE;
        int page = 0;
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<ReservationResponse.ReviewDTO> reviewPG = reviewRepository.findAll(pbId, process, pageable);
        String response = om.writeValueAsString(reviewPG);

        // then
        assertThat(reviewPG.getContent().get(0).getReviewId()).isEqualTo(1L);
        assertThat(reviewPG.getContent().get(0).getUsername()).isEqualTo("lee");
        assertThat(reviewPG.getContent().get(0).getContent()).isEqualTo("content 입니다");
        assertThat(reviewPG.getContent().get(0).getCreatedAt()).matches("content 입니다");
    }
}
