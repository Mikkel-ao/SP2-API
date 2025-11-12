package app.routes;

import app.configs.HibernateConfig;
import app.controllers.CommentController;
import app.daos.CommentDAO;
import app.daos.PostDAO;
import app.daos.UserDAO;
import app.enums.UserRole;
import app.services.CommentService;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class CommentRoute {

    private final CommentController commentController;

    public CommentRoute() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        CommentDAO commentDAO = new CommentDAO(emf);
        PostDAO postDAO = new PostDAO(emf);
        UserDAO userDAO = new UserDAO(emf);
        // Create service with DAOs
        CommentService commentService = new CommentService(commentDAO, postDAO, userDAO);
        // Create controller with service
        this.commentController = new CommentController(commentService);
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
