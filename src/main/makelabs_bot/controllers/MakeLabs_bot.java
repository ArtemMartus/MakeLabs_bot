package main.makelabs_bot.controllers;

import main.makelabs_bot.data.Contract;
import main.makelabs_bot.helper.Log;
import main.makelabs_bot.model.Analytics;
import main.makelabs_bot.model.Model;
import main.makelabs_bot.view.View;
import main.makelabs_bot.viewmodel.ViewModel;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.Serializable;
import java.util.List;

public class MakeLabs_bot extends TelegramLongPollingBot {

    private final Model model;
    private final View view;
    private final ViewModel viewModel;
    private final Analytics analytics;
    private final String token;

    public MakeLabs_bot(String token) {
        model = new Model();
        viewModel = new ViewModel(model);
        view = new View();
        viewModel.addObserver(view);

        analytics = Analytics.getInstance();
        analytics.setMakeLabs_bot(this);

        this.token = token;

        Log.Info("Bot initialized");
    }


    public Message sendMessage(String text, Long chatId, InlineKeyboardMarkup keyboardMarkup, User toUser) {
        analytics.updateSentMessages(toUser);

        SendMessage sendMessage = new SendMessage(chatId, text);
        if (keyboardMarkup != null)
            sendMessage.setReplyMarkup(keyboardMarkup);
        Message message = null;
        try {
            message = execute(sendMessage);
        } catch (Exception ex) {
            Log.Info("Exception in sendMessage:" + ex.getMessage());
            return null;
        }

        return message;
    }

    public Serializable editMessage(String editedText, Long chatId, Integer messageId,
                                    InlineKeyboardMarkup keyboardMarkup, User toUser) {

        analytics.updateEditedMessages(toUser);

        Serializable message = null;
        EditMessageText e = new EditMessageText();
        e
                .setChatId(chatId)
                .setMessageId(messageId)
                .setText(editedText);
        if (keyboardMarkup != null)
            e.setReplyMarkup(keyboardMarkup);
        try {
            message = execute(e);
        } catch (Exception ex) {
            Log.Info("Exception in editMessage :" + ex.getMessage());
            return null;
        }
        return message;

    }

    public Boolean answerInlineQuery(AnswerInlineQuery aiq, User toUser) {
        analytics.updateAnsweredInlineQueries(toUser);

        Boolean result = null;
        try {
            result = execute(aiq);
        } catch (Exception ex) {
            Log.Info("An issue in answerInlineQuery - " + ex.getMessage());
        }
        return result != null && result;
    }

    public Boolean sendCallbackAnswer(String queryID, String caption, User toUser) {
        if (queryID == null
                || queryID.isEmpty())
            return false;
        analytics.updateCallbackAnswered(toUser);

        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(queryID);
        if (caption != null && !caption.isEmpty())
            answerCallbackQuery.setText(caption);
        try {
            return execute(answerCallbackQuery);
        } catch (Exception ex) {
            Log.Info("Exception in sendCallbackAnswer:" + ex.getMessage(), Log.VERBOSE);
            //ex.printStackTrace();
        }
        return false;
    }

    public List<Contract> getAllContracts() {
        return model.getAllOpenContracts();
    }

    @Override
    public void onUpdateReceived(Update update) {
        viewModel.setUpdate(update);
    }

    @Override
    public String getBotUsername() {
        return "MakeLabs_bot";
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public void setContractPaid(Long contractId) {
        List<Contract> all = getAllContracts();
        all.forEach((item) -> {
            if (contractId.equals(item.getId())) {
                item.paid();
                return;
            }
        });
    }
}
