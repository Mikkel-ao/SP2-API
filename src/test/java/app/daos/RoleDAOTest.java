package app.daos;

import app.entities.Role;
import app.populators.TestPopulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoleDAOTest extends BaseDAOTest {

    private RoleDAO roleDAO;
    private TestPopulator populator;

    @BeforeEach
    void setUp() {
        roleDAO = new RoleDAO(emf);
        populator = new TestPopulator(emf);
        populator.populate();
    }

    @Test
    void testFindAll() {
        List<Role> roles = roleDAO.findAll();
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
    }

    @Test
    void testCreateAndFind() {
        Role newRole = new Role("TEST_ROLE");
        roleDAO.create(newRole);

        Role found = roleDAO.find("TEST_ROLE");
        assertNotNull(found);
        assertEquals("TEST_ROLE", found.getRoleName());
    }

    @Test
    void testDelete() {
        Role tempRole = new Role("DELETE_ME");
        roleDAO.create(tempRole);

        boolean deleted = roleDAO.delete("DELETE_ME");
        assertTrue(deleted);
        assertNull(roleDAO.find("DELETE_ME"));
    }
}
