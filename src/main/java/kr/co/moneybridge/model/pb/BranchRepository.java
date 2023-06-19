package kr.co.moneybridge.model.pb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    @Query("select b from Branch b where b.company.id = :companyId and " +
            "(replace(trim(b.name), ' ', '') like concat('%', :keyword, '%') or " +
            "replace(trim(b.roadAddress), ' ', '') like concat('%', :keyword, '%') or " +
            "replace(trim(b.streetAddress), ' ', '') like concat('%', :keyword, '%'))")
    Page<Branch> findByCompanyIdAndKeyword(@Param("companyId") Long companyId,
                                           @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Branch b WHERE b.name = :branchName")
    Optional<Branch> findByName(@Param("branchName") String branchName);

}