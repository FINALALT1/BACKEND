package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserAgreementType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "pb_agreement_tb")
@Entity
public class PBAgreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PB pb;

    @Column(nullable = false)
    private String title; // 약관명, varchar(255)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PBAgreementType type; // 약관 종류

    @Column(nullable = false)
    private Boolean isAgreed;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
