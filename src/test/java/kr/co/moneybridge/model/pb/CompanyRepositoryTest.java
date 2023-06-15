package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.core.util.MyDateUtil;
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

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
public class CompanyRepositoryTest extends DummyEntity {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE company_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        Company c1 = companyRepository.save(newCompany("미래에셋증권"));
        Company c2 = companyRepository.save(newCompany("키움증권"));
        em.clear();
    }

    @Test
    public void findAll() {
        // when
        List<Company> companies = companyRepository.findAll();

        // then
        Assertions.assertThat(companies.get(0).getId()).isEqualTo(1L);
        Assertions.assertThat(companies.get(0).getLogo()).isEqualTo("logo.png");
        Assertions.assertThat(companies.get(0).getName()).isEqualTo("미래에셋증권");

        Assertions.assertThat(companies.get(1).getId()).isEqualTo(2L);
        Assertions.assertThat(companies.get(1).getLogo()).isEqualTo("logo.png");
        Assertions.assertThat(companies.get(1).getName()).isEqualTo("키움증권");
    }
}
