package cw.cmm529.http;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Adds PATCH HTTP method support to HTTP controllers
 *
 * @author a.molcanovas@gmail.com
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@HttpMethod("PATCH")
@Documented
public @interface PATCH {

}
