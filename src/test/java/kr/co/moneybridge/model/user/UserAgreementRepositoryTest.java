package kr.co.moneybridge.model.user;

import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.model.pb.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
public class UserAgreementRepositoryTest extends DummyEntity {
    @Autowired
    private UserAgreementRepository userAgreementRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE user_agreement_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        User user = userRepository.save(newUser("김투자"));
        UserAgreement userAgreement = userAgreementRepository.save(newUserAgreement(user, UserAgreementType.OPTIONAL));

        em.clear();
    }

    @Test
    public void deleteByUserId() {
        // given
        Long id = 1L;

        // when
        userAgreementRepository.deleteByUserId(id);

        //
        assertThat(userAgreementRepository.findById(1L)).isEmpty();
    }

    @Test
    public void save() {
        // given
        User user2 = userRepository.save(newUser("김투자2"));
        UserAgreement userAgreement = newUserAgreement(user2, UserAgreementType.OPTIONAL);

        // when
        UserAgreement userAgreementPS = userAgreementRepository.save(userAgreement);

        //
        assertThat(userAgreementPS.getId()).isInstanceOf(Long.class);
        assertThat(userAgreementPS.getUser().getName()).isEqualTo("김투자2");
        assertThat(userAgreementPS.getTitle()).isEqualTo("약관1");
        assertThat(userAgreementPS.getType()).isEqualTo(UserAgreementType.OPTIONAL);
        assertThat(userAgreementPS.getIsAgreed()).isEqualTo(true);
        assertThat(userAgreementPS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        assertThat(userAgreementPS.getUpdatedAt()).isNull();
    }
}
