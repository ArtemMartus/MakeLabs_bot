package maincode.view;

import maincode.helper.Log;
import maincode.model.Analytics;
import org.telegram.telegrambots.meta.api.objects.User;

public class CallbackMessageHandler implements MessageHandler {

    private final String queryId;
    private final User toUser;
    private final String caption;

    public CallbackMessageHandler(String queryId, String caption, User toUser) {
        this.queryId = queryId;
        this.toUser = toUser;
        this.caption = caption;
    }

    @Override
    public void handle() {
        Log.Info("Handling callbackQuery with caption " + caption);


        if (Analytics.getInstance().getMakeLabs_bot().sendCallbackAnswer(queryId, caption, toUser))
            Log.Info("Callback query answered");
        else
            Log.Info("Callback query answer failed");
    }

    @Override
    public boolean isValid() {
        return queryId != null
                && !queryId.isEmpty()
                && toUser != null;
    }
}
