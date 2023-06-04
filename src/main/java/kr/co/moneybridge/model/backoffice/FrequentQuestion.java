package kr.co.moneybridge.model.backoffice;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "frequent_question_tb")
@Entity
public class FrequentQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 글자수 제한 몇자? 최대 60자

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 글자수 제한 몇자? 과연 TEXT가 맞을까. 난 괜찮을거같음. 근데 텍스트 에디터는 쓰게 할건지, 데이터 크기 초과전에 예외처리 + 초과 오류시 예외처리 해야함

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
