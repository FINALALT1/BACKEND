package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.model.user.UserAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
}
