package app.routes;

import app.configs.HibernateConfig;
import app.controllers.NewsApiController;
import app.daos.PostDAO;
import app.daos.UserDAO;
import app.enums.UserRole;
import app.services.NewsApiService;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class NewsApiRoute {

    private final NewsApiController newsController;

    public NewsApiRoute() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        PostDAO postDAO = new PostDAO(emf);
        UserDAO userDAO = new UserDAO(emf);
        NewsApiService newsService = new NewsApiService(postDAO, userDAO);
        this.newsController = new NewsApiController(newsService);
    }

    public EndpointGroup getRoutes() {
        return () -> {
            post("/fetch-news", newsController::fetchNews, UserRole.ADMIN);
        };
    }
}
