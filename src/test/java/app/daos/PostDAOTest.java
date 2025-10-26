package app.daos;

import app.entities.Post;
import app.entities.User;
import app.populators.TestPopulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostDAOTest extends BaseDAOTest {

    private PostDAO postDAO;
    private UserDAO userDAO;
    private TestPopulator populator;

    @BeforeEach
    void setUp() {
        postDAO = new PostDAO(emf);
        userDAO = new UserDAO(emf);
        populator = new TestPopulator(emf);
        populator.populate();
    }

    @Test
    void testFindAll() {
        List<Post> posts = postDAO.findAll();
        assertNotNull(posts);
        assertFalse(posts.isEmpty());
    }

    @Test
    void testFind() {
        Post existing = postDAO.findAll().get(0);
        Post found = postDAO.find(existing.getId());
        assertEquals(existing.getId(), found.getId());
    }

    @Test
    void testCreate() {
        // Need to use an admin role to create a post
        User author = userDAO.findByUsername("admin");
        Post post = new Post();
        post.setTitle("Populator Test Post");
        post.setContent("This is a post created in test");
        post.setCreatedAt(Instant.now());
        post.setAuthor(author);
        postDAO.create(post);

        assertNotNull(post.getId());
        Post found = postDAO.find(post.getId());
        assertEquals("Populator Test Post", found.getTitle());
    }

    @Test
    void testUpdate() {
        Post post = postDAO.findAll().get(0);
        String originalTitle = post.getTitle();
        post.setTitle(originalTitle + " (edited)");
        postDAO.update(post);

        Post updated = postDAO.find(post.getId());
        assertTrue(updated.getTitle().endsWith("(edited)"));
    }

    @Test
    void testDelete() {
        Post post = postDAO.findAll().get(0);
        boolean deleted = postDAO.delete(post.getId());
        assertTrue(deleted);
        assertNull(postDAO.find(post.getId()));
    }
}
