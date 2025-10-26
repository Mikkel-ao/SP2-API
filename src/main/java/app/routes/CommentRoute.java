package app.routes;

import app.controllers.CommentController;
import app.daos.CommentDAO;
import app.daos.PostDAO;
import app.daos.UserDAO;
import app.configs.HibernateConfig;
import app.enums.UserRole;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class CommentRoute {

    private final CommentController commentController;

    public CommentRoute() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.commentController = new CommentController(
                new CommentDAO(emf),
                new PostDAO(emf),
                new UserDAO(emf)
        );
    }

    protected EndpointGroup getRoutes() {
        return () -> {
            // Public endpoints
            get("/", commentController::getAll, UserRole.ANYONE);
            get("/{id}", commentController::getById, UserRole.ANYONE);

            // Protected endpoints (logged-in users)
            post("/", commentController::create, UserRole.USER);

            // Protected deletion (author or admin)
            delete("/{id}", commentController::delete, UserRole.USER);
        };
    }
}
