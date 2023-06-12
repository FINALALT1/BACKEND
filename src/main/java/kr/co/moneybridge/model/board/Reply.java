package kr.co.moneybridge.model.board;

import kr.co.moneybridge.model.backoffice.QuestionAuthorRole;
import kr.co.moneybridge.model.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "reply_tb")
@Entity
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

//    @ManyToOne(fetch = FetchType.LAZY)
//    private User user;
    @Column(nullable = false)
    private Long authorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReplyAuthorRole authorRole;

    @Column(nullable = false)
    private String content; // 댓글, varchar(255)

//    private Long parentId;

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
}
