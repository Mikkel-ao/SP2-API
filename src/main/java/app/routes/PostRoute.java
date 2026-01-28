package app.routes;

import app.controllers.PostController;
import app.enums.UserRole;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PostRoute {

    private final PostController postController;

    public PostRoute(PostController postController) {
        this.postController = postController;
    }

    public EndpointGroup getRoutes() {
        return () -> {
            get("/", postController::getAll, UserRole.ANYONE);
            get("/{id}", postController::getById, UserRole.ANYONE);
            post("/", postController::create, UserRole.ADMIN);
            put("/{id}", postController::update, UserRole.ADMIN);
            delete("/{id}", postController::delete, UserRole.ADMIN);

        };
    }
}
