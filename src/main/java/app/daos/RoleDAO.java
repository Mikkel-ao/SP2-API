package app.daos;

import app.entities.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class RoleDAO {

    private final EntityManagerFactory emf;

    public RoleDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // Create a new role
    public void create(Role role) {
        try (EntityManager em = emf.createEntityManager()) {
            if (find(role.getRoleName()) != null) return;
            em.getTransaction().begin();
            em.persist(role);
            em.getTransaction().commit();
        }
    }

    // Find a role by name
    public Role find(String roleName) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Role.class, roleName);
        }
    }

    // Delete a role by name
    public boolean delete(String roleName) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Role role = em.find(Role.class, roleName);
            if (role != null) {
                em.remove(role);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().commit();
            return false;
        }
    }

    // Delete all roles
    public int deleteAllRoles() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            int deletedCount = em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
            return deletedCount;
        }
    }

    // Get all roles
    public List<Role> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT r FROM Role r", Role.class).getResultList();
        }
    }
}
