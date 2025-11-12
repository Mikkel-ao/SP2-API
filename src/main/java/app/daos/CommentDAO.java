package app.daos;

import app.entities.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class CommentDAO {
    private final EntityManagerFactory emf;

    public CommentDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void create(Comment comment) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(comment);
            em.getTransaction().commit();
        }
    }

    /** Fetch comment with author, post, parent, and replies (and their authors) */
    public Comment find(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Comment> comments = em.createQuery("""
                    SELECT DISTINCT c FROM Comment c
                    LEFT JOIN FETCH c.author
                    LEFT JOIN FETCH c.post
                    LEFT JOIN FETCH c.parent
                    LEFT JOIN FETCH c.replies r
                    LEFT JOIN FETCH r.author
                    WHERE c.id = :id
                    """, Comment.class)
                    .setParameter("id", id)
                    .getResultList();

            return comments.isEmpty() ? null : comments.get(0);
        }
    }

    /** Fetch all comments with joined relations to avoid lazy loading */
    public List<Comment> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("""
                    SELECT DISTINCT c FROM Comment c
                    LEFT JOIN FETCH c.author
                    LEFT JOIN FETCH c.post
                    LEFT JOIN FETCH c.parent
                    LEFT JOIN FETCH c.replies r
                    LEFT JOIN FETCH r.author
                    ORDER BY c.createdAt DESC
                    """, Comment.class)
                    .getResultList();
        }
    }

    public void update(Comment comment) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(comment);
            em.getTransaction().commit();
        }
    }

    /** Soft delete comment */
    public boolean delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Comment comment = em.find(Comment.class, id);
            if (comment != null) {
                comment.markDeleted(); // soft delete
                em.merge(comment);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        }
    }
}
