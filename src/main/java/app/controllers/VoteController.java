package app.controllers;

import app.daos.CommentDAO;
import app.daos.PostDAO;
import app.daos.UserDAO;
import app.daos.VoteDAO;
import app.entities.Comment;
import app.entities.Post;
import app.entities.User;
import app.entities.Vote;
import app.dtos.VoteDTO;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

import java.util.List;

public class VoteController {

    private final VoteDAO voteDAO;
    private final PostDAO postDAO;
    private final CommentDAO commentDAO;
    private final UserDAO userDAO;

    public VoteController(VoteDAO voteDAO, PostDAO postDAO, CommentDAO commentDAO, UserDAO userDAO) {
        this.voteDAO = voteDAO;
        this.postDAO = postDAO;
        this.commentDAO = commentDAO;
        this.userDAO = userDAO;
    }

    // Fetch all votes
    public void getAll(Context ctx) {
        List<Vote> votes = voteDAO.findAll();
        ctx.status(200).json(votes);
    }

    // Fetch vote by ID
    public void getById(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Vote vote = voteDAO.find(id);
        if (vote == null) {
            ctx.status(404).json("{\"msg\":\"Vote not found\"}");
            return;
        }
        ctx.status(200).json(vote);
    }

    // Create a vote (for a post or comment)
    public void create(Context ctx) {
        UserDTO userDTO = ctx.attribute("user");
        if (userDTO == null) throw new UnauthorizedResponse("You must be logged in to vote.");

        VoteDTO input = ctx.bodyAsClass(VoteDTO.class);

        User user = userDAO.findByUsername(userDTO.getUsername());
        if (user == null) {
            ctx.status(404).json("{\"msg\":\"User not found\"}");
            return;
        }

        Post post = null;
        Comment comment = null;

        if (input.getPostId() != null) {
            post = postDAO.find(input.getPostId());
            if (post == null) {
                ctx.status(404).json("{\"msg\":\"Post not found\"}");
                return;
            }
        } else if (input.getCommentId() != null) {
            comment = commentDAO.find(input.getCommentId());
            if (comment == null) {
                ctx.status(404).json("{\"msg\":\"Comment not found\"}");
                return;
            }
        } else {
            ctx.status(400).json("{\"msg\":\"Vote must target a post or a comment\"}");
            return;
        }

        // Prevent duplicate votes
        boolean alreadyVoted = (post != null)
                ? voteDAO.findByPostId(post.getId()).stream().anyMatch(v -> v.getUser().getUsername().equals(user.getUsername()))
                : voteDAO.findByCommentId(comment.getId()).stream().anyMatch(v -> v.getUser().getUsername().equals(user.getUsername()));

        if (alreadyVoted) {
            ctx.status(409).json("{\"msg\":\"User has already voted on this item\"}");
            return;
        }

        Vote vote = Vote.builder()
                .value(input.getValue())
                .user(user)
                .post(post)
                .comment(comment)
                .build();

        voteDAO.create(vote);
        ctx.status(201).json(vote);
    }

    // Delete a vote by ID
    public void delete(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        boolean removed = voteDAO.delete(id);

        if (removed) ctx.status(204);
        else ctx.status(404).json("{\"msg\":\"Vote not found\"}");
    }
}
