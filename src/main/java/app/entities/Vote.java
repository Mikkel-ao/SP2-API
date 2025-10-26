package app.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "votes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int value; // +1 or -1

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne
    private Post post; // Nullable

    @ManyToOne
    private Comment comment; // Nullable
}
