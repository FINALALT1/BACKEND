package kr.co.moneybridge.model.user;

import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_tb")
@Entity
public class User implements Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 60) // 패스워드 인코딩(BCrypt)
    private String password;

    @Column(nullable = false, length = 30, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserPropensity propensity;

    @Column(nullable = false)
    private String profile;

    @Column(nullable = false)
    private Boolean hasDoneBoardBookmark; // 콘텐츠 북마크 한 적 있는지

    @Column(nullable = false)
    private Boolean hasDoneReservation; // 상담 예약 신청한 적있는지

    @Column(nullable = false)
    private Boolean hasDoneReview; // 후기작성 완료한 적 있는지

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
    public void updatePassword(String password){
        this.password = password;
    }
    public void updateName(String name){
        this.name = name;
    }
    public void updatePhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }
    public void updatePropensity(UserPropensity propensity){
        this.propensity = propensity;
    }
}