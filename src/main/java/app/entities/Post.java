package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 50000)
    private String content;

    private String sourceUrl;
    private String sourceName;

    private Instant createdAt;

    @ManyToOne(optional = false)
    private User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    // Constructor for API-imported posts.
    public Post(String title, String content, String sourceUrl, String sourceName, Instant createdAt, User author) {
        this.title = title;
        this.content = content;
        this.sourceUrl = sourceUrl;
        this.sourceName = sourceName;
        this.createdAt = createdAt;
        this.author = author;
        // Consider initializing comments to an empty list directly if nullpointer occurs when adding a comment
        // this.comments = new ArrayList<>();

    }
}
