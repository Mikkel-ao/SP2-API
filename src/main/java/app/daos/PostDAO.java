package app.daos;

import app.entities.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class PostDAO {
    private final EntityManagerFactory emf;

    public PostDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void create(Post post) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(post);
            em.getTransaction().commit();
        }
    }

    // Merge instead of persist - Inserts if entity doesn't exist, updates if it does.
    public void save(Post post) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(post);
            em.getTransaction().commit();
        }
    }

    /** Fetch post with author and comments (and their authors) */
    public Post find(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Post> posts = em.createQuery("""
                    SELECT DISTINCT p FROM Post p
                    LEFT JOIN FETCH p.author
                    LEFT JOIN FETCH p.comments c
                    LEFT JOIN FETCH c.author
                    LEFT JOIN FETCH c.replies r
                    LEFT JOIN FETCH r.author
                    WHERE p.id = :id
                    """, Post.class)
                    .setParameter("id", id)
                    .getResultList();

            return posts.isEmpty() ? null : posts.get(0);
        }
    }

    /** Fetch all posts with author and comments (and their authors) */
    public List<Post> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("""
                    SELECT DISTINCT p FROM Post p
                    LEFT JOIN FETCH p.author
                    LEFT JOIN FETCH p.comments c
                    LEFT JOIN FETCH c.author
                    LEFT JOIN FETCH c.replies r
                    LEFT JOIN FETCH r.author
                    ORDER BY p.createdAt DESC
                    """, Post.class)
                    .getResultList();
        }
    }

    public void update(Post post) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(post);
            em.getTransaction().commit();
        }
    }

    public boolean delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Post post = em.find(Post.class, id);
            if (post != null) {
                em.remove(post);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        }
    }

    // Checks for duplicates before inserting.
    public boolean existsByTitle(String title) {
        try (EntityManager em = emf.createEntityManager()) {
            Long count = em.createQuery(
                            "SELECT COUNT(p) FROM Post p WHERE p.title = :title", Long.class)
                    .setParameter("title", title)
                    .getSingleResult();
            return count > 0;
        }
    }

    // Checks if a post already exists with the given source URL 
    public boolean existsBySourceUrl(String sourceUrl) {
        try (EntityManager em = emf.createEntityManager()) {
            Long count = em.createQuery(
                            "SELECT COUNT(p) FROM Post p WHERE p.sourceUrl = :sourceUrl", Long.class)
                    .setParameter("sourceUrl", sourceUrl)
                    .getSingleResult();
            return count > 0;
        }
    }

}
