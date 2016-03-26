package info.xonix.passmanager;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class EncLogic {
    private static final Logger log = Logger.getLogger(EncLogic.class.getName());

    public static final int BACKUPS_COUNT = 10;
    public static final String KEY_MASTER_DATA = "0";
    public static final String ENTITY_DATA = "Data";
    public static final String ENTITY_DATA_BACKUP = "DataBackup";
    public static final String PROP_VALUE = "value";
    public static final String PROP_TIMESTAMP = "timestamp";
    public static final String PROP_LAST_BACKUP_KEY = "lastBackupKey";

    public static String getEncyptedPassDataJson() {
        return Logic.gson.toJson(getEncyptedPassData());
    }

    public static Map<String, Object> getEncyptedPassData() {
        Map<String, Object> res = new LinkedHashMap<>();

        Entity data = null;
        try {
            data = Logic.getDatastoreService().get(KeyFactory.createKey(ENTITY_DATA, KEY_MASTER_DATA));
        } catch (EntityNotFoundException e) {
            log.info("Data not found");
        }

        if (data != null) {
            String value = ((Text) data.getProperty(PROP_VALUE)).getValue();
            Date timestamp = (Date) data.getProperty(PROP_TIMESTAMP);

            log.info("Found data of size: " + value.length() + ", lastUpdated: " + timestamp);

            res.put("data", value);
            res.put("lastUpdated", timestamp);
        } else {
            log.info("No data saved yet...");
        }
        return res;
    }

    /**
     * @param encData data to store
     * @return last updated date
     */
    public static Date storeEncryptedPassData(String encData) {
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
        data.setProperty(PROP_VALUE, new Text(encData));
        Date lastUpdated = new Date();
        data.setProperty(PROP_TIMESTAMP, lastUpdated);
        data.setProperty(PROP_LAST_BACKUP_KEY, newBackupKey);

        log.info("Storing data of size: " + encData.length());

        Logic.getDatastoreService().put(data);
        return lastUpdated;
    }
}
