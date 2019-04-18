/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.view;

import main.makelabs_bot.helper.InnerPath;
import main.makelabs_bot.helper.Log;
import main.makelabs_bot.model.Analytics;
import org.telegram.telegrambots.meta.api.objects.User;

public class RegularMessageHandler implements MessageHandler {

    private final InnerPath innerPath;
    private final User fromUser;
    private final Long chatId;
    private boolean goHome = false;

    public RegularMessageHandler(InnerPath innerPath, User fromUser, Long chatId) {
        this.innerPath = innerPath;
        this.fromUser = fromUser;
        this.chatId = chatId;
    }

    @Override
    public void handle() {
        String answer = "";
        String lowCaseMessage = innerPath.getLast().toLowerCase();
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
        else {
            answer = "Извините, "
                    + fromUser.getFirstName()
                    + ", я понятия не имею что значит Ваше "
                    + innerPath.getLast();
            goHome = true;
        }

        Log.Info(answer);

        if (Analytics.getInstance().getMakeLabs_bot().sendMessage(answer, chatId, null, fromUser) != null)
            Log.Info("Successfully sent", Log.VERBOSE);
        else
            Log.Info("There was an issue with sending message");
    }

    @Override
    public boolean isValid() {
        return innerPath != null
                && !innerPath.isEmpty()
                && innerPath.isAbsolute()
                && fromUser != null
                && chatId != null;
    }

    public InnerPath getInnerPath() {
        return innerPath;
    }

    public User getFromUser() {
        return fromUser;
    }

    public Long getChatId() {
        return chatId;
    }

    public boolean goHome() {
        return goHome;
    }
}
