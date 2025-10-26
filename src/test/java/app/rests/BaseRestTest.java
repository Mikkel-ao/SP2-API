package app.rests;

import app.configs.HibernateConfig;
import app.populators.TestPopulator;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseRestTest {

    protected static EntityManagerFactory emf;
    protected static TestPopulator populator;

    protected static final String BASE_URL = "http://localhost:7070/api";

    @BeforeAll
    static void setUpBase() {
        // Initialize JPA
        emf = HibernateConfig.getEntityManagerFactoryForTest();

        // Populate test data
        populator = new TestPopulator(emf);
        populator.populate();

        // Configure RestAssured
        RestAssured.baseURI = BASE_URL;

        // Start embedded server if necessary (e.g., Javalin)
        // Example:
        // AppServer.startForTests();
    }

    @AfterAll
    static void tearDownBase() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }

        // Stop embedded server if you started it
        // AppServer.stop();
    }

    protected String login(String username, String password) {
        return io.restassured.RestAssured.given()
                .contentType(io.restassured.http.ContentType.JSON)
                .body("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
                .when().post(BASE_URL + "/auth/login")
                .then().statusCode(200)
                .extract().path("token");
    }
}
