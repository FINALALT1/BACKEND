package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.model.Role;
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
import java.util.List;
import java.util.Optional;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
public class PBRepositoryTest extends DummyEntity {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private PBRepository pbRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
//    private Branch b = newBranch(newCompany("미래에셋증권"), 0);

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE pb_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        Company c = companyRepository.save(newCompany("미래에셋증권"));
        Branch b = branchRepository.save(newBranch(c, 0));
        pbRepository.save(newPB("김피비", b));
        em.clear();
    }

    @Test
    public void findByNameAndPhoneNumber() {
        // given
        String name = "김피비";
        String phoneNumber = "01012345678";

        // when
        List<PB> pbPSs = pbRepository.findByNameAndPhoneNumber(name, phoneNumber);
        PB pbPS = pbPSs.get(0);

        // then
        Assertions.assertThat(pbPS.getId()).isEqualTo(1L);
        Assertions.assertThat(pbPS.getName()).isEqualTo("김피비");
        Assertions.assertThat(
                passwordEncoder.matches("password1234", pbPS.getPassword())
        ).isEqualTo(true);
        Assertions.assertThat(pbPS.getEmail()).isEqualTo("김피비@nate.com");
        Assertions.assertThat(pbPS.getPhoneNumber()).isEqualTo("01012345678");
        Assertions.assertThat(pbPS.getBranch().getCompany().getName()).isEqualTo("미래에셋증권");
        Assertions.assertThat(pbPS.getRole()).isEqualTo(Role.PB);
        Assertions.assertThat(pbPS.getProfile()).isEqualTo("profile.png");
        Assertions.assertThat(pbPS.getBusinessCard()).isEqualTo("card.png");
        Assertions.assertThat(pbPS.getCareer()).isEqualTo(10);
        Assertions.assertThat(pbPS.getSpeciality1()).isEqualTo(PBSpeciality.BOND);
        Assertions.assertThat(pbPS.getSpeciality2()).isNull();
        Assertions.assertThat(pbPS.getIntro()).isEqualTo("김피비 입니다");
        Assertions.assertThat(pbPS.getMsg()).isEqualTo("한줄메시지..");
        Assertions.assertThat(pbPS.getReservationInfo()).isEqualTo("10분 미리 도착해주세요");
        Assertions.assertThat(pbPS.getConsultStart()).isEqualTo(MyDateUtil.StringToLocalTime("09:00"));
        Assertions.assertThat(pbPS.getConsultEnd()).isEqualTo(MyDateUtil.StringToLocalTime("18:00"));
        Assertions.assertThat(pbPS.getConsultNotice()).isEqualTo("월요일 불가능합니다");
        Assertions.assertThat(pbPS.getStatus()).isEqualTo(PBStatus.ACTIVE);
        Assertions.assertThat(pbPS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(pbPS.getUpdatedAt()).isNull();
    }

    @Test
    public void findByEmail() {
        // given
        String email = "김피비@nate.com";

        // when
        PB pbPS = pbRepository.findByEmail(email)
                .orElseThrow(() -> new Exception404("해당하는 PB가 없습니다"));

        // then
        Assertions.assertThat(pbPS.getId()).isEqualTo(1L);
        Assertions.assertThat(pbPS.getName()).isEqualTo("김피비");
        Assertions.assertThat(
                passwordEncoder.matches("password1234", pbPS.getPassword())
        ).isEqualTo(true);
        Assertions.assertThat(pbPS.getEmail()).isEqualTo("김피비@nate.com");
        Assertions.assertThat(pbPS.getPhoneNumber()).isEqualTo("01012345678");
        Assertions.assertThat(pbPS.getBranch().getCompany().getName()).isEqualTo("미래에셋증권");
        Assertions.assertThat(pbPS.getRole()).isEqualTo(Role.PB);
        Assertions.assertThat(pbPS.getProfile()).isEqualTo("profile.png");
        Assertions.assertThat(pbPS.getBusinessCard()).isEqualTo("card.png");
        Assertions.assertThat(pbPS.getCareer()).isEqualTo(10);
        Assertions.assertThat(pbPS.getSpeciality1()).isEqualTo(PBSpeciality.BOND);
        Assertions.assertThat(pbPS.getSpeciality2()).isNull();
        Assertions.assertThat(pbPS.getIntro()).isEqualTo("김피비 입니다");
        Assertions.assertThat(pbPS.getMsg()).isEqualTo("한줄메시지..");
        Assertions.assertThat(pbPS.getReservationInfo()).isEqualTo("10분 미리 도착해주세요");
        Assertions.assertThat(pbPS.getConsultStart()).isEqualTo(MyDateUtil.StringToLocalTime("09:00"));
        Assertions.assertThat(pbPS.getConsultEnd()).isEqualTo(MyDateUtil.StringToLocalTime("18:00"));
        Assertions.assertThat(pbPS.getConsultNotice()).isEqualTo("월요일 불가능합니다");
        Assertions.assertThat(pbPS.getStatus()).isEqualTo(PBStatus.ACTIVE);
        Assertions.assertThat(pbPS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(pbPS.getUpdatedAt()).isNull();
    }

    @Test
    public void findById() {
        // given
        Long id = 1L;

        // when
        PB pbPS = pbRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당하는 PB가 없습니다"));

        // then
        Assertions.assertThat(pbPS.getId()).isEqualTo(1L);
        Assertions.assertThat(pbPS.getName()).isEqualTo("김피비");
        Assertions.assertThat(
                passwordEncoder.matches("password1234", pbPS.getPassword())
        ).isEqualTo(true);
        Assertions.assertThat(pbPS.getEmail()).isEqualTo("김피비@nate.com");
        Assertions.assertThat(pbPS.getPhoneNumber()).isEqualTo("01012345678");
        Assertions.assertThat(pbPS.getBranch().getCompany().getName()).isEqualTo("미래에셋증권");
        Assertions.assertThat(pbPS.getRole()).isEqualTo(Role.PB);
        Assertions.assertThat(pbPS.getProfile()).isEqualTo("profile.png");
        Assertions.assertThat(pbPS.getBusinessCard()).isEqualTo("card.png");
        Assertions.assertThat(pbPS.getCareer()).isEqualTo(10);
        Assertions.assertThat(pbPS.getSpeciality1()).isEqualTo(PBSpeciality.BOND);
        Assertions.assertThat(pbPS.getSpeciality2()).isNull();
        Assertions.assertThat(pbPS.getIntro()).isEqualTo("김피비 입니다");
        Assertions.assertThat(pbPS.getMsg()).isEqualTo("한줄메시지..");
        Assertions.assertThat(pbPS.getReservationInfo()).isEqualTo("10분 미리 도착해주세요");
        Assertions.assertThat(pbPS.getConsultStart()).isEqualTo(MyDateUtil.StringToLocalTime("09:00"));
        Assertions.assertThat(pbPS.getConsultEnd()).isEqualTo(MyDateUtil.StringToLocalTime("18:00"));
        Assertions.assertThat(pbPS.getConsultNotice()).isEqualTo("월요일 불가능합니다");
        Assertions.assertThat(pbPS.getStatus()).isEqualTo(PBStatus.ACTIVE);
        Assertions.assertThat(pbPS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(pbPS.getUpdatedAt()).isNull();
    }
}
