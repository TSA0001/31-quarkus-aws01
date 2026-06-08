package com.example.resource;

import com.example.entity.RiverLevel;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/river")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RiverResource {

    @GET
    public List<RiverLevel> getAllRiverLevels() {
        return RiverLevel.listAll();
    }

    @GET
    @Path("/{id}")
    public RiverLevel getRiverLevel(@PathParam("id") Long id) {
        RiverLevel river = RiverLevel.findById(id);
        if (river == null) {
            throw new WebApplicationException("River level not found", 404);
        }
        return river;
    }

    @POST
    @Transactional
    public Response createRiverLevel(RiverLevel riverLevel) {
        riverLevel.persist();
        return Response.status(Response.Status.CREATED)
            .entity(riverLevel)
            .build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteRiverLevel(@PathParam("id") Long id) {
        RiverLevel river = RiverLevel.findById(id);
        if (river == null) {
            throw new WebApplicationException("River level not found", 404);
        }
        river.delete();
        return Response.noContent().build();
    }
}
