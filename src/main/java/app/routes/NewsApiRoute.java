package app.routes;

import app.controllers.NewsApiController;
import app.enums.UserRole;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class NewsApiRoute {

    private final NewsApiController newsController;

    public NewsApiRoute(NewsApiController newsController) {
        this.newsController = newsController;
    }

    public EndpointGroup getRoutes() {
        return () -> {
            post("/fetch-news", newsController::fetchNews, UserRole.ADMIN);
        };
    }
}
