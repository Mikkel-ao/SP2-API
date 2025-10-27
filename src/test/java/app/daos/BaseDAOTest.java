package app.daos;

import app.configs.HibernateConfig;
import app.populators.TestPopulator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseDAOTest {

    protected static EntityManagerFactory emf;
    protected TestPopulator populator;

    @BeforeAll
    static void setupOnce() {
        // Create factory only once for all DAO tests
        if (emf == null || !emf.isOpen()) {
            emf = HibernateConfig.getEntityManagerFactoryForTest();
        }
    }

    @BeforeEach
    void resetDatabase() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("""
                TRUNCATE TABLE votes, comments, posts, users, roles RESTART IDENTITY CASCADE
            """).executeUpdate();
            em.getTransaction().commit();
        }
        if (populator == null) {
            populator = new TestPopulator(emf);
        }
        populator.populate();
    }

    @AfterAll
    static void tearDown() {
        // Optional: close at very end of test suite
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
