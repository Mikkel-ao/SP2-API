package app.routes;

import app.configs.AppContainer;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final PostRoute postRoute;
    private final CommentRoute commentRoute;
    private final VoteRoute voteRoute;
    private final NewsApiRoute externalNewsRoute;

    public Routes(AppContainer container) {
        this.postRoute = new PostRoute(container.postController);
        this.commentRoute = new CommentRoute(container.commentController, container.voteController);
        this.voteRoute = new VoteRoute(container.voteController);
        this.externalNewsRoute = new NewsApiRoute(container.newsApiController);
    }

    public EndpointGroup getRoutes() {
        return () -> {
            path("/posts", postRoute.getRoutes());
            path("/comments", commentRoute.getRoutes());
            path("/votes", voteRoute.getRoutes());
            path("/external", externalNewsRoute.getRoutes());
        };
    }
}
