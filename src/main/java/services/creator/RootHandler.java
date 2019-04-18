/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package services.creator;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsExchange;
import helper.Log;
import makelabs_bot.model.Analytics;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class RootHandler implements HttpHandler {

    private final SSLPaymentRestService service;
    private final AdminClient adminClient;

    public RootHandler(SSLPaymentRestService service, AdminClient adminClient) {
        this.service = service;
        this.adminClient = adminClient;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        HttpsExchange httpsExchange = (HttpsExchange) t;

        String method = httpsExchange.getRequestMethod();
        String requestBody;

        Long sessionId = (Long) httpsExchange.getAttribute("session");
        Long hashToken = (Long) httpsExchange.getAttribute("hashToken");
        String username = (String) httpsExchange.getAttribute("username");
        String password = (String) httpsExchange.getAttribute("password");
        String action = (String) httpsExchange.getAttribute("action");

        {
            InputStreamReader isr = new InputStreamReader(httpsExchange.getRequestBody());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder stringBuilder = new StringBuilder();
            while (reader.ready())
                stringBuilder.append(reader.readLine());
            requestBody = stringBuilder.toString();
        }
        Log.Info(method, Log.PAYMENT_SERVICE);
        if (requestBody.length() > 1) {
            String[] args = requestBody.split("&");
            for (String arg : args) {
                String[] keyVal = arg.split("=");
                if (keyVal.length != 2)
                    continue;
                Log.Info(keyVal[0] + " = " + keyVal[1], Log.PAYMENT_SERVICE);
                if (keyVal[0].equals("username"))
                    username = keyVal[1];
                if (keyVal[0].equals("password"))
                    password = keyVal[1];
                if (keyVal[0].equals("action"))
                    action = keyVal[1];

                if (keyVal[0].equals("session"))
                    try {
                        sessionId = Long.parseLong(keyVal[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                if (keyVal[0].equals("hashToken"))
                    try {
                        hashToken = Long.parseLong(keyVal[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        } else
            Log.Info(requestBody, Log.PAYMENT_SERVICE);

        String response = getResponse(method, hashToken, sessionId, username, password, action, requestBody);


        t.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String getParam(String requestBody, String paramName) {
        if (requestBody.length() > 1) {
            String[] args = requestBody.split("&");
            for (String arg : args) {
                String[] keyVal = arg.split("=");
                if (keyVal.length != 2)
                    continue;
                if (keyVal[0].equals(paramName))
                    return keyVal[1];
            }
        }
        return null;
    }

    private String getResponse(String method, Long hashToken, Long sessionId, String username, String password
            , String action, String requestBody) {
        JSONObject object = new JSONObject();
        boolean ok = false;
        if (method.equals("GET")) {
            if (sessionId == null) {
                object.put("session", adminClient.sessionId);
            }
        } else {
            if (sessionId != null && sessionId > 0) {
                if (username == null && password == null) {
                    try {
                        if (service.adminClient.sessionId.equals(sessionId)) {
                            if (service.adminClient.hashToken.equals(hashToken)) {
                                ok = true;
                                if (action != null) {
                                    switch (action) {
                                        case "getContracts":
                                            object.put("contracts",
                                                    Analytics.getInstance().getMakeLabs_bot().getAllContracts());
                                            Log.Info(object.toString(), Log.PAYMENT_SERVICE);
                                            break;
                                        case "setStatus":
                                            String status = getParam(requestBody, "status");
                                            String contract = getParam(requestBody, "contract");
                                            if (status != null && contract != null) {
                                                Long contractId = null;
                                                try {
                                                    contractId = Long.parseLong(contract);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    break;
                                                }
                                                switch (status) {
                                                    case "paid":
                                                        Analytics.getInstance().getMakeLabs_bot().setContractPaid(contractId);
                                                        break;
                                                    default:
                                                }
                                                ok = true;
                                            }
                                            break;
                                        default:
                                    }
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (username != null && password != null) {
                    if (username.equals(service.adminClient.username)
                            && password.equals(service.adminClient.password)
                            && sessionId.equals(service.adminClient.sessionId)) {
                        ok = true;
                        object.put("hashToken", service.adminClient.hashToken);
                    }
                }
            }
        }
        object.put("ok", ok);
        return object.toString();
    }
}