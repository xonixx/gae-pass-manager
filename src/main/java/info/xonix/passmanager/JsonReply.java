package info.xonix.passmanager;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: xonix
 * Date: 26.03.16
 * Time: 23:01
 */
public abstract class JsonReply {
    public abstract void fillJson(Map<String, Object> result) throws IOException;

    public static void reply(HttpServletResponse resp, JsonReply jsonReply) throws IOException {
        resp.setContentType("application/json");

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);

        try {
            jsonReply.fillJson(res);
        } catch (Throwable t) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            t.printStackTrace();
            res.put("success", false);
            res.put("error", t.toString());
        }

        resp.getWriter().print(Logic.gson.toJson(res));
        resp.getWriter().flush();
    }
}
