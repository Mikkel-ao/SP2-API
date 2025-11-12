package app.routes;

import app.configs.HibernateConfig;
import app.controllers.PostController;
import app.daos.PostDAO;
import app.daos.UserDAO;
import app.enums.UserRole;
import app.services.PostService;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PostRoute {

    private final PostController postController;

    public PostRoute() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        // Create DAOs
        PostDAO postDAO = new PostDAO(emf);
        UserDAO userDAO = new UserDAO(emf);

        // Create service
        PostService postService = new PostService(postDAO, userDAO);

        // Create controller
        this.postController = new PostController(postService);
    }

    protected EndpointGroup getRoutes() {
        return () -> {
            // Public routes
            get("/", postController::getAll, UserRole.ANYONE);
            get("/{id}", postController::getById, UserRole.ANYONE);

            // Admin-only routes
            post("/", postController::create, UserRole.ADMIN);
            put("/{id}", postController::update, UserRole.ADMIN);
            delete("/{id}", postController::delete, UserRole.ADMIN);
        };
    }
}
