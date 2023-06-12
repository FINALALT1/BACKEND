package kr.co.moneybridge.model;

import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
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
import java.util.Optional;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest extends DummyEntity {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        userRepository.save(newUser("김투자"));
        em.clear();
    }

    @Test
    public void findByEmail() {
        // given
        String email = "김투자@nate.com";

        // when
        Optional<User> userOP = userRepository.findByEmail(email);
        if (userOP.isEmpty()) {
            throw new Exception400("email", "이메일을 찾을 수 없습니다");
        }
        User userPS = userOP.get();

        // then
        Assertions.assertThat(userPS.getId()).isEqualTo(1L);
        Assertions.assertThat(userPS.getName()).isEqualTo("김투자");
        Assertions.assertThat(
                passwordEncoder.matches("password1234", userPS.getPassword())
        ).isEqualTo(true);
        Assertions.assertThat(userPS.getEmail()).isEqualTo("김투자@nate.com");
        Assertions.assertThat(userPS.getPhoneNumber()).isEqualTo("01012345678");
        Assertions.assertThat(userPS.getRole()).isEqualTo(Role.USER);
        Assertions.assertThat(userPS.getProfile()).isEqualTo("프로필.png");
        Assertions.assertThat(userPS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(userPS.getUpdatedAt()).isNull();
    }

    @Test
    public void save() {
        // given
        User user = newUser("이투자");

        // when
        User userPS = userRepository.save(user);

        // then (beforeEach에서 2건이 insert 되어 있음)
        Assertions.assertThat(userPS.getId()).isEqualTo(2L);
        Assertions.assertThat(userPS.getName()).isEqualTo("이투자");
        Assertions.assertThat(
                passwordEncoder.matches("password1234", userPS.getPassword())
        ).isEqualTo(true);
        Assertions.assertThat(userPS.getEmail()).isEqualTo("이투자@nate.com");
        Assertions.assertThat(userPS.getPhoneNumber()).isEqualTo("01012345678");
        Assertions.assertThat(userPS.getRole()).isEqualTo(Role.USER);
        Assertions.assertThat(userPS.getProfile()).isEqualTo("프로필.png");
        Assertions.assertThat(userPS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(userPS.getUpdatedAt()).isNull();
    }
}