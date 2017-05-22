package cw.cmm529.util;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.ResponseBuilder;

/**
 * Formatting utilities
 *
 * @author a.molcanovas@gmail.com
 */
public class Formatters {

    /**
     * Converts the given Error to a nearly-built {@link Response}.
     *
     * @param error The Throwable that caused the error
     * @return A ResponseBuilder with the HTTP status {@link javax.ws.rs.core.Response.Status#INTERNAL_SERVER_ERROR 500}.
     * If the provided Throwable is not null then the response will have the media type
     * {@link MediaType#TEXT_PLAIN text/plain} and the throwable's {@link Throwable#getMessage() message}.
     */
    public static ResponseBuilder toErrorResponseBuilder(final Throwable error) {
        final ResponseBuilder b = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        if (null != error) {
            b.entity(error.getMessage()).type(MediaType.TEXT_PLAIN_TYPE);
        }
        return b;
    }

    /**
     * Same as {@link #toErrorResponseBuilder(Throwable)}, but also builds the response.
     *
     * @param error The Throwable that caused the error
     * @return The built response
     */
    public static Response toErrorResponse(final Throwable error) {
        return toErrorResponseBuilder(error).build();
    }
}
