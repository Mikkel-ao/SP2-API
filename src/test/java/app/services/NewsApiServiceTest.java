package app.services;

import app.configs.HibernateConfig;
import app.daos.PostDAO;
import app.daos.UserDAO;
import app.entities.Post;
import app.entities.User;
import app.populators.TestPopulator;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NewsApiServiceTest {
/*
    private NewsApiService newsApiService;
    private UserDAO userDAO;
    private PostDAO postDAO;
    private EntityManagerFactory emf;

    @BeforeEach
    void setup() {
        String apiKey = System.getenv("NEWS_API_KEY");
        Assumptions.assumeTrue(apiKey != null && !apiKey.isBlank(),
                "Skipping test: NEWS_API_KEY not set in environment.");

        emf = HibernateConfig.getEntityManagerFactoryForTest();
        userDAO = new UserDAO(emf);
        postDAO = new PostDAO(emf);
        newsApiService = new NewsApiService(postDAO, userDAO);

        if (userDAO.findByUsername("admin") == null) {
            userDAO.create(new User("admin", "adminpass"));
        }
    }



    @Test
    void fetchAndSaveNewsTest() {
        // Act
        List<Post> posts = newsApiService.fetchAndSaveNews();

        // Assert
        assertNotNull(posts, "The fetched post list should not be null");
        assertFalse(posts.isEmpty(), "There should be at least one post saved");

        // Check that all posts have a valid admin author
        for (Post post : posts) {
            assertNotNull(post.getAuthor(), "Post author should not be null");
            assertEquals("admin", post.getAuthor().getUsername(), "Author should be admin");
            assertNotNull(post.getTitle(), "Post should have a title");
            assertNotNull(post.getContent(), "Post should have content");
        }
    }
    
 */
}
