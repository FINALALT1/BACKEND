package kr.co.moneybridge.model.user;

import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.model.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
public class UserInvestInfoTest extends DummyEntity {

    @Autowired
    private UserInvestInfoRepository userInvestInfoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE user_invest_info_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        User user = userRepository.save(newUser("김투자"));
        UserInvestInfo userInvestInfo = userInvestInfoRepository.save(newUserInvestInfo(user));
        em.clear();
    }

    @Test
    public void findByUserId() {
        // given
        Long id = 1L;

        // when
        Optional<UserInvestInfo> userInvestInfo = userInvestInfoRepository.findByUserId(id);

        // then
        Assertions.assertThat(userInvestInfo).isNotNull();
        Assertions.assertThat(userInvestInfo.get().getUser().getId()).isEqualTo(1L);
    }

    @Test
    public void save() {
        // given
        String name = "이투자";
        User user = userRepository.save(newUser(name));
        UserInvestInfo userInvest = newUserInvestInfo(user);

        // when
        UserInvestInfo userInvestInfo = userInvestInfoRepository.save(userInvest);

        // then
        Assertions.assertThat(userInvestInfo).isNotNull();
        Assertions.assertThat(userInvestInfo.getUser().getName()).isEqualTo(name);
    }
}
