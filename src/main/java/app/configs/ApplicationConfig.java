package app.configs;

import app.populators.DataPopulator;
import app.routes.SecurityRoutes;
import app.routes.Routes;
import app.securities.controllers.AccessController;
import app.securities.controllers.SecurityController;
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
        config.router.contextPath = "/api";
        config.router.apiBuilder(routes.getRoutes());
        config.router.apiBuilder(SecurityRoutes.getSecuredRoutes());
        config.router.apiBuilder(SecurityRoutes.getSecurityRoutes());
    }

    public static Javalin startServer(int port) {
        Javalin app = Javalin.create(ApplicationConfig::configuration);

        // CORS MUST run before any auth or route matching
        app.before(ApplicationConfig::corsHeaders);
        app.options("/*", ApplicationConfig::corsHeadersOptions);

        // Access control for secured routes
        app.beforeMatched(accessController::accessHandler);

        // Logging for every request
        app.after(ApplicationConfig::afterRequest);

        // Exception handling
        app.exception(Exception.class, ApplicationConfig::generalExceptionHandler);
        app.exception(ApiException.class, ApplicationConfig::apiExceptionHandler);

        // Populate initial data
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

    /**
     * CORS – development-friendly, credential-safe
     */
    private static void corsHeaders(Context ctx) {
        String origin = ctx.header("Origin");

        // Only allow requests from your React dev server
        if ("http://localhost:5173".equals(origin)) {
            ctx.header("Access-Control-Allow-Origin", origin);
            ctx.header("Access-Control-Allow-Credentials", "true");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        }
    }

    private static void corsHeadersOptions(Context ctx) {
        // Respond to preflight OPTIONS request
        corsHeaders(ctx);
        ctx.status(204);
    }
}
