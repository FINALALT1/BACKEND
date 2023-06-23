package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.core.dummy.DummyEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
class CareerRepositoryTest extends DummyEntity {
    @Autowired
    private CareerRepository careerRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private PBRepository pbRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        em.createNativeQuery("ALTER TABLE company_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE branch_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE pb_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE career_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        Company c = companyRepository.save(newCompany("미래에셋증권"));
        Branch b = branchRepository.save(newBranch(c, 0));
        PB pb = pbRepository.save(newPB("김피비", b));
        Career career = newCareer(pb);
    }

    @Test
    public void deleteByPBId() {
        // given
        Long id = 1L;

        // when
        careerRepository.deleteByPBId(id);

        //
        assertThat(careerRepository.findAll().size()).isEqualTo(0);
    }
}