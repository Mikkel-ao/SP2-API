package app.daos;

import app.entities.Vote;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class VoteDAO {
    private final EntityManagerFactory emf;

    public VoteDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void create(Vote vote) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(vote);
            em.getTransaction().commit();
        }
    }

    public Vote find(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Vote.class, id);
        }
    }

    public List<Vote> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT v FROM Vote v", Vote.class)
                    .getResultList();
        }
    }

    public void update(Vote vote) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(vote);
            em.getTransaction().commit();
        }
    }

    public boolean delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Vote vote = em.find(Vote.class, id);
            if (vote != null) {
                em.remove(vote);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        }
    }

    public List<Vote> findByPostId(Long postId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT v FROM Vote v WHERE v.post.id = :postId", Vote.class)
                    .setParameter("postId", postId)
                    .getResultList();
        }
    }

    public List<Vote> findByCommentId(Long commentId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT v FROM Vote v WHERE v.comment.id = :commentId", Vote.class)
                    .setParameter("commentId", commentId)
                    .getResultList();
        }
    }

}
