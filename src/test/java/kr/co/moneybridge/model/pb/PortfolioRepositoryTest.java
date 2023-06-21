package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.core.dummy.DummyEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
class PortfolioRepositoryTest extends DummyEntity {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private PBRepository pbRepository;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        em.createNativeQuery("ALTER TABLE company_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE branch_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE pb_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE portfolio_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        Company c = companyRepository.save(newCompany("미래에셋증권"));
        Branch b = branchRepository.save(newBranch(c, 0));
        PB pb = pbRepository.save(newPB("김피비", b));
        Portfolio portfolio = portfolioRepository.save(newPortfolio(pb));
    }

    @Test
    void findByPbId() {
        //when
        Optional<Portfolio> portfolio = portfolioRepository.findByPbId(1L);

        //then
        Assertions.assertThat(portfolio).isPresent();

    }
}