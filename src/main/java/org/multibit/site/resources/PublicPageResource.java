package org.multibit.site.resources;

import com.google.common.base.Optional;
import com.yammer.dropwizard.jersey.caching.CacheControl;
import com.yammer.metrics.annotation.Timed;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.multibit.site.caches.InMemoryArtifactCache;
import org.multibit.site.core.cleaner.AdvertTagVisitor;
import org.multibit.site.model.BaseModel;
import org.multibit.site.views.PublicFreemarkerView;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * <p>Resource to provide the following to application:</p>
 * <ul>
 * <li>Provision of all static HTML pages</li>
 * </ul>
 *
 * @since 3.0.0
 *         
 */
@Path("/")
public class PublicPageResource extends BaseResource {

  /**
   * The advert server (e.g. KarmaAds)
   */
  private static final String ADVERT_SERVER_HOST = "https://karma-ads.com";
  /**
   * The failsafe HTML to ensure continued correct presentation
   */
  private static final String FAILSAFE = "http://localhost:8080/ka/failsafe.html";

  /**
   * The advert server
   */
  private static final URI advertServer = URI.create(
    ADVERT_SERVER_HOST +
      "/service/ad/1ArfNRuhBsMUTQsA2SVNXFDVtGUc5sAWMy");

  private final CleanerProperties cleanerProperties;

  public PublicPageResource() {

    // Configure a strict set of cleaner properties
    cleanerProperties = new CleanerProperties();
    cleanerProperties.setTranslateSpecialEntities(true);
    cleanerProperties.setTransResCharsToNCR(true);
    cleanerProperties.setOmitComments(true);
    cleanerProperties.setOmitXmlDeclaration(true);
    cleanerProperties.setOmitDeprecatedTags(true);
    cleanerProperties.setOmitUnknownTags(true);
    cleanerProperties.setAllowHtmlInsideAttributes(false);
  }

  /**
   * Provide the favicon
   *
   * @return A favicon image from the assets
   */
  @GET
  @Path("favicon.ico")
  @Timed
  @CacheControl(maxAge = 24, maxAgeUnit = TimeUnit.HOURS)
  public Response viewFavicon() {

    InputStream is = PublicPageResource.class.getResourceAsStream("/assets/images/favicon.ico");

    return Response
      .ok(is)
      .type("image/png")
      .build();
  }

  /**
   * @return The /robots.txt file
   */
  @GET
  @Path("robots.txt")
  @Timed
  @CacheControl(maxAge = 24, maxAgeUnit = TimeUnit.HOURS)
  @Produces(MediaType.TEXT_PLAIN)
  public Response viewRobots() {

    InputStream is = PublicPageResource.class.getResourceAsStream("/views/robots.txt");

    return Response.ok(is).build();
  }

  /**
   * @return The /sitemap.xml file
   */
  @GET
  @Path("sitemap.xml")
  @Timed
  @CacheControl(maxAge = 24, maxAgeUnit = TimeUnit.HOURS)
  public Response viewSitemap() throws IOException {

    // Pull this from the long-lived artifact cache
    Optional<String> siteMap = InMemoryArtifactCache.INSTANCE.getByResourcePath(InMemoryArtifactCache.SITE_MAP_KEY);
    if (!siteMap.isPresent()) {
      throw notFound();
    }

    return Response
      .ok(siteMap.get())
      .type(MediaType.TEXT_XML)
      .build();
  }

