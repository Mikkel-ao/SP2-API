package app.populators;

import app.daos.CommentDAO;
import app.daos.PostDAO;
import app.daos.UserDAO;
import app.entities.Comment;
import app.entities.Post;
import app.entities.User;
import jakarta.persistence.EntityManagerFactory;

import java.time.Instant;

public class CommentPopulator {
    private final CommentDAO commentDAO;
    private final PostDAO postDAO;
    private final UserDAO userDAO;

    public CommentPopulator(EntityManagerFactory emf) {
        this.commentDAO = new CommentDAO(emf);
        this.postDAO = new PostDAO(emf);
        this.userDAO = new UserDAO(emf);
    }

    public void populate() {
        User user = userDAO.findByUsername("user");
        User admin = userDAO.findByUsername("admin");
        Post post = postDAO.find(1L);
        if (user == null || post == null) return;

        Comment topLevel = Comment.builder()
                .content("Looking forward to more updates!")
                .author(user)
                .post(post)
                .createdAt(Instant.now())
                .build();

        commentDAO.create(topLevel);

        Comment reply = Comment.builder()
                .content("Thanks for the feedback! More coming soon.")
                .author(admin)
                .post(post)
                .parent(topLevel)
                .createdAt(Instant.now())
                .build();

        commentDAO.create(reply);
    }
}
