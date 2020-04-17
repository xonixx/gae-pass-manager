package info.xonix.passmanager;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VersionManager {
  private static final Logger log = Logger.getLogger(VersionManager.class.getName());

  private static final Properties versionProps = new Properties();
  public static final String UNKNOWN = "UNKNOWN";

  public static final String VERSION_FILE = "version.properties";

  static {
    try {
      versionProps.load(VersionManager.class.getClassLoader().getResourceAsStream(VERSION_FILE));
    } catch (IOException e) {
      log.log(Level.SEVERE, "Can't read project version", e);
    }
  }

  public static String getApplicationVersion() {
    return versionProps.getProperty("pom", UNKNOWN)
        + "r"
        + versionProps.getProperty("revision", UNKNOWN);
  }
}
