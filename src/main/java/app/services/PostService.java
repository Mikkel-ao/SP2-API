package app.services;

import app.daos.PostDAO;
import app.daos.UserDAO;
import app.dtos.PostDTO;
import app.entities.Comment;
import app.entities.Post;
import app.entities.User;
import app.mappers.PostMapper;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;

import java.time.Instant;
import java.util.List;

public class PostService {

    private final PostDAO postDAO;
    private final UserDAO userDAO;

    public PostService(PostDAO postDAO, UserDAO userDAO) {
        this.postDAO = postDAO;
        this.userDAO = userDAO;
    }

    /** Recursively initializes all comments and their nested replies */
    private void initializeComments(Post post) {
        if (post == null || post.getComments() == null) return;
        post.getComments().forEach(this::initializeCommentRecursively);
    }

    private void initializeCommentRecursively(Comment comment) {
        if (comment == null) return;

        if (comment.getAuthor() != null) comment.getAuthor().getUsername();
        if (comment.getPost() != null) comment.getPost().getId();
        if (comment.getParent() != null) comment.getParent().getId();

        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            comment.getReplies().forEach(this::initializeCommentRecursively);
        }
    }

    /** Get all posts */
    public List<PostDTO> getAll() {
        List<Post> posts = postDAO.findAll();
        posts.forEach(this::initializeComments);
        return posts.stream()
                .map(PostMapper::toDTO)
                .toList();
    }

    /** Get post by ID */
    public PostDTO getById(Long id) {
        Post post = postDAO.find(id);
        if (post == null)
            throw new NotFoundResponse("Post not found");

        initializeComments(post);
        return PostMapper.toDTO(post);
    }

    /** Create new post */
    public PostDTO create(PostDTO input, UserDTO userDTO) {
        if (userDTO == null)
            throw new UnauthorizedResponse("Login required");

        if (input.getTitle() == null || input.getTitle().isBlank())
            throw new BadRequestResponse("Title cannot be empty");

        if (input.getContent() == null || input.getContent().isBlank())
            throw new BadRequestResponse("Content cannot be empty");

        User author = userDAO.findByUsername(userDTO.getUsername());
        if (author == null)
            throw new UnauthorizedResponse("User not found");

        Post post = Post.builder()
                .title(input.getTitle())
                .content(input.getContent())
                .sourceUrl(input.getSourceUrl())
                .sourceName(input.getSourceName())
                .createdAt(Instant.now())
                .author(author)
                .build();

        postDAO.create(post);
        initializeComments(post);

        return PostMapper.toDTO(post);
    }

    /** Update existing post */
    public PostDTO update(Long id, PostDTO input, UserDTO userDTO) {
        if (userDTO == null)
            throw new UnauthorizedResponse("Login required");

        Post existing = postDAO.find(id);
        if (existing == null)
            throw new NotFoundResponse("Post not found");

        boolean isAdmin = userDTO.getRoles().contains("ADMIN");
        boolean isOwner = existing.getAuthor().getUsername().equals(userDTO.getUsername());
        if (!isAdmin && !isOwner)
            throw new UnauthorizedResponse("Not allowed to update this post");

        existing.setTitle(input.getTitle());
        existing.setContent(input.getContent());
        existing.setSourceUrl(input.getSourceUrl());
        existing.setSourceName(input.getSourceName());

        postDAO.update(existing);
        initializeComments(existing);

        return PostMapper.toDTO(existing);
    }

    /** Delete post */
    public void delete(Long id, UserDTO userDTO) {
        if (userDTO == null)
            throw new UnauthorizedResponse("Login required");

        Post post = postDAO.find(id);
        if (post == null)
            throw new NotFoundResponse("Post not found");

        boolean isAdmin = userDTO.getRoles().contains("ADMIN");
        boolean isOwner = post.getAuthor().getUsername().equals(userDTO.getUsername());
        if (!isAdmin && !isOwner)
            throw new UnauthorizedResponse("Not allowed to delete this post");

        postDAO.delete(id);
    }
}
