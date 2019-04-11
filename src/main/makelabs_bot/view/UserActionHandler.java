/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.view;

import main.makelabs_bot.helper.Log;
import main.makelabs_bot.model.Analytics;
import main.makelabs_bot.model.data_pojo.Contract;
import main.makelabs_bot.model.data_pojo.ContractUser;
import main.makelabs_bot.model.data_pojo.PostWorkData;
import main.makelabs_bot.viewmodel.ViewModel;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public class UserActionHandler implements MessageHandler {
    private final RegularMessageHandler messageHandler;
    private final CallbackMessageHandler callbackMessageHandler;
    private final ContractUser contractUser;
    private final User fromUser;
    private final InlineKeyboardManager keyboard;
    private final Long chatId;
    private final CommandBuilder commandBuilder;
    private final ViewModel viewModel;

    private boolean handled = false;
    private PostWorkData workData;
    private Integer messageId;
    private String editedText = null;

    public UserActionHandler(RegularMessageHandler messageHandler, CallbackMessageHandler callbackMessageHandler,
                             ContractUser contractUser, InlineKeyboardManager keyboard,
                             Integer messageId, ViewModel viewModel) {
        this.messageHandler = messageHandler;
        this.callbackMessageHandler = callbackMessageHandler;
        this.contractUser = contractUser;
        this.keyboard = keyboard;
        this.messageId = messageId;


        if (messageHandler.isValid()) {
            this.commandBuilder = messageHandler.getCommandBuilder();
            this.fromUser = messageHandler.getFromUser();
            this.chatId = messageHandler.getChatId();
        } else {
            this.fromUser = null;
            this.chatId = null;
            this.commandBuilder = null;
        }

        if (keyboard.isValid()) {
            this.workData = keyboard.getWorkData();
        } else {
            this.workData = null;
        }

        this.viewModel = viewModel;

    }

    private void getHome() {
        messageId = viewModel.initializeUserState();
        commandBuilder.setHome();
        workData = viewModel.getWorkData(commandBuilder.getValidURI(), fromUser);
        contractUser.setMessageId(messageId);
    }

    private void Send(String message) {
        Analytics.getInstance().getMakeLabs_bot().sendMessage(message, chatId, null, fromUser);
    }

    private boolean precheck() throws Exception {
        String command = commandBuilder.getCommand();
        switch (command) {
            case "start": {
                getHome();
                return true;
            }
            case "help": {
                Analytics.getInstance().getMakeLabs_bot().sendMessage("Введите /start чтобы вызвать меню",
                        chatId, null, fromUser);
                return true;
            }
            case "Сотрудничество": {
                Send("Присоединяйтесь к комманде Make Labs\n" +
                        "Если Вы срочно хотите заработать денег\n" +
                        "и способны выполнять лабораторные работы\n" +
                        "пишите этому боту @MakeLabsJob_bot\n" +
                        "");
                return true;
            }
            case "О нас": {
                Send("Make Labs это бот-помошник созданный с целью\n" +
                        "избавить студентов от рутинных заданий\n" +
                        "чтобы Вы могли заниматься любимыми делами\n" +
                        "не переживая о незданных самостоятельных работах\n" +
                        "Telegram: @upsage");
                return true;
            }

            //TODO make ability to choose whether the work should be made just to pass 60% with 0.75 price in 5 days or \n
            // made normally for regular price in 5 days or made finest quality in 5 days for double price\n
            // or made in 1 day with fines quality with 4 times price

            //TODO start planning Jobs bot
            // we can handle new jobs by asking employees for pdf task details and writing our own prices. It would be the safest way
            case "Мои заказы": {
                List<Contract> contractList = viewModel.getContracts(contractUser);
                if (contractList.size() > 0) {
                    for (Contract contract : contractList) {
                        Send(contract.toString());
                    }

                    getHome();

                } else {
                    if (!callbackMessageHandler.isAlreadyRun()) {
                        callbackMessageHandler.setCaption("У Вас отсутствуют активные заказы");
                        callbackMessageHandler.handle();
                    } else
                        Send("У Вас отсутствуют активные заказы");
                }
                return true;
            }
            case "Назад": {
                commandBuilder.updateState(contractUser.goBack());
                commandBuilder.clearCommand();
                workData = viewModel.getWorkData(commandBuilder.getValidURI(), fromUser);
                return true;
            }
            case "Подтвердить": {
                {
                    Contract contract = viewModel.getUnappliedContract(contractUser);
                    contract.setUpAllIncluding(workData);
                    viewModel.saveContract(contract);
                    Send(contract.toString());
                    Send("Оплатите заказ переводом в " + contract.getPrice() + "₴ на карту монобанка 5375411401989640,");
                    getHome();

                }
                break;
            }
            default: {

                workData = viewModel.getWorkData(commandBuilder.getValidURI(), fromUser);

                if (workData.hasChild(commandBuilder.getCommand())) {
                    Contract contract = viewModel.getUnappliedContract(contractUser);

                    contract.setUpAllIncluding(workData);

                    contract.toggle(commandBuilder.getCommand());

                    editedText = contract.getCheckoutText(workData);

                    return true;
                }

            }
        }
        return false;
    }

    @Override
    public void handle() {
        boolean returnPrecheck = false;
        if (!commandBuilder.baseUrlValid()
                && !commandBuilder.getCommand().isEmpty()) {
            try {
                returnPrecheck = precheck();
            } catch (Exception e) {
                e.printStackTrace();// may be interesting
                return;
            }
        }
        if (messageId == null) {
            handled = returnPrecheck;
        }

        contractUser.setStateUri(commandBuilder.getValidURI());
        viewModel.saveContractUser(contractUser);

        if (workData == null) {
            Log.Info("Some strange shit makes data_pojo set to null...");
            workData = viewModel.getWorkData(commandBuilder.getValidURI(), fromUser);
        }

        keyboard.updateData(workData);


        if (!returnPrecheck
                && editedText == null) {
            boolean is_not_endpoint = !workData.isEndpoint();

            if (!is_not_endpoint) {// If we just loaded last branch show the recipe for un applied contract
                Contract contract = viewModel.getUnappliedContract(contractUser);
                try {
                    contract.setUpAllIncluding(workData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                editedText = contract.getCheckoutText(workData);
            }
        }


        Log.Info("\tText = " + commandBuilder.getURI()
                + "\n\tCommand = " + commandBuilder.getCommand()
                + "\n\tValid Text = " + commandBuilder.getValidURI());


        if (editedText == null)
            editedText = workData.getDescription();


        if (messageId != null) {
            if (Analytics.getInstance().getMakeLabs_bot().editMessage(editedText,
                    chatId, messageId, keyboard.getMarkup(), fromUser) == null) {

                Log.Info("Some issue with editing a message to " + fromUser.getUserName());
                getHome();
                editedText = workData.getDescription();
                keyboard.updateData(workData);
                // Give it a second try
                if (Analytics.getInstance().getMakeLabs_bot().sendMessage(editedText,
                        chatId, keyboard.getMarkup(), fromUser) == null) {

                    Log.Info("An error with sending a message to " + fromUser.getUserName());
                } else {
                    Log.Info("Sent fresh message ");
                    handled = true;
                }
            } else {
                Log.Info("Edited message ");
                handled = true;
            }
        }


        if (callbackMessageHandler.isValid())
            callbackMessageHandler.handle();

        if (!handled
                && messageHandler.isValid()) {
            messageHandler.handle();
            if (messageHandler.goHome())
                getHome();
        }

        handled = true;
    }

    @Override
    public boolean isValid() {
        return !handled
                && keyboard != null
                && keyboard.isValid()
                && commandBuilder != null
                && commandBuilder.isValid()
                && viewModel != null
                && ((messageHandler != null && messageHandler.isValid())
                || (callbackMessageHandler != null && callbackMessageHandler.isValid()));
    }

}
