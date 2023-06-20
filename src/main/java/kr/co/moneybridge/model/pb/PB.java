package kr.co.moneybridge.model.pb;

import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "pb_tb")
@Entity
public class PB implements Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Branch branch;      //지점

    @Column(nullable = false, length = 20)
    private String name;        //이름 - varchar(20)

    @Column(nullable = false, length = 60)
    private String password;    //비밀번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, length = 30, unique = true)
    private String email;       //이메일

    @Column(nullable = false, length = 20)
    private String phoneNumber;     //전화번호

    @Setter
    @Column(nullable = false)
    private String businessCard;        //명함

    private String profile;     //프로필사진

    @Column(nullable = false)
    private Integer career;     //경력(연차)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PBSpeciality speciality1;     //전문분야1

    @Enumerated(EnumType.STRING)
    private PBSpeciality speciality2;     //전문분야2

    @Column(columnDefinition = "TEXT")
    private String intro;   //자기소개,

    private String msg;     //한줄메세지, varchar(255)

    private String reservationInfo;     //예약 전달사항, varchar(255)

    private LocalTime consultStart;        //상담가능 시작시간

    private LocalTime consultEnd;      //상담가능 종료시간

    private String consultNotice;       //상담불가시간 메세지, varchar(255)

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PBStatus status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateProfile(String profile) {
        this.profile = profile;
    }

    public void updateBranch(Branch branch) {
        this.branch = branch;
    }

    public void updateCareer(Integer career) {
        this.career = career;
    }

    public void updateSpeciality1(PBSpeciality speciality1) {
        this.speciality1 = speciality1;
    }

    public void updateSpeciality2(PBSpeciality speciality2) {
        this.speciality1 = speciality2;
    }

    public void updateIntro(String intro) {
        this.intro = intro;
    }

    public void updateMsg(String msg) {
        this.msg = msg;
    }

    public void approved() {
        this.status = PBStatus.ACTIVE;
    }

    public void updateConsultStart(LocalTime start) {
        this.consultStart = start;
    }

    public void updateConsultEnd(LocalTime end) {
        this.consultEnd = end;
    }

    public void updateConsultNotice(String notice) {
        this.consultNotice = notice;
    }
}
