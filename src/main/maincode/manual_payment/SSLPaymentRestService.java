package main.maincode.manual_payment;

import com.sun.net.httpserver.*;
import main.maincode.makelabs_bot.helper.Log;
import main.maincode.makelabs_bot.model.Analytics;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.Random;

public class SSLPaymentRestService {

    Random random = new Random();
    AdminClient adminClient = new AdminClient("root", "password");

    public SSLPaymentRestService() {


        try {
            // setup the socket address
            InetSocketAddress address = new InetSocketAddress(1337);

            // initialise the HTTPS server
            HttpsServer httpsServer = HttpsServer.create(address, 0);
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // initialise the keystore
            char[] password = "password".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("testkey.jks");
            ks.load(fis, password);

            // setup the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            // setup the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            // setup the HTTPS context and parameters
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    try {
                        // initialise the SSL context
                        SSLContext context = getSSLContext();
                        SSLEngine engine = context.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());

                        // Set the SSL parameters
                        SSLParameters sslParameters = context.getSupportedSSLParameters();
                        params.setSSLParameters(sslParameters);

                    } catch (Exception ex) {
                        Log.Info("Failed to create HTTPS port", Log.PAYMENT_SERVICE);
                    }
                }
            });
            httpsServer.createContext("/", new RootHandler(this));
            httpsServer.setExecutor(null); // creates a default executor
            httpsServer.start();

        } catch (Exception exception) {
            Log.Info("Failed to create HTTPS server on port " + 1337 + " of localhost", Log.PAYMENT_SERVICE);
            exception.printStackTrace();

        }
    }

    public class RootHandler implements HttpHandler {

        private final SSLPaymentRestService service;

        public RootHandler(SSLPaymentRestService service) {
            this.service = service;
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
            Boolean ok = false;
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

    class AdminClient {
        public Long hashToken;
        public Long sessionId;
        public String username;
        public String password;

        public AdminClient(String username, String password) {
            hashToken = random.nextLong();
            sessionId = 137913791379L;
            this.username = username;
            this.password = password;
        }
    }

}
