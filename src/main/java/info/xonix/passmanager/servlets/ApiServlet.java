package info.xonix.passmanager.servlets;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
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

    public static final int BACKUPS_COUNT = 10;

    public static final String KEY_MASTER_DATA = "0";
    public static final String ENTITY_DATA = "Data";
    public static final String ENTITY_DATA_BACKUP = "DataBackup";

    public static final String ACTION = "action";
    public static final String ACTION_LOAD = "load";
    public static final String ACTION_SAVE = "save";

    public static final String PROP_VALUE = "value";
    public static final String PROP_TIMESTAMP = "timestamp";
    public static final String PROP_LAST_BACKUP_KEY = "lastBackupKey";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);

        resp.setContentType("application/json");

        if (ACTION_LOAD.equals(action)) {
            resp.getWriter().print(Logic.getEncyptedPassDataJson());
            resp.getWriter().flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);

        Map json = Logic.gson.fromJson(new InputStreamReader(req.getInputStream()), Map.class);

        resp.setContentType("application/json");

        if (ACTION_SAVE.equals(action)) {
            Entity currentData = null;
            try {
                currentData = Logic.getDatastoreService().get(KeyFactory.createKey(ENTITY_DATA, KEY_MASTER_DATA));
            } catch (EntityNotFoundException ignore) {
            }

            long newBackupKey = -1;
            if (currentData != null) {
                long lastBackupKey = -1; // no backup
                Long currnetBackupKey = (Long) currentData.getProperty(PROP_LAST_BACKUP_KEY);
                if (currnetBackupKey != null) {
                    lastBackupKey = currnetBackupKey;
                }
                newBackupKey = (lastBackupKey + 1) % BACKUPS_COUNT;

                log.info("Storing backup: #" + newBackupKey);

                Entity backupData = new Entity(ENTITY_DATA_BACKUP, "" + newBackupKey);
                backupData.setProperty(PROP_VALUE, currentData.getProperty(PROP_VALUE));
                backupData.setProperty(PROP_TIMESTAMP, new Date());
                Logic.getDatastoreService().put(backupData);
            }

            Entity data = new Entity(ENTITY_DATA, KEY_MASTER_DATA);
            String val = (String) json.get("data");
            data.setProperty(PROP_VALUE, new Text(val));
            Date lastUpdated = new Date();
            data.setProperty(PROP_TIMESTAMP, lastUpdated);
            data.setProperty(PROP_LAST_BACKUP_KEY, newBackupKey);

            log.info("Storing data of size: " + val.length());

            Logic.getDatastoreService().put(data);

            Map<String, Object> res = new LinkedHashMap<>();
            res.put("success", true);
            res.put("lastUpdated", lastUpdated);
            resp.getWriter().print(Logic.gson.toJson(res));
            resp.getWriter().flush();
        }
    }
}
