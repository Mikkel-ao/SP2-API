package app.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String content;

    private Instant createdAt;

    // Small relation, fine to be eager
    @ManyToOne(fetch = FetchType.EAGER)
    private User author;

    // Needed for DTO mapping
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Post post;

    // Parent is recursive, keep it lazy
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    // Use Set to avoid MultipleBagFetchException
    @OneToMany(mappedBy = "parent",
            cascade = CascadeType.ALL,
            orphanRemoval = false,
            fetch = FetchType.LAZY)
    private Set<Comment> replies = new HashSet<>();

    @Column(nullable = false)
    private boolean deleted = false;

    public void markDeleted() {
        this.deleted = true;
        this.content = "[deleted]";
        this.author = null;
    }

    public boolean isActive() {
        return !deleted;
    }
}
