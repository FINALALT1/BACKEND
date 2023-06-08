package kr.co.moneybridge.model.backoffice;

import kr.co.moneybridge.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    @Query("select a from Admin a where a.email = :email")
    Optional<Admin> findByEmail(@Param("email") String email);
}
