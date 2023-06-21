package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.dto.user.UserResponse;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserBookmarkRepository;
import kr.co.moneybridge.model.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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
    private UserRepository userRepository;
    @Autowired
    private UserBookmarkRepository userBookmarkRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE pb_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE company_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE branch_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE user_bookmark_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        Company c = companyRepository.save(newCompany("미래에셋증권"));
        Branch b = branchRepository.save(newBranch(c, 0));
        PB pb = pbRepository.save(newPB("김피비", b));
        PB pb2 = pbRepository.save(newPBwithStatus("김대기", b, PBStatus.PENDING));
        User user = userRepository.save(newUser("lee"));
        userBookmarkRepository.save(newUserBookmark(user, pb));

        em.clear();
    }

//    @Test
//    public void findBusinessCardById() {
//        // given
//        Long id = 1L;
//
//        // when
//        String img = pbRepository.findBusinessCardById(id);
//
//        // then
//        Assertions.assertThat(img).isEqualTo("card.png");
//    }

    @Test
    public void findAllByStatus() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        // when
        Page<PB> pbPG = pbRepository.findAllByStatus(PBStatus.PENDING, pageable);
        PB pbPS = pbPG.getContent().get(0);

        // then
        Assertions.assertThat(pbPS.getId()).isInstanceOf(Long.class);
        Assertions.assertThat(pbPS.getName()).isEqualTo("김대기");
        Assertions.assertThat(
                passwordEncoder.matches("password1234", pbPS.getPassword())
        ).isEqualTo(true);
        Assertions.assertThat(pbPS.getEmail()).isEqualTo("김대기@nate.com");
        Assertions.assertThat(pbPS.getPhoneNumber()).isEqualTo("01012345678");
        Assertions.assertThat(pbPS.getBranch().getCompany().getName()).isEqualTo("미래에셋증권");
        Assertions.assertThat(pbPS.getRole()).isEqualTo(Role.PB);
        Assertions.assertThat(pbPS.getProfile()).isEqualTo("profile.png");
        Assertions.assertThat(pbPS.getBusinessCard()).isEqualTo("card.png");
        Assertions.assertThat(pbPS.getCareer()).isEqualTo(10);
        Assertions.assertThat(pbPS.getSpeciality1()).isEqualTo(PBSpeciality.BOND);
        Assertions.assertThat(pbPS.getSpeciality2()).isNull();
        Assertions.assertThat(pbPS.getIntro()).isEqualTo("김대기 입니다");
        Assertions.assertThat(pbPS.getMsg()).isEqualTo("한줄메시지..");
        Assertions.assertThat(pbPS.getReservationInfo()).isEqualTo("10분 미리 도착해주세요");
        Assertions.assertThat(pbPS.getConsultStart()).isEqualTo(MyDateUtil.StringToLocalTime("09:00"));
        Assertions.assertThat(pbPS.getConsultEnd()).isEqualTo(MyDateUtil.StringToLocalTime("18:00"));
        Assertions.assertThat(pbPS.getConsultNotice()).isEqualTo("월요일 불가능합니다");
        Assertions.assertThat(pbPS.getStatus()).isEqualTo(PBStatus.PENDING);
        Assertions.assertThat(pbPS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(pbPS.getUpdatedAt()).isNull();
    }

    @Test
    public void findByIdIn() {
        // given
        List<Long> pbIds = Arrays.asList(1L);

        // when
        List<PB> pbs = pbRepository.findByIdIn(pbIds);

        // then
        Assertions.assertThat(pbs.get(0).getId()).isEqualTo(1L);
        Assertions.assertThat(pbs.get(0).getName()).isEqualTo("김피비");
        Assertions.assertThat(
                passwordEncoder.matches("password1234", pbs.get(0).getPassword())
        ).isEqualTo(true);
        Assertions.assertThat(pbs.get(0).getEmail()).isEqualTo("김피비@nate.com");
        Assertions.assertThat(pbs.get(0).getPhoneNumber()).isEqualTo("01012345678");
        Assertions.assertThat(pbs.get(0).getBranch().getCompany().getName()).isEqualTo("미래에셋증권");
        Assertions.assertThat(pbs.get(0).getRole()).isEqualTo(Role.PB);
        Assertions.assertThat(pbs.get(0).getProfile()).isEqualTo("profile.png");
        Assertions.assertThat(pbs.get(0).getBusinessCard()).isEqualTo("card.png");
        Assertions.assertThat(pbs.get(0).getCareer()).isEqualTo(10);
        Assertions.assertThat(pbs.get(0).getSpeciality1()).isEqualTo(PBSpeciality.BOND);
        Assertions.assertThat(pbs.get(0).getSpeciality2()).isNull();
        Assertions.assertThat(pbs.get(0).getIntro()).isEqualTo("김피비 입니다");
        Assertions.assertThat(pbs.get(0).getMsg()).isEqualTo("한줄메시지..");
        Assertions.assertThat(pbs.get(0).getReservationInfo()).isEqualTo("10분 미리 도착해주세요");
        Assertions.assertThat(pbs.get(0).getConsultStart()).isEqualTo(MyDateUtil.StringToLocalTime("09:00"));
        Assertions.assertThat(pbs.get(0).getConsultEnd()).isEqualTo(MyDateUtil.StringToLocalTime("18:00"));
        Assertions.assertThat(pbs.get(0).getConsultNotice()).isEqualTo("월요일 불가능합니다");
        Assertions.assertThat(pbs.get(0).getStatus()).isEqualTo(PBStatus.ACTIVE);
        Assertions.assertThat(pbs.get(0).getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(pbs.get(0).getUpdatedAt()).isNull();
    }

    @Test
    public void findIdsBySpecialityIn() {
        // given
        List<PBSpeciality> list = Arrays.asList(PBSpeciality.BOND);

        // when
        List<Long> pbIds = pbRepository.findIdsBySpecialityIn(list);

        // then
        Assertions.assertThat(pbIds.get(0)).isInstanceOf(Long.class);
    }

    @Test
    public void findIdsBySpecialityNotIn() {
        // given
        List<PBSpeciality> list = Arrays.asList(PBSpeciality.REAL_ESTATE);

        // when
        List<Long> pbIds = pbRepository.findIdsBySpecialityNotIn(list);

        // then
        Assertions.assertThat(pbIds.get(0)).isInstanceOf(Long.class);
    }

    @Test
    public void findTwoByBookMarker() {
        // when
        Pageable topTwo = PageRequest.of(0, 2);
        Page<UserResponse.BookmarkDTO> pbBookmarkTwo = pbRepository.findTwoByBookmarker(1L, topTwo);

        // then
        Assertions.assertThat(pbBookmarkTwo.getContent().get(0).getId()).isEqualTo(1L);
        Assertions.assertThat(pbBookmarkTwo.getContent().get(0).getThumbnail()).isEqualTo("profile.png");
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
