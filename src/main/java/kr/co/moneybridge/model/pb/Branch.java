package kr.co.moneybridge.model.pb;

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

    @Column(nullable = false, length = 100)
    private String name; // 증권사명(company의 name) + ' ' + 지점명

    @Column(nullable = false)
    private String roadAddress; // 도로명 주소, 최대 60자?

    @Column(nullable = false)
    private String streetAddress; // 지번 주소, 최대 60자?

    @Column(nullable = false)
    private String latitude; // 위도, 최대 60자?

    @Column(nullable = false)
    private String longitude; // 경도, 최대 60자?

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
