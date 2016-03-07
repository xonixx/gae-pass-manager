package info.xonix.passmanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: xonix
 * Date: 21.02.16
 * Time: 19:47
 */
public class Logic {
    public static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create();

    // TODO: do we need to cache?
    public static UserService getUserService() {
        return UserServiceFactory.getUserService();
    }

    public static DatastoreService getDatastoreService() {
        return DatastoreServiceFactory.getDatastoreService();
    }

    public static User getCurrentUser() {
        return getUserService().getCurrentUser();
    }

    public static String getLogoutUrl() {
        return getUserService().createLogoutURL("/");
    }

    public static String renderGlobals() {
        Map<String, Object> globals = new LinkedHashMap<>();
        globals.put("email", getCurrentUser().getEmail());
        return gson.toJson(globals);
    }

    public static String renderJsCss(PageContext pageContext,
                                     String jsCssBlockName,
                                     boolean offline) {

        String jsCssBlockContent = (String) pageContext.getAttribute(jsCssBlockName);
        ServletContext servletContext = pageContext.getServletContext();

        String[] lines = jsCssBlockContent.split("\n");
        StringBuilder res = new StringBuilder();

        for (String line : lines) {
            line = line.trim();
            if ("".equals(line))
                continue;
            if (line.endsWith(".js")) {
                if (offline) {
                    res.append("<script>/* src:").append(line).append(" */\n")
                            .append(getStringContent(servletContext, line))
                            .append("\n</script>\n");
                } else
                    res.append("<script src=\"").append(line).append("\"></script>\n");
            } else if (line.endsWith(".css")) {
                if (offline) {
                    res.append("<style>/* src:").append(line).append(" */\n")
                            .append(getStringContent(servletContext, line))
                            .append("\n</style>\n");
                } else
                    res.append("<link rel=\"stylesheet\" href=\"").append(line).append("\"/>\n");
            } else
                res.append("<script>alert(\"Can't determine JS/CSS type: ").append(line).append("\")</script>\n");
        }

        return res.toString();
    }

    private static String getStringContent(ServletContext servletContext, String path) {
        if (!path.startsWith("/"))
            path = "/" + path;

        try (InputStream inputStream = servletContext.getResourceAsStream(path)) {
            String str = CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            str = str.replaceAll("</script>", "");
            str = str.replaceAll("</style>", "");
            return str;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
