package info.xonix.passmanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.*;

/** User: xonix Date: 26.03.16 Time: 23:01 */
@Slf4j
@RequiredArgsConstructor
public class JsonReply {
  public static final String SUCCESS = "success";
  public static final String ERROR = "error";

  private final Gson gson;
  private final DatastoreService datastoreService;

  interface JsonFormer {
    void formJson(JsonBuilder jsonBuilder) throws Exception;
  }

  interface JsonBuilder {
    void setStatus(int status);

    void putField(String key, Object val);

    void putAllFields(Map<String, ?> data);

    default void err404(String errorMsg){
      log.error("ERROR 404: " + errorMsg);
      setStatus(SC_NOT_FOUND);
      putField(ERROR, errorMsg);
    }
  }

  private static class JsonBuilderImpl implements JsonBuilder {
    final Map<String, Object> res = new LinkedHashMap<>();
    int status = SC_OK;

    @Override
    public void setStatus(int status) {
      this.status = status;
    }

    @Override
    public void putField(String key, Object val) {
      res.put(key, val);
    }

    @Override
    public void putAllFields(Map<String, ?> data) {
      res.putAll(data);
    }
  }

  void reply(HttpServletResponse resp, boolean transactional, JsonFormer jsonFormer)
      throws IOException {
    JsonBuilderImpl jsonBuilder = new JsonBuilderImpl();
    resp.setContentType("application/json");

    Transaction transaction = null;
    try {
      if (transactional) {
        transaction = datastoreService.beginTransaction(TransactionOptions.Builder.withXG(true));
      }

      jsonFormer.formJson(jsonBuilder);
    } catch (Exception ex) {
      jsonBuilder.setStatus(SC_INTERNAL_SERVER_ERROR);
      jsonBuilder.putField(ERROR, ex.toString());
      log.error("Unknown exception", ex);
    }

    boolean success = jsonBuilder.status == SC_OK;
    if (transaction != null) {
      if (success) {
        transaction.commit();
      } else if (transaction.isActive()) {
        log.warn("Rollback tx");
        transaction.rollback();
      }
    }
    jsonBuilder.putField(SUCCESS, success);
    resp.setStatus(jsonBuilder.status);

    PrintWriter writer = resp.getWriter();
    writer.print(gson.toJson(jsonBuilder.res));
    writer.flush();
  }
}
