package app.mappers;

import app.dtos.CommentDTO;
import app.entities.Comment;
import java.util.stream.Collectors;
import java.util.List;

public class CommentMapper {

    public static CommentDTO toDTO(Comment comment) {
        if (comment == null) return null;

        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .authorUsername(comment.getAuthor() != null ? comment.getAuthor().getUsername() : null)
                .postId(comment.getPost() != null ? comment.getPost().getId() : null)
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .deleted(comment.isDeleted())
                .replies(comment.getReplies() != null
                        ? comment.getReplies().stream().map(CommentMapper::toDTO).collect(Collectors.toList())
                        : null)
                .build();
    }

    public static Comment toEntity(CommentDTO dto) {
        if (dto == null) return null;

        return Comment.builder()
                .id(dto.getId())
                .content(dto.getContent())
                .createdAt(dto.getCreatedAt())
                .deleted(dto.isDeleted())
                // post, author, and parent must be resolved externally
                .build();
    }

    public static List<CommentDTO> toDTOList(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toDTO).collect(Collectors.toList());
    }
}
