package app.daos;

import app.configs.HibernateConfig;
import app.populators.TestPopulator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseDAOTest {

    protected static EntityManagerFactory emf;
    protected static TestPopulator populator;

    @BeforeAll
    static void setupOnce() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        populator = new TestPopulator(emf);
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
        populator.populate();
    }

    @AfterAll
    static void tearDown() {
        HibernateConfig.closeFactories();
    }
}
