package com.example.resource;

import com.example.entity.RainfallRecord;
import com.example.service.RainfallService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/api/rainfall")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RainfallResource {

    @Inject
    RainfallService rainfallService;

    @POST
    public Response receiveRainfallData(RainfallRecord record) {
        try {
            rainfallService.saveRainfallData(record);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "雨量データを受信しました: " + record.location);
            
            return Response.ok(response).build();
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    @GET
    public List<RainfallRecord> getAllRecords(
            @QueryParam("location") String location,
            @QueryParam("limit") @DefaultValue("100") int limit) {
        return rainfallService.getRecords(location, limit);
    }

    @GET
    @Path("/latest")
    public List<RainfallRecord> getLatestRecords() {
        return rainfallService.getLatestRecords();
    }

    @GET
    @Path("/alerts")
    public List<RainfallRecord> getAlerts() {
        return rainfallService.getAlerts();
    }
}
