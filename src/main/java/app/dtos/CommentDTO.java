package app.dtos;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Long id;
    private String content;
    private Instant createdAt;
    private String authorUsername;  // instead of full User entity
    private Long postId;
    private Long parentId;
    private boolean deleted;
    private boolean ownComment;

    // Needed?
    private List<CommentDTO> replies;
}
