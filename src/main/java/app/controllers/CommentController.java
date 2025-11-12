package app.controllers;

import app.dtos.CommentDTO;
import app.services.CommentService;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;

public class CommentController implements IController<CommentDTO> {

    private final CommentService service;

    // Constructor injection: service is provided
    public CommentController(CommentService service) {
        this.service = service;
    }

    @Override
    public void getAll(Context ctx) {
        ctx.json(service.getAll(), CommentDTO.class);
    }

    @Override
    public void getById(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        ctx.json(service.getById(id), CommentDTO.class);
    }

    @Override
    public void create(Context ctx) {
        UserDTO user = ctx.attribute("user");
        CommentDTO input = ctx.bodyAsClass(CommentDTO.class);
        ctx.status(201).json(service.create(input, user), CommentDTO.class);
    }

    @Override
    public void update(Context ctx) {
        UserDTO user = ctx.attribute("user");
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        CommentDTO input = ctx.bodyAsClass(CommentDTO.class);
        ctx.json(service.update(id, input, user), CommentDTO.class);
    }

    @Override
    public void delete(Context ctx) {
        UserDTO user = ctx.attribute("user");
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        service.delete(id, user);
        ctx.status(204);
    }
}
