package app.services;

import app.daos.CommentDAO;
import app.daos.PostDAO;
import app.daos.UserDAO;
import app.dtos.CommentDTO;
import app.entities.Comment;
import app.entities.Post;
import app.entities.User;
import app.mappers.CommentMapper;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;

import java.time.Instant;
import java.util.List;

public class CommentService {

    private final CommentDAO commentDAO;
    private final PostDAO postDAO;
    private final UserDAO userDAO;

    public CommentService(CommentDAO commentDAO, PostDAO postDAO, UserDAO userDAO) {
        this.commentDAO = commentDAO;
        this.postDAO = postDAO;
        this.userDAO = userDAO;
    }

    private User resolveUser(UserDTO userDTO) {
        return userDTO == null
                ? null
                : userDAO.findByUsername(userDTO.getUsername());
    }

    public List<CommentDTO> getAll(UserDTO userDTO) {
        User currentUser = resolveUser(userDTO);
        return commentDAO.findAll().stream()
                .map(c -> CommentMapper.toDTO(c, currentUser))
                .toList();
    }

    public CommentDTO getById(Long id, UserDTO userDTO) {
        Comment comment = commentDAO.find(id);
        if (comment == null)
            throw new NotFoundResponse("Comment not found");

        return CommentMapper.toDTO(comment, resolveUser(userDTO));
    }

    public CommentDTO create(CommentDTO input, UserDTO userDTO) {
        if (userDTO == null)
            throw new UnauthorizedResponse("Login required");

        if (input.getContent() == null || input.getContent().isBlank())
            throw new BadRequestResponse("Content cannot be empty");

        Post post = postDAO.find(input.getPostId());
        if (post == null)
            throw new NotFoundResponse("Post not found");

        User author = resolveUser(userDTO);
        if (author == null)
            throw new UnauthorizedResponse("User not found");

        Comment parent = null;
        if (input.getParentId() != null) {
            parent = commentDAO.find(input.getParentId());
            if (parent == null)
                throw new NotFoundResponse("Parent comment not found");
        }

        Comment comment = Comment.builder()
                .content(input.getContent())
                .createdAt(Instant.now())
                .author(author)
                .post(post)
                .parent(parent)
                .build();

        commentDAO.create(comment);
        return CommentMapper.toDTO(comment, author);
    }

    public CommentDTO update(Long id, CommentDTO input, UserDTO userDTO) {
        if (userDTO == null)
            throw new UnauthorizedResponse("Login required");

        Comment existing = commentDAO.find(id);
        if (existing == null)
            throw new NotFoundResponse("Comment not found");

        boolean isAdmin = userDTO.getRoles().contains("ADMIN");
        boolean isOwner =
                existing.getAuthor() != null &&
                        existing.getAuthor().getUsername().equals(userDTO.getUsername());

        if (!isAdmin && !isOwner)
            throw new UnauthorizedResponse("Not allowed");

        existing.setContent(input.getContent());
        commentDAO.update(existing);

        return CommentMapper.toDTO(existing, resolveUser(userDTO));
    }

    public void delete(Long id, UserDTO userDTO) {
        if (userDTO == null)
            throw new UnauthorizedResponse("Login required");

        Comment existing = commentDAO.find(id);
        if (existing == null)
            throw new NotFoundResponse("Comment not found");

        boolean isAdmin = userDTO.getRoles().contains("ADMIN");
        boolean isOwner =
                existing.getAuthor() != null &&
                        existing.getAuthor().getUsername().equals(userDTO.getUsername());

        if (!isAdmin && !isOwner)
            throw new UnauthorizedResponse("Not allowed");

        commentDAO.delete(id);
    }
}
