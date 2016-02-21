package info.xonix.passmanager;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * User: xonix
 * Date: 21.02.16
 * Time: 19:47
 */
public class Logic {
    public static User getCurrentUser() {
        UserService userService = UserServiceFactory.getUserService();
        return userService.getCurrentUser();
    }

    public static String getLogoutUrl() {
        UserService userService = UserServiceFactory.getUserService();
        return userService.createLogoutURL("/");
    }
}
