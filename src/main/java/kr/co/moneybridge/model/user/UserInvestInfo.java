package kr.co.moneybridge.model.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_invest_info_tb")
@Entity
public class UserInvestInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private int q1;
    @Column(nullable = false)
    private int q2;
    @Column(nullable = false)
    private int q3;
    @Column(nullable = false)
    private int q4;
    @Column(nullable = false)
    private int q5;
    @Column(nullable = false)
    private int q6;

    private Boolean status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
