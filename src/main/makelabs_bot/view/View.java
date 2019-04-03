package main.makelabs_bot.view;

import main.makelabs_bot.controllers.PostWorkController;
import main.makelabs_bot.data.ContractUser;
import main.makelabs_bot.data.PostWorkData;
import main.makelabs_bot.helper.Log;
import main.makelabs_bot.model.Analytics;
import main.makelabs_bot.viewmodel.ViewModel;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Observable;
import java.util.Observer;

public class View implements Observer {
    private ViewModel viewModel;
    private final Analytics analytics;

    public View() {
        analytics = Analytics.getInstance();
        Log.Info("View initialized");
    }

    @Override
    public void update(Observable o, Object arg) {
        viewModel = (ViewModel) o;
        String gotMessage = viewModel.getHandleMessage();

        String inlineId = viewModel.getInlineId();
        String callbackId = viewModel.getCallbackId();
        Integer messageId = viewModel.getMessageId();
        ContractUser contractUser = viewModel.getContractUser();
        Long chatId = viewModel.getChatId();
        User fromUser = viewModel.getFromUser();

        InlineMessageHandler inlineMessageHandler = new InlineMessageHandler(fromUser, inlineId);
        if (inlineMessageHandler.isValid()) {
            inlineMessageHandler.handle();
        }

        if (contractUser != null
                && contractUser.getState() != null
                && !contractUser.getState().isEmpty()
                && fromUser != null
                && chatId != null
                && gotMessage != null
                && !gotMessage.isEmpty()
        ) {

            CallbackMessageHandler callbackMessageHandler = new CallbackMessageHandler(callbackId,
                    null, fromUser);

            CommandBuilder commandBuilder = new CommandBuilder(contractUser.getState(), gotMessage);
            RegularMessageHandler regularMessageHandler = new RegularMessageHandler(commandBuilder, fromUser, chatId);

            String getUri = commandBuilder.getValidURI();

            PostWorkData workData = viewModel.getWorkData(getUri, fromUser);
            if (workData == null) {

                workData = PostWorkController.getData(getUri, false);
                viewModel.setWorkData(getUri, workData);
                Log.Info("Loaded " + getUri + " work data for " + fromUser.getUserName());
            }

            InlineKeyboardManager keyboardManager = new InlineKeyboardManager(workData);

            UserActionHandler actionHandler = new UserActionHandler(regularMessageHandler,
                    callbackMessageHandler, contractUser,
                    keyboardManager, messageId, viewModel);

            if (actionHandler.isValid())
                actionHandler.handle();

        }

    }

}
