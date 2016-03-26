package info.xonix.passmanager.servlets;

import info.xonix.passmanager.AppLogic;
import info.xonix.passmanager.Logic;
import info.xonix.passmanager.Util;
import info.xonix.passmanager.model.PassData;

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

    public static final String ACTION_LOAD_FILE = "loadFile";
    public static final String ACTION_SAVE_FILE = "saveFile";
    public static final String ACTION_DELETE_FILE = "deleteFile";

    public static final String PARAM_KEY = "key";
    public static final String PARAM_DATA = "data";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);

        resp.setContentType("application/json");

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);

        try {
            if (ACTION_LOAD.equals(action)) {
                PassData passData = AppLogic.getEncyptedPassData();
                if (passData != null)
                    res.putAll(Util.objectToMap(passData));
            } else if (ACTION_LOAD_FILE.equals(action)) {
                String key = req.getParameter(PARAM_KEY);
                String data = AppLogic.loadFile(key);
//                res.put("data", data)
            }
        } catch (Throwable t) {
            res.put("success", false);
            res.put("error", t.toString());
        }

        resp.getWriter().print(Logic.gson.toJson(res));
        resp.getWriter().flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);

        Map json = Logic.gson.fromJson(new InputStreamReader(req.getInputStream()), Map.class);

        resp.setContentType("application/json");

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        try {
            if (ACTION_SAVE.equals(action)) {
                Date lastUpdated = AppLogic.storeEncryptedPassData((String) json.get(PARAM_DATA));

                res.put("lastUpdated", lastUpdated);
            } else if (ACTION_SAVE_FILE.equals(action)) {
                String key = (String) json.get(PARAM_KEY);
                String data = (String) json.get(PARAM_DATA);
                Date uploaded = AppLogic.saveFile(key, data);

                res.put("uploaded", uploaded);
            } else if (ACTION_DELETE_FILE.equals(action)) {
                String key = (String) json.get(PARAM_KEY);
                AppLogic.deleteFile(key);
            }
        } catch (Throwable t) {
            res.put("success", false);
            res.put("error", t.toString());
        }

        resp.getWriter().print(Logic.gson.toJson(res));
        resp.getWriter().flush();
    }
}
