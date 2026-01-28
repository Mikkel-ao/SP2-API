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

    public Vote findByUserAndPost(String username, Long postId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT v FROM Vote v WHERE v.user.username = :username AND v.post.id = :postId",
                            Vote.class
                    )
                    .setParameter("username", username)
                    .setParameter("postId", postId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        }
    }

    public Vote findByUserAndComment(String username, Long commentId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT v FROM Vote v WHERE v.user.username = :username AND v.comment.id = :commentId",
                            Vote.class
                    )
                    .setParameter("username", username)
                    .setParameter("commentId", commentId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        }
    }

    public int getPostScore(Long postId) {
        try (EntityManager em = emf.createEntityManager()) {
            Long score = em.createQuery(
                            "SELECT COALESCE(SUM(v.value), 0) FROM Vote v WHERE v.post.id = :id",
                            Long.class // <-- change from Integer.class
                    )
                    .setParameter("id", postId)
                    .getSingleResult();
            return score != null ? score.intValue() : 0; // convert to int
        }
    }

    public int getCommentScore(Long commentId) {
        try (EntityManager em = emf.createEntityManager()) {
            Long score = em.createQuery(
                            "SELECT COALESCE(SUM(v.value), 0) FROM Vote v WHERE v.comment.id = :id",
                            Long.class // <-- change from Integer.class
                    )
                    .setParameter("id", commentId)
                    .getSingleResult();
            return score != null ? score.intValue() : 0;
        }
    }





}
