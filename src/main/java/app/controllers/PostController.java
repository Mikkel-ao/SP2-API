package app.controllers;

import app.dtos.PostDTO;
import app.services.PostService;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;

public class PostController implements IController<PostDTO> {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @Override
    public void getAll(Context ctx) {
        UserDTO user = ctx.attribute("user"); // may be null
        ctx.json(service.getAll(user), PostDTO.class);
    }

    @Override
    public void getById(Context ctx) {
        UserDTO user = ctx.attribute("user"); // may be null
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        ctx.json(service.getById(id, user), PostDTO.class);
    }

    @Override
    public void create(Context ctx) {
        UserDTO user = ctx.attribute("user");
        PostDTO input = ctx.bodyAsClass(PostDTO.class);
        ctx.status(201).json(service.create(input, user), PostDTO.class);
    }

    @Override
    public void update(Context ctx) {
        UserDTO user = ctx.attribute("user");
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        PostDTO input = ctx.bodyAsClass(PostDTO.class);
        ctx.json(service.update(id, input, user), PostDTO.class);
    }

    @Override
    public void delete(Context ctx) {
        UserDTO user = ctx.attribute("user");
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        service.delete(id, user);
        ctx.status(204);
    }
}
