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

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false)
    private UserAgreementType userAgreementType;

    @Column(nullable = false)
    private Boolean isAgreed;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean status;
}
