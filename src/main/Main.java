package main;

import io.github.cdimascio.dotenv.Dotenv;
import main.makelabs_bot.controllers.MakeLabs_bot;
import main.makelabs_bot.helper.Log;
import main.manual_payment.SSLPaymentRestService;
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
