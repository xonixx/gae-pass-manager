package info.xonix.passmanager;

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

    public JsonReply(HttpServletResponse resp) throws IOException {
        this.resp = resp;
        reply();
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

        try {
            fillJson();
        } catch (Throwable t) {
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            res.put("error", t.toString());
            t.printStackTrace();
        }

        res.put("success", status == HttpServletResponse.SC_OK);
        resp.setStatus(status);

        resp.getWriter().print(Logic.gson.toJson(res));
        resp.getWriter().flush();
    }
}
