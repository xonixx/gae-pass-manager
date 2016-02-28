package info.xonix.passmanager.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: xonix
 * Date: 28.02.16
 * Time: 20:50
 */
public class ApiServlet extends HttpServlet {

    public static final String ACTION = "action";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(ACTION);
        resp.setContentType("application/json");
        if ("load".equals(action)) {
            resp.getWriter().print("{\"data\":null}");
            resp.getWriter().flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ;
    }
}
