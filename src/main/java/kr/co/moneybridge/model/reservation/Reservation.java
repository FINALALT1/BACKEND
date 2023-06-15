package kr.co.moneybridge.model.reservation;

import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.user.User;
import lombok.*;

import javax.persistence.*;
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
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private PB pb;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationType type;        //상담방식

    @Column(length = 90)
    private String locationName;        //상담장소명, varchar(90) (지점명이랑 같을 수밖에 없음)

    private String locationAddress;     //상담장소주소

    private LocalDateTime candidateTime1;       //후보시간1

    private LocalDateTime candidateTime2;       //후보시간2

    private LocalDateTime time;     //날짜 및 시간(확정)

    private String question; // 추가 전달 사항 (최대 100자 제한 있음)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationGoal goal;        // 상담 목적

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationProcess process;     //예약신청/예약확정/상담완료

    @Column(nullable = false, length = 20)
    private String investor;        //예약자

    @Column(nullable = false, length = 20)
    private String phoneNumber;     //핸드폰번호

    @Column(nullable = false, length = 30)
    private String email;       //이메일

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateTime(LocalDateTime time) {
        this.time = time;
    }

    public void updateType(ReservationType type) {
        this.type = type;
    }

    public void updateLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void updateLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }
}
