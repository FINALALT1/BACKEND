package kr.co.moneybridge.model.board;

import kr.co.moneybridge.model.pb.PB;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "board_tb")
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PB pb;

    @Column(nullable = false)
    private String title; // 제목, varchar(255)

    private String thumbnail;

    @Lob
    @Column(nullable = false)
    private String content; // 블로그글이니 사이즈크니 longtext, HTML 코드저장.


    @Column(nullable = false, length = 30)
    private String tag1; // 7자 이내

    @Column(nullable = false, length = 30)
    private String tag2; // 7자 이내

    @Column(nullable = false)
    private Long clickCount; // 확장성 생각하면 Long

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardStatus status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
