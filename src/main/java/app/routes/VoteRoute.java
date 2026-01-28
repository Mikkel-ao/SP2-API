package app.routes;

import app.controllers.VoteController;
import app.enums.UserRole;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class VoteRoute {

    private final VoteController voteController;

    public VoteRoute(VoteController voteController) {
        this.voteController = voteController;
    }

    protected EndpointGroup getRoutes() {
        return () -> {
            post("/", voteController::create, UserRole.USER);
            get("/", voteController::getAll, UserRole.ANYONE);
            get("/{id}", voteController::getById, UserRole.ANYONE);
            delete("/{id}", voteController::delete, UserRole.USER);

            get("/post/{id}/score", voteController::getPostScore, UserRole.ANYONE);
            get("/comment/{id}/score", voteController::getCommentScore, UserRole.ANYONE);

        };
    }
}
