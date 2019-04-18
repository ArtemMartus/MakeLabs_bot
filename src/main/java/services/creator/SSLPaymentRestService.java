/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package services.creator;

import com.sun.net.httpserver.*;
import helper.Log;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.Random;

public class SSLPaymentRestService {

    Random random = new Random();
    AdminClient adminClient = new AdminClient("root", "password", random.nextLong());

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
            //todo implement adding new post work data
            HttpContext rootContext = httpsServer.createContext("/", new RootHandler(this, adminClient));
            rootContext.setAuthenticator(new BasicAuthenticator("test") {
                @Override
                public boolean checkCredentials(String user, String pwd) {
                    System.out.println("Got credentials " + user + ":" + pwd);
                    return user.equals("test") && pwd.equals("test");
                }
            });

            httpsServer.setExecutor(null); // creates a default executor
            httpsServer.start();

        } catch (Exception exception) {
            Log.Info("Failed to create HTTPS server on port " + 1337 + " of localhost", Log.PAYMENT_SERVICE);
            exception.printStackTrace();

        }
    }

}
