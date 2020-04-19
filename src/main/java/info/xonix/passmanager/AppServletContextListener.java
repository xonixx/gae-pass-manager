package info.xonix.passmanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@Slf4j
public class AppServletContextListener implements ServletContextListener {
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ServletContext servletContext = sce.getServletContext();

    log.info(
        "Context started, DEV={}, Servlet API={}.{}",
        Env.isDev(),
        servletContext.getMajorVersion(),
        servletContext.getMinorVersion());

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    UserService userService = UserServiceFactory.getUserService();

    AppLogic appLogic = new AppLogic(datastoreService);
    JsonReply jsonReply = new JsonReply(gson, datastoreService);

    servletContext
        .addServlet("Api", new ApiServlet(gson, appLogic, jsonReply))
        .addMapping("/api/*");

    JspLogic jspLogic = new JspLogic(gson, appLogic, userService);
    JspLogic.setInstance(jspLogic);
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {}
}
