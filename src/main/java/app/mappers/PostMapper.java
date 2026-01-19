package app.mappers;

import app.dtos.PostDTO;
import app.entities.Post;
import java.util.stream.Collectors;

public class PostMapper {

    public static PostDTO toDTO(Post post) {
        if (post == null) return null;

        return PostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .sourceUrl(post.getSourceUrl())
                .sourceName(post.getSourceName())
                .createdAt(post.getCreatedAt())
                .authorUsername(
                        post.getAuthor() != null
                                ? post.getAuthor().getUsername()
                                : null
                )
                .comments(
                        post.getComments() != null
                                ? post.getComments().stream()
                                .filter(c -> !c.isDeleted())
                                .filter(c -> c.getParent() == null) // only top-level comments
                                .map(CommentMapper::toDTO)
                                .collect(Collectors.toList())
                                : null
                )
                .build();
    }


    public static Post toEntity(PostDTO dto) {
        if (dto == null) return null;

        return Post.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .sourceUrl(dto.getSourceUrl())
                .sourceName(dto.getSourceName())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}
