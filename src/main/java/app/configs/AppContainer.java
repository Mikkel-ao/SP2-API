package app.configs;

import app.controllers.*;
import app.daos.*;
import app.services.*;
import jakarta.persistence.EntityManagerFactory;

public class AppContainer {

    // --- Persistence Layer ---
    public final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    public final UserDAO userDAO = new UserDAO(emf);
    public final PostDAO postDAO = new PostDAO(emf);
    public final CommentDAO commentDAO = new CommentDAO(emf);
    public final VoteDAO voteDAO = new VoteDAO(emf);

    // --- Service Layer ---
    public final PostService postService = new PostService(postDAO, userDAO);
    public final CommentService commentService = new CommentService(commentDAO, postDAO, userDAO);
    public final VoteService voteService = new VoteService(voteDAO, postDAO, commentDAO, userDAO);
    public final NewsApiService newsApiService = new NewsApiService(postDAO, userDAO);

    // --- Controller Layer ---
    public final PostController postController = new PostController(postService);
    public final CommentController commentController = new CommentController(commentService);
    public final VoteController voteController = new VoteController(voteService);
    public final NewsApiController newsApiController = new NewsApiController(newsApiService);

}
