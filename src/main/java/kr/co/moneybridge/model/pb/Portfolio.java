package kr.co.moneybridge.model.pb;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "portfolio_tb")
@Entity
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private PB pb;

    private Double cumulativeReturn; // 누적수익률

    private Double maxDrawdown; // 최대자본인하율

    private Double profitFactor;
    private Double averageProfit; // 평균손익률

    private String file; // 첨부 파일

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

    public void updateFile(String file) { this.file = file; }
    public void deleteFile() { this.file = null; }
    public void updateCumulativeReturn(Double cumulativeReturn) { this.cumulativeReturn = cumulativeReturn; }
    public void updateMaxDrawdown(Double maxDrawdown) { this.maxDrawdown = maxDrawdown; }
    public void updateProfitFactor(Double profitFactor) { this.profitFactor = profitFactor; }
    public void updateAverageProfit(Double averageProfit) { this.averageProfit = averageProfit; }

}
