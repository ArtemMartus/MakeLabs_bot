package main.makelabs_bot.viewmodel;

import main.makelabs_bot.data.ContractUser;
import main.makelabs_bot.data.PostWorkData;
import main.makelabs_bot.helper.Log;
import main.makelabs_bot.model.Analytics;
import main.makelabs_bot.model.Model;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import java.util.Observable;

public class ViewModel extends Observable {

    private final Model model;
    private Update update;
    private ContractUser contractUser;
    private String handleMessage;
    private Integer messageId;
    private String callbackId;
    private String inlineId;
    private Long chatId;
    private User fromUser;
    private final BackgroundService backgroundService;

    public ViewModel(Model model) {
        this.model = model;
        backgroundService = new BackgroundService();
        Log.Info("ViewModel initialized");
    }

    public void setUpdate(Update update) {
        this.update = update;

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

        //if (message != null || callbackQuery != null || inlineQuery != null)
        {
            Message localMessage = message == null ? (callbackQuery != null ? callbackQuery.getMessage() : null) : message;
            if (localMessage != null) {
                chatId = localMessage.getChatId();
            } else {
                chatId = null;
            }
            messageId = contractUser.getMessageId();
            if (messageId == null)
                messageId = getMessageIdForUser(fromUser.getId());

            handleMessage = message != null && message.getText() != null ?
                    message.getText() :
                    (callbackQuery != null ?
                            callbackQuery.getData() : null);
            if (handleMessage == null) {
                handleMessage = inlineQuery != null ? inlineQuery.getQuery() : null;
            }
//            else if(contractUser!=null){
//                CommandBuilder commandBuilder = new CommandBuilder(contractUser.getState(),handleMessage);
//                handleMessage = commandBuilder.getURI();
//            }

            if (message != null
                    && message.hasText()
                    && contractUser.getWaitingForComment()) {
                contractUser.setComment(handleMessage);
                contractUser.setWaitingForComment(false);
                handleMessage = "Подтвердить";
            }

        }

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

        if (fromUser.getId() == 0) {
            Log.Info("userId == 0 Invalid update", Log.VERBOSE);
            return null;
        }
        ContractUser usr = model.getUser(fromUser.getId());
        if (usr == null) {
            usr = new ContractUser(fromUser.getId(),
                    username, firstname);
//            usr.setState("/");
            model.setUser(usr);
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

    public Integer getMessageIdForUser(Integer uid) {
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

    public void setMessageIdForUser(Integer uid, Integer mid) {
        model.setMessageId(uid, mid);
    }

    public PostWorkData getWorkData(String state, User userRequested) {
        return model.getPostWorkData(state, userRequested);
    }

    public void setWorkData(String state, PostWorkData workData) {
        model.setPostWorkData(state, workData);
    }

    public Integer initializeUserState() {
        //commenting those three lines makes it force-update message id
//        messageId = getMessageIdForUser(fromUser.getId());
//        if (messageId == null || messageId == 0) {

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

        setMessageIdForUser(fromUser.getId(), messageId);

        handleMessage = "/";
        contractUser.setState(handleMessage);
//        }
        return messageId;
    }

    public void setContractUserForId(ContractUser usr) {
        model.setUser(usr);
    }
}
