package maincode.view;

import maincode.controllers.PostWorkController;
import maincode.data.ContractUser;
import maincode.data.PostWorkData;
import maincode.helper.Log;
import maincode.model.Analytics;
import maincode.viewmodel.ViewModel;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Observable;
import java.util.Observer;

public class View implements Observer {
    private ViewModel viewModel;
    private final Analytics analytics;

    public View() {
        analytics = Analytics.getInstance();
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

        if (messageId == null
                && inlineId == null
                && chatId != null
                && fromUser != null
                && gotMessage != null
                && gotMessage.equals("/start")) {

            Integer mid = viewModel.getMessageIdForUser(fromUser.getId());
            if (mid == null || mid == 0) {

                Message message = analytics.getMakeLabs_bot().sendMessage(".", chatId, fromUser);
                if (message != null)
                    mid = message.getMessageId();
                else
                    Log.Info("Could not get message id for "
                            + fromUser.getUserName()
                            + "["
                            + fromUser.getId()
                            + "]");

                viewModel.setMessageIdForUser(fromUser.getId(), mid);
            }
        }

        MessageHandler inlineMessageHandler = new InlineMessageHandler(fromUser, inlineId);
        if (inlineMessageHandler.isValid()) {
            inlineMessageHandler.handle();
        }

        MessageHandler callbackMessageHandler = new CallbackMessageHandler(callbackId, "caption", fromUser);

        if (contractUser != null
                && contractUser.getState() != null
                && !contractUser.getState().isEmpty()
                && fromUser != null) {

            CommandBuilder commandBuilder = new CommandBuilder(contractUser.getState(), gotMessage);
            String getUri = commandBuilder.getValidURI();

            PostWorkData workData = viewModel.getWorkData(getUri, fromUser);
            if (workData == null) {

                workData = PostWorkController.getData(getUri);
                viewModel.setWorkData(getUri, workData);
                Log.Info("Loaded " + getUri + " work data for " + fromUser.getUserName());
            }

            InlineKeyboardManager keyboardManager = new InlineKeyboardManager(workData);

            //TODO go write a bit of code in here about editing message and assigning keyboard
        }


        //default behavior
        MessageHandler regularMessageHandler = new RegularMessageHandler(gotMessage, fromUser, chatId);
        if (regularMessageHandler.isValid())
            regularMessageHandler.handle();
    }

}
