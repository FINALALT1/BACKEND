package kr.co.moneybridge.model.board;

import kr.co.moneybridge.model.user.User;
import lombok.*;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "board_bookmark_tb")
@Entity
public class BoardBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    private User user;

    @Column(nullable = false)
    private Long bookmarkerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookmarkerRole bookmarkerRole;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

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
