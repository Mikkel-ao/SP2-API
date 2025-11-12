package app.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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

    // Small relation: fine to load eagerly
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private User author;

    // Use Set instead of List to avoid MultipleBagFetchException
    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    // Constructor for API-imported posts
    public Post(String title, String content, String sourceUrl, String sourceName,
                Instant createdAt, User author) {
        this.title = title;
        this.content = content;
        this.sourceUrl = sourceUrl;
        this.sourceName = sourceName;
        this.createdAt = createdAt;
        this.author = author;
    }
}
