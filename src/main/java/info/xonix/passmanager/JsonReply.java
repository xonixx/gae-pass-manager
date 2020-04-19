package info.xonix.passmanager;

import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JsonReply {
  private final int status;
  private final String jsonBody;

  void write(HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json");
    resp.setStatus(status);

    PrintWriter writer = resp.getWriter();
    writer.print(jsonBody);
    writer.flush();
  }
}
