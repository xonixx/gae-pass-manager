package info.xonix.passmanager;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class JspLogic {
  private final Gson gson;
  private final AppLogic appLogic;
  private final UserService userService;

  @Getter @Setter private static JspLogic instance;

  public User getCurrentUser() {
    return userService.getCurrentUser();
  }

  public String getLogoutUrl() {
    return userService.createLogoutURL("/");
  }

  public String renderGlobals(boolean offline) {
    Map<String, Object> globals = new LinkedHashMap<>();

    User currentUser = getCurrentUser();
    globals.put("email", currentUser == null ? null : currentUser.getEmail());
    globals.put("version", VersionManager.getApplicationVersion());
    globals.put("jvm", System.getProperty("java.version"));
    globals.put("jvmName", System.getProperty("java.vm.name"));
    globals.put("gae", System.getProperty("com.google.appengine.runtime.version"));

    if (offline) {
      globals.put("offlineData", appLogic.getEncryptedPassData());
    }

    return gson.toJson(globals);
  }

  public String renderJsCss(PageContext pageContext, String jsCssBlockName, boolean offline) {

    String jsCssBlockContent = (String) pageContext.getAttribute(jsCssBlockName);
    ServletContext servletContext = pageContext.getServletContext();

    String[] lines = jsCssBlockContent.split("\n");
    StringBuilder res = new StringBuilder();

    for (String line : lines) {
      line = line.trim();
      if ("".equals(line)) continue;
      if (line.endsWith(".js")) {
        if (offline) {
          res.append("<script>/* src:")
              .append(line)
              .append(" */\n")
              .append(getStringContent(servletContext, line, true))
              .append("\n</script>\n");
        } else res.append("<script src=\"").append(line).append("\"></script>\n");
      } else if (line.endsWith(".css")) {
        if (offline) {
          res.append("<style>/* src:")
              .append(line)
              .append(" */\n")
              .append(getStringContent(servletContext, line, false))
              .append("\n</style>\n");
        } else {
          res.append("<link rel=\"stylesheet\" href=\"").append(line).append("\"/>\n");
        }
      } else {
        res.append("<script>alert(\"Can't determine JS/CSS type: ")
                .append(line)
                .append("\")</script>\n");
      }
    }

    return res.toString();
  }

  /** TODO: use data URI? */
  private static String getStringContent(ServletContext servletContext, String path, boolean isJs) {
    if (!path.startsWith("/")) {
      path = "/" + path;
    }

    String str = pathToString(servletContext, path);
    if (isJs) {
      str = str.replaceAll("</script>|</style>", "");
    } else { // css
      if (path.contains("bootstrap.")) {
        str =
            str.replace(
                "url('../", "url('https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.6/");
      }
    }
    return str;
  }

  private static String pathToString(ServletContext servletContext, String path) {
    try (InputStream inputStream = servletContext.getResourceAsStream(path)) {
      return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String renderNgTemplates(PageContext pageContext, boolean offline) {
    if (!offline) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    ServletContext servletContext = pageContext.getServletContext();
    Set<String> files = servletContext.getResourcePaths("/ng-tpl/");
    for (String fileName : files) {
      if (fileName.endsWith(".html")) {
        sb.append("<script type=\"text/ng-template\" id=\"")
            .append(fileName)
            .append("\">\n")
            .append(pathToString(servletContext, fileName))
            .append("\n</script>\n");
      }
    }
    return sb.toString();
  }
}
