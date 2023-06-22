package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.dto.pb.PBResponse;
import kr.co.moneybridge.dto.user.UserResponse;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserBookmarkRepository;
import kr.co.moneybridge.model.user.UserRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
public class PBAgreementRepositoryTest extends DummyEntity {
    @Autowired
    private PBAgreementRepository pbAgreementRepository;
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

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE pb_agreement_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE pb_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE company_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE branch_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        Company c = companyRepository.save(newCompany("미래에셋증권"));
        Branch b = branchRepository.save(newBranch(c, 0));
        PB pb = pbRepository.save(newPB("김피비", b));
        PBAgreement pbAgreement = newPBAgreement(pb, PBAgreementType.OPTIONAL);

        em.clear();
    }

    @Test
    public void deleteByPBId() {
        // given
        Long id = 1L;

        // when
        pbAgreementRepository.deleteByPBId(id);

        //
        assertThat(pbAgreementRepository.findById(1L)).isEmpty();
    }

    @Test
    public void save() {
        // given
        Company company = newCompany("미래에셋증권");
        Branch branch = newBranch(company, 1);
        PB pb = newPB("윤피비", branch);
        PBAgreement pbAgreement = newPBAgreement(pb, PBAgreementType.OPTIONAL);

        // when
        PBAgreement pbAgreementPS = pbAgreementRepository.save(pbAgreement);

        //
        assertThat(pbAgreementPS.getId()).isInstanceOf(Long.class);
        assertThat(pbAgreementPS.getPb().getName()).isEqualTo("윤피비");
        assertThat(pbAgreementPS.getTitle()).isEqualTo("약관1");
        assertThat(pbAgreementPS.getType()).isEqualTo(PBAgreementType.OPTIONAL);
        assertThat(pbAgreementPS.getIsAgreed()).isEqualTo(true);
        assertThat(pbAgreementPS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        assertThat(pbAgreementPS.getUpdatedAt()).isNull();
    }
}
