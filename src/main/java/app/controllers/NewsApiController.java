package app.controllers;

import app.entities.Post;
import app.services.NewsApiService;
import io.javalin.http.Context;

import java.util.List;

public class NewsApiController {

    private final NewsApiService newsService;

    public NewsApiController(NewsApiService newsService) {
        this.newsService = newsService;
    }

    // No try/catch here; exceptions bubble to global handler
    public void fetchNews(Context ctx) throws Exception {
        List<Post> posts = newsService.fetchAndSaveNews();
        ctx.json(posts);
    }
}
