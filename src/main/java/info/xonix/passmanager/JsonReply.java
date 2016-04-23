package info.xonix.passmanager;

import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * User: xonix
 * Date: 26.03.16
 * Time: 23:01
 */
public abstract class JsonReply {
    private static final Logger log = Logger.getLogger(JsonReply.class.getName());

    private HttpServletResponse resp;
    private final Map<String, Object> res = new LinkedHashMap<>();
    private int status = HttpServletResponse.SC_OK;
    private boolean transactional;

    public JsonReply(HttpServletResponse resp, boolean transactional) throws IOException {
        this.resp = resp;
        this.transactional = transactional;
        reply();
    }

    public JsonReply(HttpServletResponse resp) throws IOException {
        this(resp, false);
    }

    protected abstract void fillJson() throws IOException;

    /**
     * Put field to resulting JSON
     */
    protected void putField(String key, Object val) {
        res.put(key, val);
    }

    protected void putAllFields(Map<String, ?> data) {
        res.putAll(data);
    }

    protected void err404(String errorMsg) {
        log.severe("ERROR 404: " + errorMsg);

        res.put("error", errorMsg);
        status = HttpServletResponse.SC_NOT_FOUND;
    }

    private void reply() throws IOException {
        resp.setContentType("application/json");

        Transaction transaction = null;
        try {
            if (transactional)
                transaction = Logic.getDatastoreService().beginTransaction(
                        TransactionOptions.Builder.withXG(true));

            fillJson();

            if (transaction != null)
                transaction.commit();
        } catch (Throwable t) {
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            res.put("error", t.toString());
            t.printStackTrace();
        }

        boolean success = status == HttpServletResponse.SC_OK;
        if (transaction != null && !success && transaction.isActive()) {
            transaction.rollback();
        }
        res.put("success", success);
        resp.setStatus(status);

        resp.getWriter().print(Logic.gson.toJson(res));
        resp.getWriter().flush();
    }
}
