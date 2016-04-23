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
    public static final String ACTION_LOAD_FILE = "loadFile";

    public static final String ACTION_MODIFY = "modify";
    public static final String SUB_ACTION_SAVE_DATA = "saveData";
    public static final String SUB_ACTION_SAVE_FILES = "saveFiles";
    public static final String SUB_ACTION_DELETE_FILES = "deleteFiles";

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
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        JsonReply.reply(resp, new JsonReply() {
            @Override
            public void fillJson(Map<String, Object> result) throws IOException {
                String action = req.getParameter(ACTION);
                Map<?, ?> json = Logic.gson.fromJson(new InputStreamReader(req.getInputStream()), Map.class);

                if (!ACTION_MODIFY.equals(action)) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    log.severe("404: action " + action);
                    return;
                }

                for (Map.Entry entry : json.entrySet()) {
                    String subAction = (String) entry.getKey();
                    Object subActionData = entry.getValue();

                    if (SUB_ACTION_SAVE_DATA.equals(subAction)) {
                        Date lastUpdated = AppLogic.storeEncryptedPassData((String) subActionData);
                        result.put("lastUpdated", lastUpdated);

                    } else if (SUB_ACTION_SAVE_FILES.equals(subAction)) {
                        List<Map> files = (List<Map>) subActionData;
                        Date uploaded = null;
                        for (Map file : files) {
                            String key = (String) file.get(PARAM_KEY);
                            String data = (String) file.get(PARAM_DATA);
                            uploaded = AppLogic.saveFile(key, data);
                        }
                        result.put("uploaded", uploaded);// TODO

                    } else if (SUB_ACTION_DELETE_FILES.equals(subAction)) {
                        List<String> keys = (List<String>) subActionData;
                        for (String key : keys) {
                            AppLogic.deleteFile(key);
                        }
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        log.severe("404: subAction " + subAction);
                    }
                }
            }
        });
    }
}
