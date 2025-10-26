package app.daos;

import app.entities.Vote;
import app.entities.User;
import app.entities.Post;
import app.populators.TestPopulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VoteDAOTest extends BaseDAOTest {

    private VoteDAO voteDAO;
    private UserDAO userDAO;
    private PostDAO postDAO;
    private TestPopulator populator;

    @BeforeEach
    void setUp() {
        voteDAO = new VoteDAO(emf);
        userDAO = new UserDAO(emf);
        postDAO = new PostDAO(emf);
        populator = new TestPopulator(emf);
        populator.populate();
    }

    @Test
    void testFindAll() {
        List<Vote> votes = voteDAO.findAll();
        assertNotNull(votes);
    }

    @Test
    void testCreateAndFind() {
        User user = userDAO.findByUsername("admin");
        Post post = postDAO.findAll().get(0);

        Vote vote = Vote.builder()
                .user(user)
                .post(post)
                .value(1)
                .build();

        voteDAO.create(vote);
        assertNotNull(vote.getId());

        Vote found = voteDAO.find(vote.getId());
        assertEquals(1, found.getValue());
        assertEquals("admin", found.getUser().getUsername());
    }

    @Test
    void testUpdate() {
        Vote vote = voteDAO.findAll().get(0);
        int oldValue = vote.getValue();
        vote.setValue(oldValue == 1 ? -1 : 1);
        voteDAO.update(vote);

        Vote updated = voteDAO.find(vote.getId());
        assertNotEquals(oldValue, updated.getValue());
    }

    @Test
    void testDelete() {
        Vote vote = voteDAO.findAll().get(0);
        boolean deleted = voteDAO.delete(vote.getId());
        assertTrue(deleted);
        assertNull(voteDAO.find(vote.getId()));
    }
}
