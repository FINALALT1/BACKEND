package kr.co.moneybridge.model.pb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PBRepository extends JpaRepository<PB, Long> {
    @Query("select p from PB p where p.email = :email")
    Optional<PB> findByEmail(@Param("email") String email);
}
