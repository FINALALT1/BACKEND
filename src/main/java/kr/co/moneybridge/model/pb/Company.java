package kr.co.moneybridge.model.pb;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "company_tb")
@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 90)
    private String name; // 증권사명, varchar(90)

    @Column(nullable = false)
    private String logo; // 로고 이미지

    public void updateLogo(String path) {
        logo = path;
    }

    public void updateName(String companyName) {
        name = companyName;
    }

//    @Column(nullable = false)
//    private LocalDateTime createdAt;
//
//    private LocalDateTime updatedAt;

//    @PrePersist
//    protected void onCreate() {
//        this.createdAt = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }

}
