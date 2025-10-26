
package app.rests;

import app.daos.UserDAO;
import app.entities.Post;
import app.entities.User;
import app.populators.TestPopulator;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostApiTest extends BaseRestTest {

    private UserDAO userDAO;
    private TestPopulator populator;
    private String adminToken;

    @BeforeAll
    void setUpAll() {
        RestAssured.baseURI = BASE_URL;
        userDAO = new UserDAO(emf);
        populator = new TestPopulator(emf);
        populator.populate();
        adminToken = login("admin", "adminpassword");
    }

    @BeforeEach
    void resetDb() {
        populator.populate();
    }

    /*

    @Test
    void createPost() {
        User admin = userDAO.findByUsername("admin");
        Post post = Post.builder()
                .title("REST Test Post")
                .content("Created from REST test")
                .createdAt(Instant.now())
                .author(admin)
                .build();

        Post created = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(post)
                .when()
                .post(BASE_URL + "/posts")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("REST Test Post"))
                .body("content", equalTo("Created from REST test"))
                .extract().as(Post.class);

        assertThat(created.getTitle(), equalTo(post.getTitle()));
    }

    @Test
    void getAllPosts() {
        List<Post> posts = given()
                .when().get(BASE_URL + "/posts")
                .then().statusCode(200)
                .extract().jsonPath().getList("", Post.class);

        assertThat(posts, not(empty()));
    }

    @Test
    void updatePost() {
        Post post = given()
                .when().get(BASE_URL + "/posts")
                .then().statusCode(200)
                .extract().jsonPath().getObject("[0]", Post.class);

        post.setTitle(post.getTitle() + " (edited)");

        Post updated = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(post)
                .when().put(BASE_URL + "/posts/" + post.getId())
                .then().statusCode(200)
                .extract().as(Post.class);

        assertThat(updated.getTitle(), endsWith("(edited)"));
    }

    @Test
    void deletePost() {
        Post post = given()
                .when().get(BASE_URL + "/posts")
                .then().statusCode(200)
                .extract().jsonPath().getObject("[0]", Post.class);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when().delete(BASE_URL + "/posts/" + post.getId())
                .then().statusCode(200);

        given()
                .when().get(BASE_URL + "/posts/" + post.getId())
                .then().statusCode(404);
    }

    @Test
    void unauthorizedCreateFails() {
        Post post = Post.builder()
                .title("Fail Post")
                .content("Should fail")
                .createdAt(Instant.now())
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(post)
                .when().post(BASE_URL + "/posts")
                .then().statusCode(401);
    }

     */
}
