package app.routes;

import app.controllers.PostController;
import app.daos.PostDAO;
import app.daos.UserDAO;
import app.configs.HibernateConfig;
import app.enums.UserRole;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PostRoute {
    private final PostController postController;

    public PostRoute() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.postController = new PostController(new PostDAO(emf), new UserDAO(emf));
    }

    protected EndpointGroup getRoutes() {
        return () -> {
            // Public endpoints
            get("/", postController::getAll, UserRole.ANYONE);
            get("/{id}", postController::getById, UserRole.ANYONE);

            // Admin endpoints
            post("/", postController::create, UserRole.ADMIN);
            put("/{id}", postController::update, UserRole.ADMIN);
            delete("/{id}", postController::delete, UserRole.ADMIN);
        };
    }
}
