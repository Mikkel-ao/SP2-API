package app.routes;

import app.controllers.CommentController;
import app.controllers.VoteController;
import app.enums.UserRole;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class CommentRoute {

    private final CommentController commentController;
    private final VoteController voteController;

    public CommentRoute(CommentController commentController, VoteController voteController) {
        this.commentController = commentController;
        this.voteController = voteController;
    }

    public EndpointGroup getRoutes() {
        return () -> {
            get("/", commentController::getAll, UserRole.ANYONE);
            get("/{id}", commentController::getById, UserRole.ANYONE);
            get("/{id}/score", voteController::getCommentScore, UserRole.ANYONE);
            post("/", commentController::create, UserRole.USER);
            delete("/{id}", commentController::delete, UserRole.USER);
        };
    }
}
