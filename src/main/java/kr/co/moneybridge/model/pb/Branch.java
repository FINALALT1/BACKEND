package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.model.board.BoardStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "branch_tb")
@Entity
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private Company company;

    @Column(nullable = false, length = 20)
    private String name; // 증권사명(company의 name) + ' ' + 지점명

    @Column(nullable = false)
    private String roadAddress; // 도로명 주소

    @Column(nullable = false)
    private String streetAddress; // 지번 주소

    @Column(nullable = false)
    private String latitude; // 위도

    @Column(nullable = false)
    private String longitude; // 경도

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardStatus status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
