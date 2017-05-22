package cw.cmm529.controllers;

import cw.cmm529.entities.SiteUser;
import cw.cmm529.util.Controllers;
import cw.cmm529.util.Dynamo;
import cw.cmm529.util.Formatters;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.function.Function;

import static javax.ws.rs.core.Response.ResponseBuilder;
import static javax.ws.rs.core.Response.Status;

/**
 * HTTP endpoints tied directly to user data
 *
 * @author a.molcanovas@gmail.com
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserEndpoint {

    /**
     * Mapping function for transforming a location to a JSON response, returning a JSON NULL if the user never checked
     * in
     */
    private final static Function<SiteUser, Object> userToLocationResponseEntity =
            u -> Optional.ofNullable(u.getLocation())
                    .map(Object.class::cast)
                    .orElse("\nnull\"");

    /**
     * Maps a location response to a {@link Response} object
     */
    private final static Function<Object, ResponseBuilder> locationResponseEntityToResponseBuilder =
            e -> Response.ok().entity(e);

    /**
     * Gets the current user's site information
     *
     * @param id The user ID
     * @return
     */
    @Path("/")
    @GET
    public Response getUserInfo(@HeaderParam("x-user") final String id) {
        return Controllers.ifNull(Status.UNAUTHORIZED, id)
                .withExceptionMapper(Formatters::toErrorResponseBuilder)
                .elseTry(() -> SiteUser.load(id).map(u -> Response.ok().entity(u)))
                .orElse(Response.status(Status.NOT_FOUND).entity("\"User not found\""))
                .build();
    }

    /**
     * Gets the current user's location
     *
     * @param id The user's ID
     * @return
     */
    @Path("/location")
    @GET
    public Response getMyLocation(@HeaderParam("x-user") final String id) {
        return Controllers.ifNull(Status.UNAUTHORIZED, id)
                .map(Response.ResponseBuilder::build)
                .orElseGet(() -> getLocation(id));
    }

    /**
     * Gets the given user's location
     *
     * @param id The user's ID
     * @return
     */
    @Path("/location/{userID}")
    @GET
    public Response getLocation(@PathParam("userID") final String id) {
        try {
            return SiteUser.load(id)
                    .map(userToLocationResponseEntity)
                    .map(locationResponseEntityToResponseBuilder)
                    .orElse(Response.status(Status.NOT_FOUND).entity("\"User not found\""))
                    .build();
        } catch (final Exception e) {
            return Formatters.toErrorResponse(e);
        }
    }

    /**
     * Check in at a location
     *
     * @param user      The user checking in
     * @param latitude  Check-in latitude
     * @param longitude Check-in longitude
     * @return
     */
    @PUT
    @Path("/check-in")
    public Response checkIn(
            @HeaderParam("x-user") final String user,
            @QueryParam("lat") final String latitude,
            @QueryParam("long") final String longitude
    ) {
        return Controllers.ifNull(Status.UNAUTHORIZED, user)
                .elseTry(() -> Controllers.ifTrue(null == latitude || null == longitude, Status.NOT_FOUND))
                .elseTry(()->Controllers.ifFalse(SiteUser.exists(user), Status.NOT_FOUND,"\"User ID not found\""))
                .withExceptionMapper(Formatters::toErrorResponseBuilder)
                .orElseGet(() -> {
                    final SiteUser u = new SiteUser();

                    try {
                        u.setCoordinates(latitude, longitude);
                    } catch (final NumberFormatException e) {
                        return Response.status(Status.BAD_REQUEST);
                    }

                    u.setId(user);
                    u.setLastUpdated(System.currentTimeMillis());
                    Dynamo.newMapper().save(u);

                    return Response.ok().entity(u.getLocation());
                })
                .build();
    }

    /**
     * Check if a given user exists
     *
     * @param id The user ID
     * @return
     */
    @Path("/exists/{id}")
    @GET
    public Response exists(@PathParam("id") final String id) {
        try {
            return Response.ok()
                    .entity(SiteUser.exists(id))
                    .build();
        } catch (final Exception e) {
            return Formatters.toErrorResponse(e);
        }
    }

    /**
     * Create a user
     *
     * @param id The user ID
     * @return
     */
    @Path("/{id}")
    @POST
    public Response create(@PathParam("id") final String id) {
        try {
            final SiteUser u = new SiteUser();
            u.setId(id);

            if (u.exists()) {
                return Response.status(Status.CONFLICT).build();
            } else {
                u.setLastUpdated(System.currentTimeMillis());
                Dynamo.newMapper().save(u);
                return Response.status(Status.CREATED).build();
            }
        } catch (final Exception e) {
            return Formatters.toErrorResponse(e);
        }
    }
}
