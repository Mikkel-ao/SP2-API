package app.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {
    private final PostRoute postRoute = new PostRoute();
    private final CommentRoute commentRoute = new CommentRoute();
    private final VoteRoute voteRoute = new VoteRoute();
    private final NewsApiRoute externalNewsRoute = new NewsApiRoute();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/posts", postRoute.getRoutes());
            path("/comments", commentRoute.getRoutes());
            path("/votes", voteRoute.getRoutes());
            path("/external", externalNewsRoute.getRoutes());
        };
    }
}
