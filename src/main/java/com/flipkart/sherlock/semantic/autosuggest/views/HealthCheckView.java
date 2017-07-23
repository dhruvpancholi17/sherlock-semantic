package com.flipkart.sherlock.semantic.autosuggest.views;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by dhruv.pancholi on 30/05/17.
 */
@Slf4j
@Path("/flash")
@Singleton
public class HealthCheckView {

    private boolean oor = true;
    private static final String FLASH_TOKEN_KEY = "FLASH_TOKEN_KEY";
    private static final String OOR = "oor";
    private static final String BIR = "bir";


    @GET
    @Path("rotation-status")
    public Response getStatus(@Context UriInfo uriInfo) {
        synchronized (HealthCheckView.class) {
            if (oor) {
                return Response.status(Response.Status.MOVED_PERMANENTLY).build();
            } else {
                return Response.status(Response.Status.OK).build();
            }
        }
    }

    @GET
    @Path("rotation")
    public Response flipRotation(@QueryParam("command") String command,
                                 @QueryParam("token") String token,
                                 @Context UriInfo uriInfo) {

        String host = uriInfo.getBaseUri().getHost();
        if (!(host.equals("localhost") || host.equals("127.0.0.1"))) {
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }

        if (OOR.equalsIgnoreCase(command)) {
            synchronized (HealthCheckView.class) {
                oor = true;
            }
        } else if (BIR.equalsIgnoreCase(command)) {
            synchronized (HealthCheckView.class) {
                oor = false;
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("elb-healthcheck")
    public Response getElbHealthCheckStatus(@Context UriInfo uriInfo) {
        if (oor) {
            return Response.status(Response.Status.MOVED_PERMANENTLY).entity("Box is out of rotation!").build();
        } else {
            return Response.status(Response.Status.OK).entity("Box is in rotation!").build();
        }
    }
}
