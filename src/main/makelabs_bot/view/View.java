/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.view;

import main.makelabs_bot.helper.InnerPath;
import main.makelabs_bot.helper.Log;
import main.makelabs_bot.model.Analytics;
import main.makelabs_bot.model.data_pojo.ContractUser;
import main.makelabs_bot.model.data_pojo.PostWorkData;
import main.makelabs_bot.viewmodel.ViewModel;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Observable;
import java.util.Observer;

public class View implements Observer {

    public View() {
        Analytics.getInstance();
        Log.Info("View initialized");
    }

    @Override
    public void update(Observable o, Object arg) {
        ViewModel viewModel = (ViewModel) o;
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
                && contractUser.getStateUri() != null
                && !contractUser.getStateUri().isEmpty()
                && fromUser != null
                && chatId != null
                && gotMessage != null
                && !gotMessage.isEmpty()
        ) {

            CallbackMessageHandler callbackMessageHandler = new CallbackMessageHandler(callbackId,
                    null, fromUser);


            InnerPath innerPath = new InnerPath(contractUser.getStateUri());
            innerPath.addCommand(gotMessage);
            RegularMessageHandler regularMessageHandler = new RegularMessageHandler(innerPath, fromUser, chatId);

            if (!innerPath.isWorkData()) {
                innerPath.goBack();//get valid path
            }
            String getUri = innerPath.getPath();

            PostWorkData workData = viewModel.getWorkData(getUri, fromUser);
            if (workData == null) {
                Log.Info("Loaded " + getUri + " work data_pojo for " + fromUser.getUserName());
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
