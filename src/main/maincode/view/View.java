package maincode.view;

import maincode.controllers.PostWorkController;
import maincode.data.ContractUser;
import maincode.data.PostWorkData;
import maincode.helper.Log;
import maincode.model.Analytics;
import maincode.viewmodel.ViewModel;
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
//                && messageId != null
        ) {

            CallbackMessageHandler callbackMessageHandler = new CallbackMessageHandler(callbackId,
                    "new message", fromUser);
            RegularMessageHandler regularMessageHandler = new RegularMessageHandler(gotMessage, fromUser, chatId);

            CommandBuilder commandBuilder = new CommandBuilder(contractUser.getState(), gotMessage);
            String getUri = commandBuilder.getValidURI();

            PostWorkData workData = viewModel.getWorkData(getUri, fromUser);
            if (workData == null) {

                workData = PostWorkController.getData(getUri);
                viewModel.setWorkData(getUri, workData);
                Log.Info("Loaded " + getUri + " work data for " + fromUser.getUserName());
            }

            InlineKeyboardManager keyboardManager = new InlineKeyboardManager(workData);

            UserActionHandler actionHandler = new UserActionHandler(regularMessageHandler,
                    callbackMessageHandler, contractUser,
                    keyboardManager, messageId, commandBuilder, viewModel);

            if (actionHandler.isValid())
                actionHandler.handle();

        }

    }

}
