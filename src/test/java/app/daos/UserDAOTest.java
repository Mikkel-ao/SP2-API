package app.daos;

import app.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest extends BaseDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUpDAO() {
        userDAO = new UserDAO(emf);
    }

    @Test
    void testFindAll() {
        List<User> users = userDAO.findAll();
        assertNotNull(users);
        assertTrue(users.size() >= 2, "Populator should create at least admin and user");
    }

    @Test
    void testFindByUsername() {
        User user = userDAO.findByUsername("admin");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    void testCreateAndFind() {
        User newUser = new User("newUser", "password");
        userDAO.create(newUser);

        User found = userDAO.findByUsername("newUser");
        assertNotNull(found);
        assertEquals("newUser", found.getUsername());
    }

    @Test
    void testUpdate() {
        User user = userDAO.findByUsername("admin");
        user.setUsername("superadmin");
        userDAO.update(user);

        User updated = userDAO.findByUsername("superadmin");
        assertNotNull(updated);
        assertEquals("superadmin", updated.getUsername());
    }


    @Test
    void testDelete() {
        boolean deleted = userDAO.delete("user");
        assertTrue(deleted);

        User shouldStillExist = userDAO.findByUsername("user");
        assertNotNull(shouldStillExist);
        assertTrue(shouldStillExist.isDeleted()); // user is soft deleted
    }



}
