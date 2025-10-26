package app.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoteDTO {
    private Integer value;    // +1 or -1
    private Long postId;      // nullable
    private Long commentId;   // nullable
}
