package maincode.viewmodel;

import maincode.data.ContractUser;
import maincode.helper.Log;
import maincode.model.Model;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import java.util.Observable;

public class ViewModel extends Observable {

    private final Model model;
    private final TelegramLongPollingBot longPollingBot;
    private Update update;
    private ContractUser contractUser;
    private String handleMessage;
    private Integer messageId;
    private String callbackId;
    private String inlineId;
    private Integer chatId;
    private User fromUser;

    public ViewModel(Model model, TelegramLongPollingBot bot) {
        this.model = model;
        longPollingBot = bot;
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
                chatId = localMessage.getMessageId();
            } else {
                chatId = null;
            }
            messageId = contractUser.getMessageId();
            handleMessage = message != null && message.getText() != null ?
                    message.getText() :
                    (callbackQuery != null ?
                            callbackQuery.getData() : null);
            if (handleMessage == null) {
                handleMessage = inlineQuery != null ? inlineQuery.getQuery() : null;
            }

            if (message != null && handleMessage != null) {
                switch (handleMessage) {
                    case "/start": {
                        messageId = null;
                        model.setMessageId(fromUser.getId(), messageId);
                        contractUser.setMessageId(messageId);
                        contractUser.setState("/");
                        break;
                    }
                    case "/help": {
                        break;
                    }
                    default:
                }
            }
        }

        model.saveContractUser(contractUser);

        setChanged();
        notifyObservers();

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
            model.setUser(fromUser.getId(), usr);
        }
        return usr;
    }

    public TelegramLongPollingBot getLongPollingBot() {
        return longPollingBot;
    }

    public ContractUser getContractUser() {
        return contractUser;
    }

    public void setContractUser(ContractUser contractUser) {
        this.contractUser = contractUser;
    }

    public String getHandleMessage() {
        return handleMessage;
    }

    public void setHandleMessage(String handleMessage) {
        this.handleMessage = handleMessage;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }

    public String getInlineId() {
        return inlineId;
    }

    public void setInlineId(String inlineId) {
        this.inlineId = inlineId;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void updateMention(User fromUser) {
        model.mentionedInline(fromUser);
    }
}
