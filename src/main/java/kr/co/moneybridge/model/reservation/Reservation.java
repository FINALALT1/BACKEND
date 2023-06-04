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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pb_id")
    private PB pb;

    @Column(nullable = false, length = 20)
    private String type;        //상담방식

    private String locationName;        //상담장소명

    private String locationAddress;     //상담장소주소

    private LocalDateTime candidateTime1;       //후보시간1
    private LocalDateTime candidateTime2;       //후보시간2

    private LocalDateTime time;     //날짜 및 시간

    @Column(columnDefinition = "TEXT")
    private String question;

    private String goal1;        //상담목적1;

    private String goal2;        //상담목적2;

    @Enumerated(EnumType.STRING)
    private ReservationProcess process;     //예약신청/예약확정/상담완료

    @Column(nullable = false)
    private String investor;        //예약자

    @Column(nullable = false)
    private String phoneNumber;     //핸드폰번호

    @Column(nullable = false)
    private String email;       //이메일

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

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
