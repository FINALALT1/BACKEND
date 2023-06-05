package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.model.reservation.Reservation;
import kr.co.moneybridge.model.reservation.ReviewAdherence;
import kr.co.moneybridge.model.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "career_tb")
@Entity
public class Career {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private PB pb;

    @Column(nullable = false, length = 60)
    private String career; // 경력사항, varchar(90) - 최대 30자?

    @Column(nullable = false)
    private Integer start; // 시작년도

    @Column(nullable = false)
    private Integer end; // 끝년도

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
