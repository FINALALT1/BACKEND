package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.dto.pb.PBResponse;
import kr.co.moneybridge.model.board.BoardRepository;
import kr.co.moneybridge.model.board.ReReplyRepository;
import kr.co.moneybridge.model.board.ReplyRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
class AwardRepositoryTest extends DummyEntity {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private PBRepository pbRepository;
    @Autowired
    private AwardRepository awardRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        em.createNativeQuery("ALTER TABLE company_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE branch_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE pb_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE award_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        Company c = companyRepository.save(newCompany("미래에셋증권"));
        Branch b = branchRepository.save(newBranch(c, 0));
        PB pb = pbRepository.save(newPB("김피비", b));
        PB pb2 = pbRepository.save(newPB("김피비2", b));
        Award award = awardRepository.save(newAward(pb));
        Award award2 = awardRepository.save(newAward(pb2));
    }

    @Test
    public void deleteByPBId() {
        // given
        Long id = 2L;

        // when
        awardRepository.deleteByPBId(id);

        //
        assertThat(awardRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void getAwards() {
        //when
        List<PBResponse.AwardOutDTO> list = awardRepository.getAwards(1L);

        //then
        Assertions.assertThat(list.size()).isGreaterThanOrEqualTo(1);
    }
}