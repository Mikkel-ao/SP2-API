package app.routes;

import app.configs.HibernateConfig;
import app.controllers.VoteController;
import app.daos.CommentDAO;
import app.daos.PostDAO;
import app.daos.UserDAO;
import app.daos.VoteDAO;
import app.enums.UserRole;
import app.services.VoteService;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class VoteRoute {

    private final VoteController voteController;

    public VoteRoute() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        // Create DAOs
        VoteDAO voteDAO = new VoteDAO(emf);
        PostDAO postDAO = new PostDAO(emf);
        CommentDAO commentDAO = new CommentDAO(emf);
        UserDAO userDAO = new UserDAO(emf);

        // Create service
        VoteService voteService = new VoteService(voteDAO, postDAO, commentDAO, userDAO);

        // Create controller
        this.voteController = new VoteController(voteService);
    }

    protected EndpointGroup getRoutes() {
        return () -> {
            post("/", voteController::create, UserRole.USER);   // Logged-in users
            get("/", voteController::getAll, UserRole.ANYONE); // Public
            get("/{id}", voteController::getById, UserRole.ANYONE);
            delete("/{id}", voteController::delete, UserRole.USER); // Author or admin
        };
    }
}
