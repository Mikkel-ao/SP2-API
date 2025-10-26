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

    public Comment find(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Comment.class, id);
        }
    }

    public List<Comment> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT c FROM Comment c", Comment.class)
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

    public boolean delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Comment comment = em.find(Comment.class, id);
            if (comment != null) {
                comment.markDeleted(); // soft delete
                em.merge(comment); // persist changes
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        }
    }

}
