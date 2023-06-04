package kr.co.moneybridge.model.user;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_invest_info_tb")
@Entity
public class UserInvestInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer q1; // 복잡(5), 고려(4), 시작(3), 모름(2)

    @Column(nullable = false)
    private Integer q2; // 손실(4), 여유(3), 소액(2)

    @Column(nullable = false)
    private Integer q3; // 위험(5), 과감(4), 감수(3), 안정(1)

    @Column(nullable = false)
    private Integer q4; // 둘다(5), 수익(4), 안배(3), 보호(1)

    @Column(nullable = false)
    private Integer q5; // 계속(5), 기간(4), 감소(3), 회수(2)

    @Column(nullable = false)
    private Integer q6; // 증가(5), 감소"4), 대기(2), 회수(1)

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