  /**
   * @return The advert HTML suitable for inclusion in an iframe
   */
  @GET
  @Path("/ad")
  @Timed
  @CacheControl(noCache = true)
  public Response viewAdvert() throws IOException {

    // Clean a fresh advert from the advertising network
    TagNode node = new HtmlCleaner(cleanerProperties).clean(advertServer.toURL());

    try {
      // Make some adjustments
      node.traverse(new AdvertTagVisitor(ADVERT_SERVER_HOST));
    } catch (WebApplicationException e) {
      // Problem detected so redirect to failsafe
      return Response
        .temporaryRedirect(URI.create(FAILSAFE))
        .type(MediaType.TEXT_HTML)
        .build();
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new PrettyXmlSerializer(cleanerProperties).writeToStream(node, out);

    return Response
      .ok(out.toByteArray())
      .type(MediaType.TEXT_HTML)
      .build();
  }

  /**
   * @return The default index page for the main site
   */
  @GET
  @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
  public PublicFreemarkerView<BaseModel> getDefaultHomePage() {

    BaseModel model = new BaseModel("/" + DEFAULT_LANGUAGE + "/index.html");
    return new PublicFreemarkerView<BaseModel>("content/home.ftl", model);

  }

  /**
   * @return The index page for the main site (requires a specific entry point)
   */
  @GET
  @Path("index.html")
  @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
  public PublicFreemarkerView<BaseModel> getHomePage() {

    return getDefaultHomePage();

  }

  /**
   * @param page The page name (or slug)
   *
   * @return The default language page for the main site
   */
  @GET
  @Path("{page}.html")
  @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
  public PublicFreemarkerView<BaseModel> getDefaultPage(
    @PathParam("page") String page
  ) {

    BaseModel model = new BaseModel("/" + DEFAULT_LANGUAGE + "/" + page + ".html");
    return new PublicFreemarkerView<BaseModel>("content/main.ftl", model);

  }

  /**
   * @param lang The two letter language code (ISO 639-1)
   *
   * @return The language specific index page for the main site
   */
  @GET
  @Path("{lang}")
  @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
  public PublicFreemarkerView<BaseModel> getLanguageSpecificDefaultHomePage(
    @Size(min = 3, max = 3) @PathParam("lang") String lang
  ) {

    BaseModel model = new BaseModel("/" + lang + "/index.html");
    return new PublicFreemarkerView<BaseModel>("content/home.ftl", model);

  }

  /**
   * @param lang The two letter language code (ISO 639-1)
   * @param page The page name (or slug)
   *
   * @return The language specific page for the main site
   */
  @GET
  @Path("{lang}/{page}.html")
  @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
  public PublicFreemarkerView<BaseModel> getLanguageSpecificPage(
    @Size(min = 3, max = 3) @PathParam("lang") String lang,
    @PathParam("page") String page
  ) {

    BaseModel model = new BaseModel("/" + lang + "/" + page + ".html");
    return new PublicFreemarkerView<BaseModel>("content/main.ftl", model);

  }

  /**
   * Provide the default language blog page
   *
   * @param year  A four digit year specifier
   * @param month A two digit month specifier (1-based)
   * @param day   A two digit day specifier (1-based)
   * @param page  The page name (or slug)
   *
   * @return The view (template + data) allowing the HTML to be rendered
   */
  @GET
  @Path("blog/{year}/{month}/{day}/{page}.html")
  @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
  public PublicFreemarkerView<BaseModel> getDefaultBlogPage(
    @Digits(integer = 2, fraction = 0) @PathParam("year") String year,
    @Digits(integer = 2, fraction = 0) @PathParam("month") String month,
    @Digits(integer = 2, fraction = 0) @PathParam("day") String day,
    @PathParam("page") String page
  ) {

    return getLanguageSpecificBlogPage(DEFAULT_LANGUAGE, year, month, day, page);

  }

  /**
   * Provide a language specific blog page
   *
   * @param lang  The two letter language code (ISO 639-1)
   * @param year  A four digit year specifier
   * @param month A two digit month specifier (1-based)
   * @param day   A two digit day specifier (1-based)
   * @param page  The page name (or slug)
   *
   * @return The view (template + data) allowing the HTML to be rendered
   */
  @GET
  @Path("/{lang}/blog/{year}/{month}/{day}/{page}.html")
  @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
  public PublicFreemarkerView<BaseModel> getLanguageSpecificBlogPage(
    @Size(min = 3, max = 3) @PathParam("lang") String lang,
    @Digits(integer = 2, fraction = 0) @PathParam("year") String year,
    @Digits(integer = 2, fraction = 0) @PathParam("month") String month,
    @Digits(integer = 2, fraction = 0) @PathParam("day") String day,
    @PathParam("page") String page
  ) {

    // Java6 uses StringBuilder to optimise this
    String resourcePath = "/" + lang + "/blog/" + year + "-" + month + "-" + day + "-" + page + ".html";

    BaseModel model = new BaseModel(resourcePath);
    return new PublicFreemarkerView<BaseModel>("content/blog.ftl", model);

  }

  /**
   * Provide the default language help page
   *
   * @return The view (template + data) allowing the HTML to be rendered
   */
  @GET
  @Path("help")
  @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
  public PublicFreemarkerView<BaseModel> getDefaultHelpPage() {

    // Java6 uses StringBuilder to optimise this
    String resourcePath = "/" + DEFAULT_LANGUAGE + "/help.html";

    // Use the main template since this is a starting point for a user
    BaseModel model = new BaseModel(resourcePath);
    return new PublicFreemarkerView<BaseModel>("content/main.ftl", model);

  }

  /**
   * Provide the language specific help page
   *
   * This is normally a list of all available versions in the required language
   *
   * @return The view (template + data) allowing the HTML to be rendered
   */
  @GET
  @Path("{lang}/help")
  @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
  public PublicFreemarkerView<BaseModel> getDefaultLanguageSpecificHelpPage(
    @Size(min = 3, max = 3) @PathParam("lang") String lang
  ) {

    // Java6 uses StringBuilder to optimise this
    String resourcePath = "/" + lang + "/help.html";

    BaseModel model = new BaseModel(resourcePath);
    return new PublicFreemarkerView<BaseModel>("content/help.ftl", model);

  }

  /**
   * Provide the default language and version specific help page
   *
   * This is normally a list of all help for the given version in the given language
   *
   * @param lang    The two letter language code (ISO 639-1)
   * @param version The version number (e.g. v0.4, v0.5.3 etc)
   *
   * @return The view (template + data) allowing the HTML to be rendered
   */
  @GET
  @Path("{lang}/help/{version}")
  @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
  public PublicFreemarkerView<BaseModel> getDefaultLanguageVersionSpecificHelpPage(
    @Size(min = 3, max = 3) @PathParam("lang") String lang,
    @PathParam("version") String version
  ) {

    // Java6 uses StringBuilder to optimise this
    String resourcePath = "/" + lang + "/help/" + version + "/help_contents.html";

    BaseModel model = new BaseModel(resourcePath);
    return new PublicFreemarkerView<BaseModel>("content/help.ftl", model);

  }

  /**
   * Provide a language and version specific help page
   *
   * @param lang      The two letter language code (ISO 639-1)
   * @param version   The version number (e.g. v0.4, v0.5.3 etc)
   * @param pathParam The path parameters leading to the required resource
   *
   * @return The view (template + data) allowing the HTML to be rendered
   */
  @GET
  @Path("{lang}/help/{version}/{pathParam: (?).*}")
  @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
  public PublicFreemarkerView<BaseModel> getLanguageVersionSpecificHelpPage(
    @Size(min = 3, max = 3) @PathParam("lang") String lang,
    @PathParam("version") String version,
    @PathParam("pathParam") String pathParam
  ) {

    // Java6 uses StringBuilder to optimise this
    String resourcePath = "/" + lang + "/help/" + version + "/" + pathParam;

    BaseModel model = new BaseModel(resourcePath);
    return new PublicFreemarkerView<BaseModel>("content/help.ftl", model);

  }

}
