package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.core.dummy.DummyEntity;
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
import java.util.List;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
public class BranchRepositoryTest extends DummyEntity {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE company_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        Company c1 = companyRepository.save(newCompany("미래에셋증권"));
        Company c2 = companyRepository.save(newCompany("키움증권"));
        Branch b1 = branchRepository.save(newBranch(c1, 0));
        em.clear();
    }

    @Test
    public void findByCompanyIdAndKeyword() {
        // when
        Long companyId = 1L;
        String keyword = "지번주소";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<Branch> branchPG = branchRepository.findByCompanyIdAndKeyword(companyId, keyword, pageable);

        // then
        Assertions.assertThat(branchPG.getContent().get(0).getId()).isEqualTo(1L);
        Assertions.assertThat(branchPG.getContent().get(0).getName()).isEqualTo("미래에셋증권 여의도점");
        Assertions.assertThat(branchPG.getContent().get(0).getCompany().getName()).isEqualTo("미래에셋증권");
        Assertions.assertThat(branchPG.getContent().get(0).getRoadAddress()).isEqualTo("미래에셋증권 도로명주소");
        Assertions.assertThat(branchPG.getContent().get(0).getStreetAddress()).isEqualTo("미래에셋증권 지번주소");
    }
}
