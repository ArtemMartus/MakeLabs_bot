/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package makelabs_bot.viewmodel;

import makelabs_bot.helper.Log;
import makelabs_bot.model.Analytics;
import makelabs_bot.model.Model;
import makelabs_bot.model.data_pojo.Contract;
import makelabs_bot.model.data_pojo.ContractUser;
import makelabs_bot.model.data_pojo.PostWorkData;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import java.util.List;
import java.util.Observable;

public class ViewModel extends Observable {

    private final Model model;
    private ContractUser contractUser;
    private String handleMessage;
    private Integer messageId;
    private String callbackId;
    private String inlineId;
    private Long chatId;
    private User fromUser;

    public ViewModel(Model model) {
        this.model = model;
        new BackgroundService();
        Log.Info("ViewModel initialized");
    }

    public void setUpdate(Update update) {

        InlineQuery inlineQuery = update.getInlineQuery();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Message message = update.getMessage();
        fromUser = null;
        {
            if (inlineQuery != null) {
                inlineId = inlineQuery.getId();
                fromUser = inlineQuery.getFrom();
            } else {
                inlineId = null;
            }
            if (callbackQuery != null) {
                fromUser = callbackQuery.getFrom();
                callbackId = callbackQuery.getId();
            } else {
                callbackId = null;
            }
            if (message != null) {
                fromUser = message.getFrom();
            }
        }

        contractUser = handleUser(fromUser);

        if (contractUser == null) {
            Log.Info("contract user == null. continuing poll...");
            return;
        }

            Message localMessage = message == null ? (callbackQuery != null ? callbackQuery.getMessage() : null) : message;
            if (localMessage != null) {
                chatId = localMessage.getChatId();
            } else {
                chatId = null;
            }

            handleMessage = message != null && message.getText() != null ?
                    message.getText() :
                    (callbackQuery != null ?
                            callbackQuery.getData() : null);
            if (handleMessage == null) {
                handleMessage = inlineQuery != null ? inlineQuery.getQuery() : null;
            }

//            if (message != null
//                    && message.hasText()
//                    && contractUser.getWaitingForComment()) {
//                contractUser.setComment(handleMessage);
//                contractUser.setWaitingForComment(false);
//                handleMessage = "Подтвердить";
//            }


        setChanged();
        notifyObservers();

        model.saveContractUser(contractUser);

    }

    private ContractUser handleUser(User fromUser) {
        if (fromUser == null) {
            return null;
        }
        String username = fromUser.getUserName();
        String firstname = fromUser.getFirstName();
        String lastname = fromUser.getLastName();

        if (fromUser.getId() == 0) {
            Log.Info("userId == 0 Invalid update", Log.VERBOSE);
            return null;
        }
        ContractUser usr = model.getUser(fromUser.getId().longValue());

        if (usr == null) {
            messageId = getMessageIdForUser(fromUser.getId().longValue());
            usr = new ContractUser(fromUser.getId(),
                    username, firstname, lastname, messageId);
            model.saveContractUser(usr);
        } else {
            messageId = contractUser.getMessageId();
        }
        return usr;
    }


    public ContractUser getContractUser() {
        return contractUser;
    }


    public String getHandleMessage() {
        return handleMessage;
    }


    public Integer getMessageId() {
        return messageId;
    }

    public Integer getMessageIdForUser(Long uid) {
        if (uid == null) {
            return null;
        }
        return model.getMessageId(uid);
    }


    public String getCallbackId() {
        return callbackId;
    }


    public String getInlineId() {
        return inlineId;
    }


    public Long getChatId() {
        return chatId;
    }


    public User getFromUser() {
        return fromUser;
    }

    public PostWorkData getWorkData(String state, User userRequested) {
        return model.getPostWorkData(state, userRequested);
    }

    public void setWorkData(PostWorkData workData) {
        model.setPostWorkData(workData);
    }

    public Integer initializeUserState() {
        Message message = Analytics.getInstance().getMakeLabs_bot().sendMessage(".", chatId
                , null, fromUser);
        if (message != null)
            messageId = message.getMessageId();
        else
            Log.Info("Could not get message id for "
                    + fromUser.getUserName()
                    + "["
                    + fromUser.getId()
                    + "]");


        handleMessage = "/";
        contractUser.setStateUri(handleMessage);
        contractUser.setMessageId(messageId);
        model.saveContractUser(contractUser);

        return messageId;
    }

    public void saveContractUser(ContractUser usr) {
        model.setUser(usr);
    }

    public List<Contract> getContracts(ContractUser contractUser) {
        return model.getAllUserContracts(contractUser);
    }

    public Contract getUnappliedContract(ContractUser contractUser) {
        return model.getUnapprovedContract(contractUser);
    }

    public void saveContract(Contract contract) {
        model.saveContract(contract);
    }
}
