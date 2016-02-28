package info.xonix.passmanager.servlets;

import com.google.appengine.api.datastore.*;
import info.xonix.passmanager.Logic;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * User: xonix
 * Date: 28.02.16
 * Time: 20:50
 */
public class ApiServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ApiServlet.class.getName());

    public static final String ACTION = "action";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);

        Entity data = null;
        try {
            data = Logic.getDatastoreService().get(KeyFactory.createKey("Data", "1"));
        } catch (EntityNotFoundException e) {
            log.info("Data not found");
        }

        resp.setContentType("application/json");
        if ("load".equals(action)) {
            Map<String, String> res = new LinkedHashMap<>();
            String value = data != null ? ((Text) data.getProperty("value")).getValue() : null;

            log.info("Found data: " + value);

            res.put("data", value);

            resp.getWriter().print(Logic.gson.toJson(res));
            resp.getWriter().flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);

        Map json = Logic.gson.fromJson(new InputStreamReader(req.getInputStream()), Map.class);

        resp.setContentType("application/json");

        if ("save".equals(action)) {
            Entity data = new Entity("Data", "1");
            String val = (String) json.get("data");
            data.setProperty("value", new Text(val));

            log.info("Storing data: " + val);

            Logic.getDatastoreService().put(data);

            resp.getWriter().print("{\"success\":true}");
            resp.getWriter().flush();
        }
    }
}
