package com.example;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api")
@RegisterRestClient(configKey = "jma-api")
public interface JmaApiClient {

    @GET
    @Path("/rainfall")
    JmaRainfallResponse getRainfall(@QueryParam("location") String location);
}
