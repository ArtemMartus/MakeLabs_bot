/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

import io.github.cdimascio.dotenv.Dotenv;
import makelabs_bot.controllers.MakeLabs_bot;
import makelabs_bot.helper.Log;
import manual_payment.SSLPaymentRestService;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

public class Main {

    public static void main(String[] args) {
        Log.setShowLevel(Log.EVERYTHING);
        SSLPaymentRestService service = new SSLPaymentRestService();

        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("token");

        ApiContextInitializer.init();
        MakeLabs_bot bot = new MakeLabs_bot(token);
        TelegramBotsApi api = new TelegramBotsApi();
        try {
            api.registerBot(bot);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }
}
