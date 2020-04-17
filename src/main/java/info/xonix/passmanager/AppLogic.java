package info.xonix.passmanager;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import info.xonix.passmanager.model.Attachment;
import info.xonix.passmanager.model.PassData;

import java.util.Date;
import java.util.logging.Logger;

/** User: xonix Date: 26.03.16 Time: 21:45 */
public class AppLogic {
  private static final Logger log = Logger.getLogger(AppLogic.class.getName());

  public static final int BACKUPS_COUNT = 10;
  public static final String KEY_MASTER_DATA = "0";

  public static final String ENTITY_DATA = "Data";
  public static final String ENTITY_FILE = "File";
  public static final String ENTITY_DATA_BACKUP = "DataBackup";

  public static final String PROP_VALUE = "value";
  public static final String PROP_TIMESTAMP = "timestamp";
  public static final String PROP_LAST_BACKUP_KEY = "lastBackupKey";

  public static PassData getEncyptedPassData() {
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

      return new PassData(value, timestamp);
    } else {
      log.info("No data saved yet...");
    }
    return null;
  }

  /**
   * @param encData data to store
   * @return last updated date
   */
  public static Date storeEncryptedPassData(String encData) {
    Entity currentData = null;
    try {
      currentData =
          Logic.getDatastoreService().get(KeyFactory.createKey(ENTITY_DATA, KEY_MASTER_DATA));
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

  /**
   * @param key file id
   * @return file existed or not
   */
  public static boolean deleteFile(String key) {
    Logic.getDatastoreService().delete(KeyFactory.createKey(ENTITY_FILE, key));
    return true; // TODO seems there is not effective way to check if record existed
  }

  /**
   * @param key file id
   * @param data encrypted file content
   * @return file upload date
   */
  public static Date saveFile(String key, String data) {
    Entity fileEntity = new Entity(ENTITY_FILE, key);

    Date uploadDate = new Date();

    fileEntity.setProperty(PROP_TIMESTAMP, uploadDate);
    fileEntity.setProperty(PROP_VALUE, new Text(data));

    Logic.getDatastoreService().put(fileEntity);

    log.info("Uploaded file key=" + key + ", size=" + data.length());
    return uploadDate;
  }

  /**
   * @param key file id
   * @return file || null if absent
   */
  public static Attachment loadFile(String key) {
    try {
      Entity file = Logic.getDatastoreService().get(KeyFactory.createKey(ENTITY_FILE, key));
      return new Attachment(
          ((Text) file.getProperty(PROP_VALUE)).getValue(),
          (Date) file.getProperty(PROP_TIMESTAMP));
    } catch (EntityNotFoundException e) {
      log.info("File not found for key:" + key);
      return null;
    }
  }
}
