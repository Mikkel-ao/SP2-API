package app.enums;

import io.javalin.security.RouteRole;

public enum UserRole implements RouteRole {
    ANYONE, USER, ADMIN
}
