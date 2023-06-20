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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class StyleRepositoryTest extends DummyEntity {
    @Autowired
    private EntityManager em;
    @Autowired
    private StyleRepository styleRepository;
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
        em.createNativeQuery("ALTER TABLE style_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
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
        Review review = reviewRepository.save(newReview(reservation));
        styleRepository.save(newStyle(review, StyleStyle.FAST));
        styleRepository.save(newStyle(review, StyleStyle.KIND));

        em.clear();
    }

    @Test
    public void find_all_by_reviewId_test() {
        // given
        Long reviewId = 1L;

        // when
        List<Style> styles = styleRepository.findAllByReviewId(reviewId);

        // then
        assertThat(styles.get(0).getStyle()).isEqualTo(StyleStyle.FAST);
        assertThat(styles.get(1).getStyle()).isEqualTo(StyleStyle.KIND);
    }
}
