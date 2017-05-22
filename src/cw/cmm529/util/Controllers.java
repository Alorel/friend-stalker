package cw.cmm529.util;

import cw.cmm529.entities.SiteUser;

import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.Optional;

import static javax.ws.rs.core.Response.ResponseBuilder;
import static javax.ws.rs.core.Response.Status;

/**
 * Controller utilities
 *
 * @author a.molcanovas@gmail.com
 */
public class Controllers {

    /**
     * Check if the given parameter is not null
     *
     * @param errorStatus Status code for when the parameter is null
     * @param parameter   The parameter to check
     * @return An empty optional if the parameter is not null, otherwise an Optional with the give error status
     */
    public static ExtendedOptional<ResponseBuilder> ifNull(final Status errorStatus, final Object parameter) {
        if (null == parameter) {
            return ExtendedOptional.of(Response.status(errorStatus));
        }
        return ExtendedOptional.empty();
    }

    /**
     * Check if the given parameter is not null
     *
     * @param errorStatus Status code for when the parameter is null
     * @param message     Message for then the parameter is null
     * @param parameter   The parameter to check
     * @return An empty optional if the parameter is not null, otherwise an Optional with the give error status and
     * message
     */
    public static ExtendedOptional<ResponseBuilder> ifNull(final Status errorStatus, final Object message, final Object parameter) {
        return ifNull(errorStatus, parameter).map(rb -> rb.entity(message));
    }

    /**
     * Check if the expected boolean condition matches the actual value
     *
     * @param expected The expected value
     * @param actual   The actual value
     * @param status   Status code for when expected == actual
     * @param response HTTP response for then expected == actual
     * @return An empty Optional if actual != expected, else an Optional with the given status and response.
     */
    private static Optional<ResponseBuilder> ifEquals(boolean expected, Boolean actual, Status status, final Object response) {
        if (actual == null) {
            actual = false;
        }
        if (actual == expected) {
            final Optional<ResponseBuilder> out = Optional.of(Response.status(status));
            return null == response ? out : out.map(rb -> rb.entity(response));
        }
        return Optional.empty();
    }

    /**
     * Check if the boolean condition is equals true
     *
     * @param condition The condition
     * @param status    Status for when the condition is false
     * @return An empty Optional if condition is null or false, else an Optional with the given status
     */
    public static Optional<ResponseBuilder> ifTrue(Boolean condition, Status status) {
        return ifTrue(condition, status, null);
    }

    /**
     * Check if the boolean condition is equals true
     *
     * @param condition The condition
     * @param status    Status for when the condition is false
     * @param response  Response for when the condition is false
     * @return An empty Optional if condition is null or false, else an Optional with the given status and response
     */
    public static Optional<ResponseBuilder> ifTrue(final Boolean condition, final Status status, final Object response) {
        return ifEquals(true, condition, status, response);
    }

    /**
     * Check if the boolean condition is equals false
     *
     * @param condition The condition
     * @param status    Status for when the condition is true
     * @return An empty Optional if condition is true, else an Optional with the given status
     */
    public static Optional<ResponseBuilder> ifFalse(Boolean condition, Status status) {
        return ifFalse(condition, status, null);
    }

    /**
     * Check if the boolean condition is equals false
     *
     * @param condition The condition
     * @param status    Status for when the condition is true
     * @param response  Response for when the condition is true
     * @return An empty Optional if condition is true, else an Optional with the given status
     */
    public static Optional<ResponseBuilder> ifFalse(final Boolean condition, final Status status, final Object response) {
        return ifEquals(false, condition, status, response);
    }

    /**
     * Validate a subscription HTTP request
     *
     * @param target The "target" user ID, depends on context
     * @param from   The "source" user ID, depends on context
     * @return An empty optional if the validation passes, else an Optional containing a ResponseBuilder with an
     * error status and code.
     */
    public static Optional<ResponseBuilder> validateSubscriptionRequest(final String target, final String from) {
        try {
            String error = null;
            Status status = Status.NOT_FOUND;

            if (null == from) {
                error = "The requester parameter is required";
            } else if (null == target) {
                error = "The target parameter is required";
            } else if (Objects.equals(target, from)) {
                error = "You can't subscribe to yourself, silly goose...";
                status = Status.BAD_REQUEST;
            } else if (!SiteUser.exists(from)) {
                error = "The requesting user does not exist";
            } else if (!SiteUser.exists(target)) {
                error = "The target user does not exist";
            }

            return null == error ? Optional.empty() : Optional.of(Response.status(status).entity(error));
        } catch (final Exception e) {
            return Optional.of(Response.serverError().entity(e.getMessage()));
        }
    }
}
