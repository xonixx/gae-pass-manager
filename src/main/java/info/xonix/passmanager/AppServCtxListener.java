package info.xonix.passmanager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Logger;

/**
 * User: xonix
 * Date: 06.03.16
 * Time: 17:36
 */
public class AppServCtxListener implements ServletContextListener {
    private static final Logger log = Logger.getLogger(AppServCtxListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Context started, DEV=" + Env.isDev());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
