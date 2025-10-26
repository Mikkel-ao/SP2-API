package app.controllers;

import app.daos.PostDAO;
import app.daos.UserDAO;
import app.entities.Post;
import app.entities.User;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

import java.time.Instant;
import java.util.List;

public class PostController {

    private final PostDAO postDAO;
    private final UserDAO userDAO;

    public PostController(PostDAO postDAO, UserDAO userDAO) {
        this.postDAO = postDAO;
        this.userDAO = userDAO;
    }

    public void getAll(Context ctx) {
        List<Post> posts = postDAO.findAll();
        ctx.status(200).json(posts);
    }

    public void getById(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Post post = postDAO.find(id);
        if (post == null) {
            ctx.status(404).json("{\"msg\":\"Post not found\"}");
            return;
        }
        ctx.status(200).json(post);
    }

    public void create(Context ctx) {
        UserDTO user = ctx.attribute("user");
        if (user == null || !user.getRoles().contains("ADMIN"))
            throw new UnauthorizedResponse("Only ADMIN users can create posts.");

        Post input = ctx.bodyAsClass(Post.class);
        if (input.getTitle() == null || input.getTitle().isBlank()) {
            ctx.status(400).json("{\"msg\":\"Title must not be empty\"}");
            return;
        }

        if (postDAO.existsByTitle(input.getTitle())) {
            ctx.status(409).json("{\"msg\":\"A post with this title already exists\"}");
            return;
        }

        User author = userDAO.findByUsername(user.getUsername());
        if (author == null) throw new UnauthorizedResponse("Author not found");

        Post post = Post.builder()
                .title(input.getTitle())
                .content(input.getContent())
                .sourceUrl(input.getSourceUrl())
                .sourceName(input.getSourceName())
                .createdAt(Instant.now())
                .author(author)
                .build();

        postDAO.create(post);
        ctx.status(201).json(post);
    }

    public void update(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Post existing = postDAO.find(id);
        if (existing == null) {
            ctx.status(404).json("{\"msg\":\"Post not found\"}");
            return;
        }

        UserDTO user = ctx.attribute("user");
        if (user == null || !user.getRoles().contains("ADMIN"))
            throw new UnauthorizedResponse("Only ADMIN users can update posts.");

        Post input = ctx.bodyAsClass(Post.class);
        existing.setTitle(input.getTitle());
        existing.setContent(input.getContent());
        existing.setSourceUrl(input.getSourceUrl());
        existing.setSourceName(input.getSourceName());

        postDAO.save(existing);
        ctx.status(200).json(existing);
    }

    public void delete(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();

        UserDTO user = ctx.attribute("user");
        if (user == null || !user.getRoles().contains("ADMIN"))
            throw new UnauthorizedResponse("Only ADMIN users can delete posts.");

        boolean removed = postDAO.delete(id);
        if (removed) ctx.status(204);
        else ctx.status(404).json("{\"msg\":\"Post not found\"}");
    }
}
