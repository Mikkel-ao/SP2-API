package app.controllers;

import app.dtos.VoteDTO;
import app.services.VoteService;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;

public class VoteController implements IController<VoteDTO> {

    private final VoteService service;

    // Constructor injection
    public VoteController(VoteService service) {
        this.service = service;
    }

    @Override
    public void getAll(Context ctx) {
        ctx.json(service.getAll(), VoteDTO.class);
    }

    @Override
    public void getById(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        ctx.json(service.getById(id), VoteDTO.class);
    }

    @Override
    public void create(Context ctx) {
        UserDTO user = ctx.attribute("user");
        VoteDTO input = ctx.bodyAsClass(VoteDTO.class);
        ctx.status(201).json(service.create(input, user), VoteDTO.class);
    }

    @Override
    public void update(Context ctx) {
        // Votes typically aren't updated
        ctx.status(405).json("{\"msg\":\"Vote update not allowed\"}");
    }

    @Override
    public void delete(Context ctx) {
        UserDTO user = ctx.attribute("user");
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        service.delete(id, user);
        ctx.status(204);
    }
}
