package app.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "votes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_username", "post_id"}),
                @UniqueConstraint(columnNames = {"user_username", "comment_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int value;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_username")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
