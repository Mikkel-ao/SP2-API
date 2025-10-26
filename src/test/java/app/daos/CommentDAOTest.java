package app.daos;

import app.entities.Comment;
import app.entities.Post;
import app.entities.User;
import app.populators.TestPopulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentDAOTest extends BaseDAOTest {

    private CommentDAO commentDAO;
    private UserDAO userDAO;
    private PostDAO postDAO;
    private TestPopulator populator;

    @BeforeEach
    void setUp() {
        commentDAO = new CommentDAO(emf);
        userDAO = new UserDAO(emf);
        postDAO = new PostDAO(emf);
        populator = new TestPopulator(emf);
        populator.populate();
    }

    @Test
    void testFindAll() {
        List<Comment> comments = commentDAO.findAll();
        assertNotNull(comments);
    }

    @Test
    void testCreateAndFind() {
        User author = userDAO.findByUsername("user");
        Post post = postDAO.findAll().get(0);

        Comment comment = Comment.builder()
                .content("Test comment from populator")
                .createdAt(Instant.now())
                .author(author)
                .post(post)
                .build();

        commentDAO.create(comment);
        assertNotNull(comment.getId());

        Comment found = commentDAO.find(comment.getId());
        assertEquals("Test comment from populator", found.getContent());
    }

    @Test
    void testUpdate() {
        Comment comment = commentDAO.findAll().get(0);
        comment.setContent("Updated content");
        commentDAO.update(comment);

        Comment updated = commentDAO.find(comment.getId());
        assertEquals("Updated content", updated.getContent());
    }

    @Test
    void testSoftDelete() {
        Comment comment = commentDAO.findAll().get(0);
        boolean deleted = commentDAO.delete(comment.getId());
        assertTrue(deleted);

        Comment softDeleted = commentDAO.find(comment.getId());
        assertTrue(softDeleted.isDeleted());
        assertEquals("[deleted]", softDeleted.getContent());
    }
}
