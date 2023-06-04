package kr.co.moneybridge.model.reservation;

import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "reservation_tb")
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    private PB pb;

    @Column(nullable = false, length = 20)
    private String type;        //상담방식

    @Column(length = 100)
    private String locationName;        //상담장소명, 최대 25자? (지점명이랑 같을 수밖에 없음)

    private String locationAddress;     //상담장소주소, 최대 60자면 괜찮겠지..

    private LocalDateTime candidateTime1;       //후보시간1

    private LocalDateTime candidateTime2;       //후보시간2

    private LocalDateTime time;     //날짜 및 시간(확정)

    @Column(columnDefinition = "TEXT")
    private String question; // 추가 전달 사항(최대 100자 제한 있음)

    @Column(nullable = false, length = 30)
    private String goal1;        //상담목적1; enum 아니어도 괜찮은지

    @Column(length = 30)
    private String goal2;        //상담목적2;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationProcess process;     //예약신청/예약확정/상담완료

    @Column(nullable = false, length = 20)
    private String investor;        //예약자, 투자자 이름이랑 똑같이.. 최대 한글 6자?

    @Column(nullable = false, length = 20)
    private String phoneNumber;     //핸드폰번호, 투자자랑 똑같이?

    @Column(nullable = false, length = 30)
    private String email;       //이메일, 투자자랑 똑같이?

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean status;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}
