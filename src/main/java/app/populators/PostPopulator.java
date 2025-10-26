package app.populators;

import app.daos.PostDAO;
import app.daos.UserDAO;
import app.entities.Post;
import app.entities.User;
import jakarta.persistence.EntityManagerFactory;

import java.time.Instant;

public class PostPopulator {
    private final PostDAO postDAO;
    private final UserDAO userDAO;

    public PostPopulator(EntityManagerFactory emf) {
        this.postDAO = new PostDAO(emf);
        this.userDAO = new UserDAO(emf);
    }

    public void populate() {
        User admin = userDAO.findByUsername("admin");
        if (admin == null) return;

        Post post1 = Post.builder()
                .title("Welcome to the Platform")
                .content("This is the first official post!")
                .author(admin)
                .createdAt(Instant.now())
                .build();

        Post post2 = Post.builder()
                .title("Developer Update")
                .content("New features coming soon.")
                .author(admin)
                .createdAt(Instant.now())
                .build();

        postDAO.create(post1);
        postDAO.create(post2);
    }
}
