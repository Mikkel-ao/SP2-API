package app.services;

import app.daos.PostDAO;
import app.daos.UserDAO;
import app.entities.Post;
import app.entities.User;
import app.dtos.NewsArticleDTO;
import app.dtos.NewsResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class NewsApiService {

    private static final String BASE_URL = "https://newsapi.org/v2/everything";
    private static final String QUERY = "cybersecurity OR programming";

    private final ObjectMapper objectMapper;
    private final HttpClient client;
    private final PostDAO postDAO;
    private final UserDAO userDAO;
    private final String apiKey;

    public NewsApiService(PostDAO postDAO, UserDAO userDAO) {
        this.postDAO = postDAO;
        this.userDAO = userDAO;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.client = HttpClient.newHttpClient();
        this.apiKey = System.getenv("NEWS_API_KEY");
        if (apiKey == null) {
            throw new RuntimeException("NEWS_API_KEY not found in environment variables!");
        }
    }

    public List<Post> fetchAndSaveNews() {
        List<Post> savedPosts = new ArrayList<>();
        try {
            String url = String.format("%s?q=%s&sortBy=popularity&language=en&pageSize=10&apiKey=%s",
                    BASE_URL, URLEncoder.encode(QUERY, StandardCharsets.UTF_8), apiKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("News API request failed. Status: " + response.statusCode());
                return savedPosts;
            }

            NewsResponseDTO newsResponse = objectMapper.readValue(response.body(), NewsResponseDTO.class);
            if (newsResponse.getArticles() == null || newsResponse.getArticles().isEmpty()) {
                System.out.println("No news articles found.");
                return savedPosts;
            }

            // TODO: Consider something more robust - If admin user from populator is removed error occurs.
            User admin = userDAO.findByUsername("admin");
            if (admin == null) throw new RuntimeException("Admin user not found");

            for (NewsArticleDTO article : newsResponse.getArticles()) {
                String content = article.getDescription() != null ? article.getDescription() : "";
                if (article.getTitle() == null || content.isBlank() || postDAO.existsByTitle(article.getTitle())) {
                    continue; // skip empty or duplicate titles/content
                }

                Post post = Post.builder()
                        .title(article.getTitle())
                        .content(content)
                        .sourceUrl(article.getUrl())
                        .sourceName(article.getSource() != null ? article.getSource().getName() : "Unknown")
                        .createdAt(Instant.now())
                        .author(admin)
                        .build();

                postDAO.save(post);
                savedPosts.add(post);
            }

        } catch (Exception e) {
            System.err.println("Error fetching or saving tech news: " + e.getMessage());
            e.printStackTrace();
        }

        return savedPosts;
    }
}
