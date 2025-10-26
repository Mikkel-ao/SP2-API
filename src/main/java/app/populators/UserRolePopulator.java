package app.populators;

import app.daos.RoleDAO;
import app.daos.UserDAO;
import app.entities.Role;
import app.entities.User;
import jakarta.persistence.EntityManagerFactory;

public class UserRolePopulator {
    private final RoleDAO roleDAO;
    private final UserDAO userDAO;

    public UserRolePopulator(EntityManagerFactory emf) {
        this.roleDAO = new RoleDAO(emf);
        this.userDAO = new UserDAO(emf);
    }

    public void populate() {
        Role adminRole = new Role("ADMIN");
        Role userRole = new Role("USER");

        roleDAO.create(adminRole);
        roleDAO.create(userRole);

        User admin = new User("admin", "adminpassword");
        admin.addRole(adminRole);
        userDAO.create(admin);

        User user = new User("user", "userpassword");
        user.addRole(userRole);
        userDAO.create(user);
    }
}
