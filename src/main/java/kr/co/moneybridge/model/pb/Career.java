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

    @ManyToOne(fetch = FetchType.LAZY)
    private PB pb;

    @Column(nullable = false, length = 90)
    private String career; // 경력사항, varchar(90)

    @Column(nullable = false)
    private Integer startYear; // 시작년도

    @Column(nullable = false)
    private Integer endYear; // 끝년도

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
