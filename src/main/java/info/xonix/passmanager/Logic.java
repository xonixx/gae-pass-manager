package info.xonix.passmanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public static String renderJsCss(String jsCssBlockContent) {
        String[] lines = jsCssBlockContent.trim().split("\n");
        StringBuilder res = new StringBuilder();

        for (String line : lines) {
            line = line.trim();
            if (line.endsWith(".js"))
                res.append("<script src=\"").append(line).append("\"></script>\n");
            else if (line.endsWith(".css"))
                res.append("<link rel=\"stylesheet\" href=\"").append(line).append("\"/>\n");
            else
                res.append("<script>alert(\"Can't determine JS/CSS type: ").append(line).append("\")</script>\n");
        }

        return res.toString();
    }
}
