package info.xonix.passmanager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Util {
  public static Map<String, Object> objectToMap(Object o) {
    Objects.requireNonNull(o);

    Map<String, Object> res = new HashMap<>();
    try {
      Field[] declaredFields = o.getClass().getDeclaredFields();
      for (Field field : declaredFields) {
        res.put(field.getName(), field.get(o));
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    return res;
  }

  private Util() {}
}
