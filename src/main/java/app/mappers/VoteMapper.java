package app.mappers;

import app.dtos.VoteDTO;
import app.entities.Vote;

public class VoteMapper {

    public static VoteDTO toDTO(Vote vote) {
        if (vote == null) return null;

        VoteDTO dto = new VoteDTO();
        dto.setValue(vote.getValue());
        dto.setPostId(vote.getPost() != null ? vote.getPost().getId() : null);
        dto.setCommentId(vote.getComment() != null ? vote.getComment().getId() : null);
        return dto;
    }

    public static Vote toEntity(VoteDTO dto) {
        if (dto == null) return null;

        return Vote.builder()
                .value(dto.getValue())
                // comment/post set externally
                .build();
    }
}
