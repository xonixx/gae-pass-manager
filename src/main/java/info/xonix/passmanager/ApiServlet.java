package info.xonix.passmanager;

import com.google.gson.Gson;
import info.xonix.passmanager.model.Attachment;
import info.xonix.passmanager.model.PassData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class ApiServlet extends HttpServlet {
  public static final String ACTION = "action";

  public static final String ACTION_LOAD = "load";
  public static final String ACTION_LOAD_FILE = "loadFile";

  public static final String ACTION_MODIFY = "modify";
  public static final String SUB_ACTION_SAVE_DATA = "saveData";
  public static final String SUB_ACTION_SAVE_FILES = "saveFiles";
  public static final String SUB_ACTION_DELETE_FILES = "deleteFiles";

  public static final String PARAM_KEY = "key";
  public static final String PARAM_DATA = "data";

  private final Gson gson;
  private final AppLogic appLogic;
  private final JsonReply jsonReply;

  @Override
  protected void doGet(final HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    jsonReply.reply(
        resp,
        false,
        jsonBuilder -> {
          String action = req.getParameter(ACTION);
          if (ACTION_LOAD.equals(action)) {
            PassData passData = appLogic.getEncryptedPassData();
            if (passData != null) {
              jsonBuilder.putAllFields(Util.objectToMap(passData));
            }
          } else if (ACTION_LOAD_FILE.equals(action)) {
            String key = req.getParameter(PARAM_KEY);
            Attachment att = appLogic.loadFile(key);
            if (att != null) {
              jsonBuilder.putAllFields(Util.objectToMap(att));
            }
          }
        });
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {
    jsonReply.reply(
        resp,
        true,
        jsonBuilder -> {
          String action = req.getParameter(ACTION);
          Map<?, ?> json = gson.fromJson(new InputStreamReader(req.getInputStream()), Map.class);

          if (!ACTION_MODIFY.equals(action)) {
            jsonBuilder.err404("action " + action);
            return;
          }

          for (Map.Entry entry : json.entrySet()) {
            String subAction = (String) entry.getKey();
            Object subActionData = entry.getValue();

            if (SUB_ACTION_SAVE_DATA.equals(subAction)) {
              Date lastUpdated = appLogic.storeEncryptedPassData((String) subActionData);
              jsonBuilder.putField("lastUpdated", lastUpdated);

            } else if (SUB_ACTION_SAVE_FILES.equals(subAction)) {
              List<Map> files = (List<Map>) subActionData;
              Date uploaded = null;
              for (Map file : files) {
                String key = (String) file.get(PARAM_KEY);
                String data = (String) file.get(PARAM_DATA);
                uploaded = appLogic.saveFile(key, data);
              }
              jsonBuilder.putField("uploaded", uploaded); // TODO

            } else if (SUB_ACTION_DELETE_FILES.equals(subAction)) {
              List<String> keys = (List<String>) subActionData;
              for (String key : keys) {
                appLogic.deleteFile(key);
              }
            } else {
              jsonBuilder.err404("subAction " + subAction);
            }
          }
        });
  }
}
