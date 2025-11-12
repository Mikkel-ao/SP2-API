package app.dtos;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private String sourceUrl;
    private String sourceName;
    private Instant createdAt;
    private String authorUsername;          // references User
    // Optional, for including comments in responses if desired
    private List<CommentDTO> comments;      // optional, can be null
}
