package info.xonix.passmanager.servlets;

import info.xonix.passmanager.AppLogic;
import info.xonix.passmanager.JsonReply;
import info.xonix.passmanager.Logic;
import info.xonix.passmanager.Util;
import info.xonix.passmanager.model.Attachment;
import info.xonix.passmanager.model.PassData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: xonix
 * Date: 28.02.16
 * Time: 20:50
 */
public class ApiServlet extends HttpServlet {
    public static final String ACTION = "action";

    public static final String ACTION_LOAD = "load";
    public static final String ACTION_SAVE = "save";

    public static final String ACTION_LOAD_FILE = "loadFile";
    public static final String ACTION_SAVE_FILES = "saveFiles";
    public static final String ACTION_DELETE_FILE = "deleteFile";

    public static final String PARAM_KEY = "key";
    public static final String PARAM_DATA = "data";

    @Override
    protected void doGet(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReply.reply(resp, new JsonReply() {
            @Override
            public void fillJson(Map<String, Object> result) {
                String action = req.getParameter(ACTION);
                if (ACTION_LOAD.equals(action)) {
                    PassData passData = AppLogic.getEncyptedPassData();
                    if (passData != null)
                        result.putAll(Util.objectToMap(passData));
                } else if (ACTION_LOAD_FILE.equals(action)) {
                    String key = req.getParameter(PARAM_KEY);
                    Attachment att = AppLogic.loadFile(key);
                    if (att != null)
                        result.putAll(Util.objectToMap(att));
                }
            }
        });
    }

    @Override
    protected void doPost(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReply.reply(resp, new JsonReply() {
            @Override
            public void fillJson(Map<String, Object> result) throws IOException {
                String action = req.getParameter(ACTION);
                Map json = Logic.gson.fromJson(new InputStreamReader(req.getInputStream()), Map.class);

                if (ACTION_SAVE.equals(action)) {
                    Date lastUpdated = AppLogic.storeEncryptedPassData((String) json.get(PARAM_DATA));

                    result.put("lastUpdated", lastUpdated);
                } else if (ACTION_SAVE_FILES.equals(action)) {
                    List<Map> files = (List<Map>) json.get("files");
                    Date uploaded = null;
                    for (Map file : files) {
                        String key = (String) file.get(PARAM_KEY);
                        String data = (String) file.get(PARAM_DATA);
                        uploaded = AppLogic.saveFile(key, data);
                    }
                    result.put("uploaded", uploaded);
                } else if (ACTION_DELETE_FILE.equals(action)) {
                    String key = (String) json.get(PARAM_KEY);
                    AppLogic.deleteFile(key);
                }
            }
        });
    }
}
