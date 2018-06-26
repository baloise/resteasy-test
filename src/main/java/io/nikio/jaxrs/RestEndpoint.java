package io.nikio.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.logging.Logger;

@Path("/resource")
public class RestEndpoint {

    private static final Logger logger = Logger.getLogger(RestEndpoint.class.getName());

    @GET
    @Path("/date")
    @Produces("application/json")
    public Response getDate() {
        return Response.status(Response.Status.OK).entity(LocalDate.now()).build();
    }

    @GET
    @Path("/pojo")
    @Produces("application/json")
    public Response getPojo() {
        return Response.status(Response.Status.OK).entity(new Pojo("Hello")).build();
    }

}

