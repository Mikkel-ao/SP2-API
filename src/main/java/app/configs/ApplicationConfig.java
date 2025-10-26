package app.configs;

import app.populators.DataPopulator;
import app.routes.SecurityRoutes;
import app.routes.Routes;
import app.controllers.AccessController;
import app.controllers.SecurityController;
import app.enums.UserRole;
import app.exceptions.ApiException;
import app.utils.Utils;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private static final Routes routes = new Routes();
    private static final SecurityController securityController = SecurityController.getInstance();
    private static final AccessController accessController = new AccessController();
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private static final DataPopulator dataPopulator = new DataPopulator(emf);

    private static int requestCount = 1;

    public static void configuration(JavalinConfig config) {
        config.showJavalinBanner = false;
        config.bundledPlugins.enableRouteOverview("/routes", UserRole.ANYONE);
        config.router.contextPath = "/api"; // base path for all endpoints
        config.router.apiBuilder(routes.getRoutes());
        config.router.apiBuilder(SecurityRoutes.getSecuredRoutes());
        config.router.apiBuilder(SecurityRoutes.getSecurityRoutes());
    }

    public static Javalin startServer(int port) {
        Javalin app = Javalin.create(ApplicationConfig::configuration);

        app.beforeMatched(accessController::accessHandler);
        app.after(ApplicationConfig::afterRequest);

        app.exception(Exception.class, ApplicationConfig::generalExceptionHandler);
        app.exception(ApiException.class, ApplicationConfig::apiExceptionHandler);

        // Populator(s) for filling the db
        logger.info("Populating initial data...");
        dataPopulator.populateAll();
        logger.info("Data population complete.");

        app.start(port);
        return app;
    }

    public static void afterRequest(Context ctx) {
        String requestInfo = ctx.req().getMethod() + " " + ctx.req().getRequestURI();
        logger.info("Request {} - {} handled with status {}", requestCount++, requestInfo, ctx.status());
    }

    public static void stopServer(Javalin app) {
        app.stop();
        if (emf.isOpen()) {
            emf.close();
        }
    }

    private static void generalExceptionHandler(Exception e, Context ctx) {
        logger.error("Unhandled exception", e);
        ctx.json(Utils.convertToJsonMessage(ctx, "error", e.getMessage()));
    }

    private static void apiExceptionHandler(ApiException e, Context ctx) {
        ctx.status(e.getCode());
        logger.warn("API exception - Code: {}, Message: {}", e.getCode(), e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "warning", e.getMessage()));
    }
}
