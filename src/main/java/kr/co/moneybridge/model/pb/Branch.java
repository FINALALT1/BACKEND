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

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @Column(nullable = false, length = 60)
    private String name; // 증권사명(company의 name) + ' ' + 지점명, varchar(60)

    @Column(nullable = false)
    private String roadAddress; // 도로명 주소, varchar(255)

    @Column(nullable = false)
    private String streetAddress; // 지번 주소, varchar(255)

    @Column(nullable = false)
    private Double latitude; // 위도

    @Column(nullable = false)
    private Double longitude; // 경도
}
