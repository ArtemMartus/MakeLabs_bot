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
        String lowCaseMessage = gotMessage.toLowerCase();
        if (lowCaseMessage.contains("пидор"))
            answer = "А может ты пидор?";
        else if (lowCaseMessage.contains("что"))
            answer = "Что ?";
        else if (lowCaseMessage.contains("?"))
            answer = "Слишком много вопросов\n";
        else if (lowCaseMessage.contains("!"))
            answer = "Кайф";
        else if (lowCaseMessage.contains("слава украине") || lowCaseMessage.contains("слава україні"))
            answer = "Героям слава";
        else
            answer = "Извините, " + fromUser.getFirstName() + ", я понятия не имею что значит Ваше " + gotMessage;

        Log.Info(answer);

        if (Analytics.getInstance().getMakeLabs_bot().sendMessage(answer, chatId, null, fromUser) != null)
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

    public String getGotMessage() {
        return gotMessage;
    }

    public User getFromUser() {
        return fromUser;
    }

    public Long getChatId() {
        return chatId;
    }
}