package maincode;

import maincode.controllers.MakeLabs_bot;
import maincode.helper.Log;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Log.setShowLevel(Log.EVERYTHING);

        System.out.println("Before starting bot activity, enter token:");
        Scanner scanner = new Scanner(System.in);
        String token = scanner.nextLine();

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
