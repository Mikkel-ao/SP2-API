package app.routes;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.utils.Utils;
import app.controllers.SecurityController;
import app.enums.UserRole;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * Purpose: To handle security in the API
 *  Author: Thomas Hartmann
 */
public class SecurityRoutes {
    private static ObjectMapper jsonMapper = new Utils().getObjectMapper();
    private static SecurityController securityController = SecurityController.getInstance();
    public static EndpointGroup getSecurityRoutes() {
        return ()->{
            path("/auth", ()->{
                get("/healthcheck", securityController::healthCheck, UserRole.ANYONE);
                get("/test", ctx->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello from Open Deployment")), UserRole.ANYONE);
                post("/login", securityController.login(), UserRole.ANYONE);
                post("/register", securityController.register(), UserRole.ANYONE);
                post("/user/addrole", securityController.addRole(), UserRole.USER);
            });
        };
    }
    public static EndpointGroup getSecuredRoutes(){
        return ()->{
            path("/protected", ()->{
                get("/user_demo", (ctx)->ctx.json(jsonMapper.createObjectNode().put("msg", "Hello from USER Protected")), UserRole.USER);
                get("/admin_demo", (ctx)->ctx.json(jsonMapper.createObjectNode().put("msg", "Hello from ADMIN Protected")), UserRole.ADMIN);
            });
        };
    }
}
