package app.controllers;

import app.daos.CommentDAO;
import app.daos.PostDAO;
import app.daos.UserDAO;
import app.entities.Comment;
import app.entities.Post;
import app.entities.User;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

import java.time.Instant;
import java.util.List;

public class CommentController {

    private final CommentDAO commentDAO;
    private final PostDAO postDAO;
    private final UserDAO userDAO;

    public CommentController(CommentDAO commentDAO, PostDAO postDAO, UserDAO userDAO) {
        this.commentDAO = commentDAO;
        this.postDAO = postDAO;
        this.userDAO = userDAO;
    }

    public void getAll(Context ctx) {
        ctx.json(commentDAO.findAll());
    }

    public void getById(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Comment comment = commentDAO.find(id);
        if (comment == null) {
            ctx.status(404).json("{\"msg\":\"Comment not found\"}");
            return;
        }
        ctx.json(comment);
    }

    public void create(Context ctx) {
        UserDTO userDTO = ctx.attribute("user");
        if (userDTO == null) throw new UnauthorizedResponse("Login required");

        Comment input = ctx.bodyAsClass(Comment.class);
        if (input.getContent() == null || input.getContent().isBlank()) {
            ctx.status(400).json("{\"msg\":\"Content cannot be empty\"}");
            return;
        }

        Post post = postDAO.find(input.getPost().getId());
        if (post == null) {
            ctx.status(404).json("{\"msg\":\"Post not found\"}");
            return;
        }

        User author = userDAO.findByUsername(userDTO.getUsername());
        if (author == null) throw new UnauthorizedResponse("User not found");

        Comment comment = Comment.builder()
                .content(input.getContent())
                .createdAt(Instant.now())
                .author(author)
                .post(post)
                .build();

        commentDAO.create(comment);
        ctx.status(201).json(comment);
    }

    public void delete(Context ctx) {
        UserDTO userDTO = ctx.attribute("user");
        if (userDTO == null) throw new UnauthorizedResponse("Login required");

        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Comment comment = commentDAO.find(id);
        if (comment == null) {
            ctx.status(404).json("{\"msg\":\"Comment not found\"}");
            return;
        }

        if (!userDTO.getRoles().contains("ADMIN") &&
                !comment.getAuthor().getUsername().equals(userDTO.getUsername())) {
            throw new UnauthorizedResponse("Not allowed to delete this comment");
        }

        commentDAO.delete(id);
        ctx.status(204);
    }
}
