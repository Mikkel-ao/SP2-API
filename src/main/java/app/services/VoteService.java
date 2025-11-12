package app.services;

import app.daos.CommentDAO;
import app.daos.PostDAO;
import app.daos.UserDAO;
import app.daos.VoteDAO;
import app.dtos.VoteDTO;
import app.entities.Comment;
import app.entities.Post;
import app.entities.User;
import app.entities.Vote;
import app.mappers.VoteMapper;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ConflictResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;

import java.util.List;
import java.util.stream.Collectors;

public class VoteService {

    private final VoteDAO voteDAO;
    private final PostDAO postDAO;
    private final CommentDAO commentDAO;
    private final UserDAO userDAO;

    public VoteService(VoteDAO voteDAO, PostDAO postDAO, CommentDAO commentDAO, UserDAO userDAO) {
        this.voteDAO = voteDAO;
        this.postDAO = postDAO;
        this.commentDAO = commentDAO;
        this.userDAO = userDAO;
    }

    public List<VoteDTO> getAll() {
        return voteDAO.findAll().stream()
                .map(VoteMapper::toDTO)
                .collect(Collectors.toList());
    }

    public VoteDTO getById(Long id) {
        Vote vote = voteDAO.find(id);
        if (vote == null) throw new NotFoundResponse("Vote not found");
        return VoteMapper.toDTO(vote);
    }

    public VoteDTO create(VoteDTO input, UserDTO userDTO) {
        if (userDTO == null) throw new UnauthorizedResponse("Login required");

        if (input.getValue() == null || (input.getValue() != 1 && input.getValue() != -1))
            throw new BadRequestResponse("Vote value must be +1 or -1");

        User user = userDAO.findByUsername(userDTO.getUsername());
        if (user == null) throw new UnauthorizedResponse("User not found");

        Post post = null;
        Comment comment = null;

        if (input.getPostId() != null) {
            post = postDAO.find(input.getPostId());
            if (post == null) throw new NotFoundResponse("Post not found");
        } else if (input.getCommentId() != null) {
            comment = commentDAO.find(input.getCommentId());
            if (comment == null) throw new NotFoundResponse("Comment not found");
        } else {
            throw new BadRequestResponse("Vote must target either a post or a comment");
        }

        boolean alreadyVoted = (post != null)
                ? voteDAO.findByPostId(post.getId())
                .stream().anyMatch(v -> v.getUser().getUsername().equals(user.getUsername()))
                : voteDAO.findByCommentId(comment.getId())
                .stream().anyMatch(v -> v.getUser().getUsername().equals(user.getUsername()));

        if (alreadyVoted) throw new ConflictResponse("User has already voted on this item");

        Vote vote = VoteMapper.toEntity(input);
        vote.setUser(user);
        vote.setPost(post);
        vote.setComment(comment);

        voteDAO.create(vote);
        return VoteMapper.toDTO(vote);
    }

    public void delete(Long id, UserDTO userDTO) {
        if (userDTO == null) throw new UnauthorizedResponse("Login required");

        Vote vote = voteDAO.find(id);
        if (vote == null) throw new NotFoundResponse("Vote not found");

        boolean isOwner = vote.getUser().getUsername().equals(userDTO.getUsername());
        boolean isAdmin = userDTO.getRoles().contains("ADMIN");
        if (!isOwner && !isAdmin) throw new UnauthorizedResponse("Not allowed to delete this vote");

        voteDAO.delete(id);
    }
}
