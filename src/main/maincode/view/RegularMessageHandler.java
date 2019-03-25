package maincode.view;

import maincode.helper.Log;
import maincode.model.Analytics;
import org.telegram.telegrambots.meta.api.objects.User;

public class RegularMessageHandler implements MessageHandler {

    private final String gotMessage;
    private final User fromUser;
    private final Long chatId;

    public RegularMessageHandler(String gotMessage, User fromUser, Long chatId) {
        this.gotMessage = gotMessage;
        this.fromUser = fromUser;
        this.chatId = chatId;
    }

    @Override
    public void handle() {
        String answer = "";
        if (gotMessage.toLowerCase().contains("пидор"))
            answer = "А может ты пидор?";
        else
            answer = "Echoing back - " + gotMessage + " to " + fromUser.getFirstName();

        Log.Info(answer);

        if (Analytics.getInstance().getMakeLabs_bot().sendMessage(answer, chatId, fromUser) != null)
            Log.Info("Successfully sent", Log.VERBOSE);
        else
            Log.Info("There was an issue with sending message");
    }

    @Override
    public boolean isValid() {
        return gotMessage != null
                && !gotMessage.isEmpty()
                && fromUser != null
                && chatId != null;
    }
}
