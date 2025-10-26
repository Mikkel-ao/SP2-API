package app.controllers;

import app.entities.Post;
import app.services.NewsApiService;
import io.javalin.http.Context;

import java.io.IOException;
import java.util.List;

public class NewsApiController {

    private final NewsApiService newsService;

    public NewsApiController(NewsApiService newsService) {
        this.newsService = newsService;
    }

    public void fetchNews(Context ctx) throws IOException, InterruptedException {
        List<Post> posts = newsService.fetchAndSaveNews();
        ctx.json(posts);
    }
}
