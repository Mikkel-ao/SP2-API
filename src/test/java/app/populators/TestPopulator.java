package app.populators;

import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPopulator {

    private static final Logger log = LoggerFactory.getLogger(TestPopulator.class);

    private final DataPopulator dataPopulator;

    public TestPopulator(EntityManagerFactory emf) {
        this.dataPopulator = new DataPopulator(emf);
    }

    // TODO: Consider making new entities instead of reusing the 'production' populator
    public void populate() {
        log.info("Starting database population for test environment...");
        try {
            dataPopulator.populateAll();
            log.info("Test database population completed.");
        } catch (Exception e) {
            log.error("Error occurred during test data population", e);
        }
    }
}
