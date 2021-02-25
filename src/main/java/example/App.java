package example;

import javax.servlet.ServletException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.FunctionInitializer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.aliyun.fc.runtime.HttpRequestHandler;
import org.apache.commons.io.IOUtils;
import java.util.Base64;
import java.util.Map;
import java.io.IOException;
import java.io.OutputStream;


public class App implements HttpRequestHandler, FunctionInitializer {
    private static final String RESPONSE_MSG = "success";
    private static final String QUERY_PARAMETER_SIGNATURE = "msg_signature";
    private static final String QUERY_PARAMETER_TIMESTAMP = "timestamp";
    private static final String QUERY_PARAMETER_NONCE = "nonce";
    private static final String TOKEN_NAME = "DD_TOKEN";
    private static final String AES_KEY_NAME = "DD_AES_KEY";
    private static final String DD_APP_KEY = "DD_APP_KEY";

    private  DingCallbackCrypto callbackCrypto;

    public void initialize(Context context) throws IOException {
        try {
            callbackCrypto = new DingCallbackCrypto(System.getenv(TOKEN_NAME),
                    System.getenv(AES_KEY_NAME), System.getenv(DD_APP_KEY));
        } catch (Exception e) {
            context.getLogger().error("DingCallbackCrypto init failed due to:" + e);
        }
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context context)
            throws IOException, ServletException {

        String bodyStr = IOUtils.toString(request.getReader());
        context.getLogger().info("Get request body: " + bodyStr);
        JSONObject requestBody = JSON.parseObject(bodyStr);
        String encryptMsg = requestBody.getString("encrypt");

        String decryptMsg = null;
        try {
            decryptMsg = callbackCrypto.getDecryptMsg(request.getParameter(QUERY_PARAMETER_SIGNATURE),
                    request.getParameter(QUERY_PARAMETER_TIMESTAMP), request.getParameter(QUERY_PARAMETER_NONCE),  encryptMsg);
        } catch (DingCallbackCrypto.DingTalkEncryptException e) {
            context.getLogger().error("DingCallbackCrypto getDecryptMsg failed due to:" + e);
            e.printStackTrace();
            OutputStream out = response.getOutputStream();
            out.write(e.toString().getBytes());
            out.flush();
            out.close();
            return;
        }

        JSONObject eventJson = JSON.parseObject(decryptMsg);
        String eventType = eventJson.getString("EventType");

        if ("check_url".equals(eventType)) {
            // check callback url
            context.getLogger().info("Check callback URL correct");
        } else if ("user_add_org".equals(eventType)) {
            // process event
            context.getLogger().info("Received " + eventType + " event");
        } else {
            // process event
            context.getLogger().info("Received " + eventType + " event");
        }

        Map<String, String> successMap = null;
        try {
            successMap = callbackCrypto.getEncryptedMap(RESPONSE_MSG);
        } catch (DingCallbackCrypto.DingTalkEncryptException e) {
            context.getLogger().error("DingCallbackCrypto getEncryptedMap failed due to:" + e);
            e.printStackTrace();
            OutputStream out = response.getOutputStream();
            out.write(e.toString().getBytes());
            out.flush();
            out.close();
            return;
        }

        response.setStatus(200);
        response.setHeader("Content-Type", "application/json");
        String body = JSON.toJSONString(successMap);
        OutputStream out = response.getOutputStream();
        out.write(body.getBytes());
        out.flush();
        out.close();
    }
}
