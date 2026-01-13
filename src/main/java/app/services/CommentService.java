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

    /** Recursively initializes all comment relations to prevent lazy-loading issues */
    private void initializeComment(Comment comment) {
        if (comment == null) return;

        // Author
        if (comment.getAuthor() != null) comment.getAuthor().getUsername();

        // Post
        if (comment.getPost() != null) comment.getPost().getId();

        // Parent
        if (comment.getParent() != null) comment.getParent().getId();

        // Recursively initialize replies
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            comment.getReplies().forEach(this::initializeComment);
        }
    }

    /** Get all comments */
    public List<CommentDTO> getAll() {
        List<Comment> comments = commentDAO.findAll();
        comments.forEach(this::initializeComment);
        return comments.stream()
                .map(CommentMapper::toDTO)
                .toList();
    }

    /** Get comment by ID */
    public CommentDTO getById(Long id) {
        Comment comment = commentDAO.find(id);
        if (comment == null)
            throw new NotFoundResponse("Comment not found");

        initializeComment(comment);
        return CommentMapper.toDTO(comment);
    }

    /** Create a new comment */
    public CommentDTO create(CommentDTO input, UserDTO userDTO) {
        if (userDTO == null)
            throw new UnauthorizedResponse("Login required");

        if (input.getContent() == null || input.getContent().isBlank())
            throw new BadRequestResponse("Content cannot be empty");

        Post post = postDAO.find(input.getPostId());
        if (post == null)
            throw new NotFoundResponse("Post not found");

        User author = userDAO.findByUsername(userDTO.getUsername());
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
        initializeComment(comment);

        return CommentMapper.toDTO(comment);
    }


    /** Update comment */
    public CommentDTO update(Long id, CommentDTO input, UserDTO userDTO) {
        if (userDTO == null)
            throw new UnauthorizedResponse("Login required");

        Comment existing = commentDAO.find(id);
        if (existing == null)
            throw new NotFoundResponse("Comment not found");

        boolean isAdmin = userDTO.getRoles().contains("ADMIN");
        boolean isOwner = existing.getAuthor().getUsername().equals(userDTO.getUsername());
        if (!isAdmin && !isOwner)
            throw new UnauthorizedResponse("Not allowed to update this comment");

        existing.setContent(input.getContent());
        commentDAO.update(existing);
        initializeComment(existing);

        return CommentMapper.toDTO(existing);
    }

    /** Delete comment */
    public void delete(Long id, UserDTO userDTO) {
        if (userDTO == null)
            throw new UnauthorizedResponse("Login required");

        Comment existing = commentDAO.find(id);
        if (existing == null)
            throw new NotFoundResponse("Comment not found");

        boolean isAdmin = userDTO.getRoles().contains("ADMIN");
        boolean isOwner = existing.getAuthor().getUsername().equals(userDTO.getUsername());
        if (!isAdmin && !isOwner)
            throw new UnauthorizedResponse("Not allowed to delete this comment");

        commentDAO.delete(id);
    }
}
