/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package makelabs_bot.view;

import helper.Log;
import makelabs_bot.model.Analytics;
import org.telegram.telegrambots.meta.api.objects.User;

public class CallbackMessageHandler implements MessageHandler {

    private final String queryId;
    private final User toUser;
    private String caption;
    private boolean alreadyRun = false;

    public CallbackMessageHandler(String queryId, String caption, User toUser) {
        this.queryId = queryId;
        this.toUser = toUser;
        this.caption = caption;
    }

    @Override
    public void handle() {
        if (alreadyRun)
            return;

        Log.Info("Handling callbackQuery with caption " + caption);


        if (Analytics.getInstance().getMakeLabs_bot().sendCallbackAnswer(queryId, caption, toUser))
            Log.Info("Callback query answered");
        else
            Log.Info("Callback query answer failed");

        alreadyRun = true;
    }

    @Override
    public boolean isValid() {
        return queryId != null
                && !queryId.isEmpty()
                && toUser != null;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isAlreadyRun() {
        return alreadyRun;
    }
}
