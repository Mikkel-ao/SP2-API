package app.mappers;

import app.dtos.CommentDTO;
import app.entities.Comment;
import app.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    // Primary, user-aware mapper
    public static CommentDTO toDTO(Comment comment, User currentUser) {
        if (comment == null) return null;

        boolean ownComment =
                currentUser != null &&
                        comment.getAuthor() != null &&
                        comment.getAuthor().getUsername().equals(currentUser.getUsername());

        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .authorUsername(
                        comment.isDeleted()
                                ? null
                                : comment.getAuthor() != null
                                ? comment.getAuthor().getUsername()
                                : null
                )
                .postId(comment.getPost() != null ? comment.getPost().getId() : null)
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .deleted(comment.isDeleted())
                .ownComment(ownComment)
                .replies(
                        comment.getReplies() == null
                                ? List.of()
                                : comment.getReplies().stream()
                                .map(r -> toDTO(r, currentUser))
                                .collect(Collectors.toList())
                )
                .build();
    }

    // Overload for non-user-aware contexts (e.g. PostMapper)
    public static CommentDTO toDTO(Comment comment) {
        return toDTO(comment, null);
    }
}
