package info.xonix.passmanager.servlets;

import info.xonix.passmanager.EncLogic;
import info.xonix.passmanager.Logic;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
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
    public static final String ACTION_LOAD = "load";
    public static final String ACTION_SAVE = "save";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);

        resp.setContentType("application/json");

        if (ACTION_LOAD.equals(action)) {
            resp.getWriter().print(EncLogic.getEncyptedPassDataJson());
            resp.getWriter().flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);

        Map json = Logic.gson.fromJson(new InputStreamReader(req.getInputStream()), Map.class);

        resp.setContentType("application/json");

        if (ACTION_SAVE.equals(action)) {
            Date lastUpdated = EncLogic.storeEncryptedPassData((String) json.get("data"));

            Map<String, Object> res = new LinkedHashMap<>();
            res.put("success", true);
            res.put("lastUpdated", lastUpdated);

            resp.getWriter().print(Logic.gson.toJson(res));
            resp.getWriter().flush();
        }
    }

}
