package info.xonix.passmanager;

/** User: xonix Date: 06.03.16 Time: 17:30 */
public final class Env {
  private static Boolean dev;

  public static boolean isDev() {
    if (dev == null) dev = "1".equals(System.getProperty("dev"));
    return dev;
  }

  public static String getMinSuffix() {
    return isDev() ? "" : ".min";
  }
}
