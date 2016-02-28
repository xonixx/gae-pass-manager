package info.xonix.passmanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

/**
 * User: xonix
 * Date: 21.02.16
 * Time: 19:47
 */
public class Logic {
    public static final Gson gson = new Gson();

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
}
