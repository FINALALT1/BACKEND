package kr.co.moneybridge.model.board;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "rereply_tb")
@Entity
public class ReReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Reply reply;

//    @ManyToOne(fetch = FetchType.LAZY)
//    private User user;
    @Column(nullable = false)
    private Long authorId;

    private String name;
    private String phoneNumber;

    private String name;
    private String phoneNumber;

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
