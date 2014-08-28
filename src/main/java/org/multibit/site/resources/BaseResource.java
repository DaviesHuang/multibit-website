package org.multibit.site.resources;

import com.sun.jersey.api.core.HttpContext;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Locale;

/**
 * <p>Abstract base class to provide the following to subclasses:</p>
 * <ul>
 * <li>Provision of common methods</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public abstract class BaseResource {

  protected static final String DEFAULT_LANGUAGE = "en";

  public static final String COOKIE_NAME = "MBHD-Session";

  /**
   * Jersey creates a fresh resource every request so this is safe
   */
  @Context
  protected UriInfo uriInfo;

  /**
   * Jersey creates a fresh resource every request so this is safe
   */
  @Context
  protected HttpHeaders httpHeaders;

  /**
   * Jersey creates a fresh resource every request so this is safe
   */
  @Context
  protected HttpContext httpContext;

  /**
   * @return The most appropriate locale for the upstream request (never null)
   */
  public Locale getLocale() {
    // TODO This should be a configuration setting
    Locale defaultLocale = Locale.UK;

    Locale locale;
    if (httpHeaders == null) {
      locale = defaultLocale;
    } else {
      locale = httpHeaders.getLanguage();
      if (locale == null) {
        locale = defaultLocale;
      }
    }
    return locale;
  }

  public WebApplicationException badRequest() {
    return new WebApplicationException(Response.Status.BAD_REQUEST);
  }

  public WebApplicationException notFound() {
    return new WebApplicationException(Response.Status.NOT_FOUND);
  }

  /**
   * @return True if the session cookie exists indicating acceptance of the terms and conditions
   */
  protected boolean acceptedTandC() {

    boolean accepted = httpHeaders != null && httpHeaders.getCookies().containsKey(COOKIE_NAME);

    System.out.println("Accepted:" + accepted);
    return accepted;
  }
}
