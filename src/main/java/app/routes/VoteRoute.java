package app.routes;

import app.controllers.VoteController;
import app.daos.CommentDAO;
import app.daos.PostDAO;
import app.daos.UserDAO;
import app.daos.VoteDAO;
import app.configs.HibernateConfig;
import app.enums.UserRole;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class VoteRoute {

    private final VoteController voteController;

    public VoteRoute() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.voteController = new VoteController(
                new VoteDAO(emf),
                new PostDAO(emf),
                new CommentDAO(emf),
                new UserDAO(emf)
        );
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
