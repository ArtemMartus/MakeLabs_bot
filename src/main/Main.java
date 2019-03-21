package main;

import data.Log;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

public class Main {

    public static void main(String[] args) throws Exception {
        Log.setShowLevel(Log.EVERYTHING);
        ApiContextInitializer.init();
        DataClass dataClass = new DataClass();
        MyClass bot = new MyClass(dataClass);
        TelegramBotsApi api = new TelegramBotsApi();
        try {
            api.registerBot(bot);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }
}
