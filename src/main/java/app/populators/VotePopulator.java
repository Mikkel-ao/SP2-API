package app.populators;

import app.daos.VoteDAO;
import app.daos.PostDAO;
import app.daos.CommentDAO;
import app.daos.UserDAO;
import app.entities.Vote;
import app.entities.Post;
import app.entities.Comment;
import app.entities.User;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class VotePopulator {

    private final VoteDAO voteDAO;
    private final PostDAO postDAO;
    private final CommentDAO commentDAO;
    private final UserDAO userDAO;

    public VotePopulator(EntityManagerFactory emf) {
        this.voteDAO = new VoteDAO(emf);
        this.postDAO = new PostDAO(emf);
        this.commentDAO = new CommentDAO(emf);
        this.userDAO = new UserDAO(emf);
    }

    public void populate() {
        // Get existing users
        User user = userDAO.findByUsername("user");
        User admin = userDAO.findByUsername("admin");
        if (user == null || admin == null) return;

        // Example: vote on a post
        Post post = postDAO.find(1L); // make sure post with id 1 exists
        if (post != null) {
            Vote vote1 = Vote.builder()
                    .value(1)
                    .user(user)
                    .post(post)
                    .build();

            Vote vote2 = Vote.builder()
                    .value(-1)
                    .user(admin)
                    .post(post)
                    .build();

            voteDAO.create(vote1);
            voteDAO.create(vote2);
        }

        // Example: vote on a comment
        List<Comment> comments = commentDAO.findAll();
        for (Comment c : comments) {
            Vote vote = Vote.builder()
                    .value(1)
                    .user(user)
                    .comment(c)
                    .build();
            voteDAO.create(vote);
        }
    }
}
