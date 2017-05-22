package cw.cmm529.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import cw.cmm529.entities.SiteSubscription;
import cw.cmm529.entities.SiteSubscriptionRequest;
import cw.cmm529.entities.SiteUser;
import cw.cmm529.http.PATCH;
import cw.cmm529.util.Controllers;
import cw.cmm529.util.Dynamo;
import cw.cmm529.util.Formatters;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status;

/**
 * Subscription-centered HTTP endpoints
 *
 * @author a.molcanovas@gmail.com
 */
@Path("/subscriptions")
@Produces(MediaType.TEXT_PLAIN)
public class Subscriptions {

    /**
     * Lists incoming subscription requests
     *
     * @param userID The user's ID
     * @return
     */
    @Path("/list-requests/incoming")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listIncomingRequests(@HeaderParam("x-user") final String userID) {
        return Controllers.ifNull(Status.UNAUTHORIZED, userID)
                .elseTry(() -> Controllers.ifFalse(
                        SiteUser.exists(userID),
                        Status.NOT_FOUND,
                        "\"User doesn't exist\"")
                )
                .map(rb -> rb.entity(ImmutableList.of()))
                .withExceptionMapper(Formatters::toErrorResponseBuilder)
                .orElseGet(() -> Response.ok().entity(SiteSubscriptionRequest.getPendingRequests(userID)))
                .build();
    }

    /**
     * Cancels a subscription request
     *
     * @param from Who sent the request
     * @param to   Who was meant to accept the request
     * @return
     */
    @Path("/request/{to}")
    @DELETE
    public Response cancelOutgoingRequest(@HeaderParam("x-user") final String from, @PathParam("to") final String to) {
        return Controllers.ifNull(Status.UNAUTHORIZED, to, "User ID not provided")
                .elseTry(() -> Controllers.validateSubscriptionRequest(to, from))
                .withExceptionMapper(Formatters::toErrorResponseBuilder)
                .elseTry(() -> Controllers.ifFalse(
                        SiteSubscriptionRequest.exists(from, to),
                        Status.NOT_FOUND,
                        "The subscription request doesn't exist")
                )
                .orElseGet(() -> {
                    SiteSubscriptionRequest.delete(from, to);

                    return Response.ok();
                })
                .build();
    }

    /**
     * Lists subscription requests sent by the user which haven't been accepted yet
     *
     * @param userID The user making the request
     * @return
     */
    @Path("/list-requests/outgoing")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listOutgoingRequests(@HeaderParam("x-user") final String userID) {
        return Controllers.ifNull(Status.UNAUTHORIZED, ImmutableList.of(), userID)
                .withExceptionMapper(Formatters::toErrorResponseBuilder)
                .orElseGet(() -> Response.ok().entity(SiteSubscriptionRequest.getOutgoingRequests(userID)))
                .build();
    }

    /**
     * Sends a subscription request
     *
     * @param target The recipient
     * @param from   The sending user
     * @return
     */
    @POST
    @Path("/subscribe/{to}")
    public Response subscribe(@PathParam("to") final String target, @HeaderParam("x-user") final String from) {
        return Controllers.ifNull(Status.UNAUTHORIZED, from)
                .withExceptionMapper(Formatters::toErrorResponseBuilder)
                .elseTry(() -> Controllers.validateSubscriptionRequest(target, from))
                .elseTry(() -> Controllers.ifTrue(
                        SiteSubscriptionRequest.exists(from, target),
                        Status.CONFLICT,
                        "The subscription request already exists")
                )
                .elseTry(() -> Controllers.ifTrue(
                        SiteSubscription.exists(from, target),
                        Status.CONFLICT,
                        "The subscription already exists")
                )
                .orElseGet(() -> {
                    final SiteSubscriptionRequest rq = new SiteSubscriptionRequest(from, target);
                    Dynamo.newMapper().save(rq);

                    return Response.status(Status.CREATED);
                })
                .build();
    }

    /**
     * Rejects a subscription request
     *
     * @param requester Who sent the request
     * @param responder Who's rejecting the request
     * @return
     */
    @DELETE
    @Path("/respond/{requesterID}")
    public Response denyRequest(
            @PathParam("requesterID") final String requester,
            @HeaderParam("x-user") final String responder
    ) {
        return Controllers.ifNull(Status.UNAUTHORIZED, responder)
                .withExceptionMapper(Formatters::toErrorResponseBuilder)
                .elseTry(() -> Controllers.validateSubscriptionRequest(responder, requester))
                .elseTry(() -> Controllers.ifFalse(
                        SiteSubscriptionRequest.exists(requester, responder),
                        Status.NOT_FOUND,
                        "The subscription request is not found"
                ))
                .orElseGet(() -> {
                    SiteSubscriptionRequest.delete(requester, requester);

                    return Response.ok();
                })
                .build();
    }

    /**
     * Lists the current user's subscriptions
     *
     * @param userID The user ID
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list-subscriptions")
    public Response listSubscriptions(@HeaderParam("x-user") final String userID) {
        return Controllers.ifNull(Status.UNAUTHORIZED, userID)
                .withExceptionMapper(Formatters::toErrorResponseBuilder)
                .orElseGet(() -> Response.ok().entity(
                        SiteSubscription.getForSubscriberID(userID)
                                .map(SiteSubscription::retrieveSubscribeToAsEntities)
                                .orElse(ImmutableSet.of())
                        )
                ).build();
    }

    /**
     * Accepts a subscription request
     *
     * @param requester Who sent the request
     * @param responder Who's accepting the request
     * @return
     */
    @PATCH
    @Path("/respond/{requesterID}")
    public Response acceptRequest(
            @PathParam("requesterID") final String requester,
            @HeaderParam("x-user") final String responder
    ) {
        return Controllers.ifNull(Status.UNAUTHORIZED, responder)
                .withExceptionMapper(Formatters::toErrorResponseBuilder)
                .elseTry(() -> Controllers.validateSubscriptionRequest(responder, requester))
                .elseTry(() -> Controllers.ifFalse(
                        SiteSubscriptionRequest.exists(requester, responder),
                        Status.NOT_FOUND,
                        "The subscription request is not found"
                ))
                .orElseGet(() -> {
                    try {
                        SiteSubscription.create(requester, responder);
                        return Response.ok();
                    } catch (final Exception e) {
                        return Formatters.toErrorResponseBuilder(e);
                    }
                })
                .build();
    }
}
