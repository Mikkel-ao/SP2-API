package app.configs;

import app.entities.*;
import app.utils.Utils;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateConfig {

    private static EntityManagerFactory emf;
    private static EntityManagerFactory emfTest;
    private static boolean isTest = false;

    public static void setTest(boolean test) {
        isTest = test;
    }

    public static boolean isTest() {
        return isTest;
    }

    // Normal application factory
    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = createEMF(false);
        }
        return emf;
    }

    // Test-specific factory (static and reused)
    public static synchronized EntityManagerFactory getEntityManagerFactoryForTest() {
        if (emfTest == null || !emfTest.isOpen()) {
            setTest(true);
            emfTest = createEMF(true);
        }
        return emfTest;
    }

    // Close factories only when explicitly requested
    public static synchronized void closeFactories() {
        if (emf != null && emf.isOpen()) emf.close();
        if (emfTest != null && emfTest.isOpen()) emfTest.close();
    }

    private static void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(Role.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Comment.class);
        configuration.addAnnotatedClass(Post.class);
        configuration.addAnnotatedClass(Vote.class);
    }

    private static EntityManagerFactory createEMF(boolean forTest) {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();

            System.out.println("Hibernate URL: " +
                    props.getProperty("hibernate.connection.url"));



            setBaseProperties(props);

            if (forTest) {
                setTestProperties(props);
            } else if (System.getenv("DEPLOYED") != null) {
                setDeployedProperties(props);
            } else {
                setDevProperties(props);
            }

            configuration.setProperties(props);
            registerEntities(configuration);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
            return sf.unwrap(EntityManagerFactory.class);



        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static void setBaseProperties(Properties props) {
        props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.put("hibernate.current_session_context_class", "thread");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "false");
        props.put("hibernate.use_sql_comments", "false");
    }

    private static void setDeployedProperties(Properties props) {
        String DBName = System.getenv("DB_NAME");
        props.setProperty("hibernate.connection.url", System.getenv("CONNECTION_STR") + DBName);
        props.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
        props.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));
        props.setProperty("hibernate.hbm2ddl.auto", "create");
    }

    private static void setDevProperties(Properties props) {
        String DBName = Utils.getPropertyValue("DB_NAME", "config.properties");
        String DB_USERNAME = Utils.getPropertyValue("DB_USERNAME", "config.properties");
        String DB_PASSWORD = Utils.getPropertyValue("DB_PASSWORD", "config.properties");
        props.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/" + DBName);
        props.put("hibernate.connection.username", DB_USERNAME);
        props.put("hibernate.connection.password", DB_PASSWORD);
        props.put("hibernate.hbm2ddl.auto", "update");
    }

    private static void setTestProperties(Properties props) {
        props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
        props.put("hibernate.connection.url", "jdbc:tc:postgresql:15.3-alpine3.18:///test_db");
        props.put("hibernate.connection.username", "postgres");
        props.put("hibernate.connection.password", "postgres");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "false");
    }
}
