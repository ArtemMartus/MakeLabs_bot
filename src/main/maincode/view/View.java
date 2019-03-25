package maincode.view;

import maincode.data.ContractUser;
import maincode.viewmodel.ViewModel;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

import java.util.Observable;
import java.util.Observer;

public class View implements Observer {
    private ViewModel viewModel;
    private TelegramLongPollingBot longPollingBot;

    @Override
    public void update(Observable o, Object arg) {
        viewModel = (ViewModel) o;
        longPollingBot = viewModel.getLongPollingBot();
        String gotMessage = viewModel.getHandleMessage();

        String inlineId = viewModel.getInlineId();
        String callbackId = viewModel.getCallbackId();
        Integer messageId = viewModel.getMessageId();
        ContractUser contractUser = viewModel.getContractUser();
        Integer chatId = viewModel.getChatId();
        User fromUser = viewModel.getFromUser();

        if (inlineId != null && fromUser != null) {
            handleInlineMessage(inlineId, fromUser);
        }
    }

    private void handleInlineMessage(String inlineId, User fromUser) {
        viewModel.updateMention(fromUser);

        AnswerInlineQuery aiq = new AnswerInlineQuery();
        InlineQueryResultArticle result = new InlineQueryResultArticle();
        InputTextMessageContent content = new InputTextMessageContent();

        content
                .setMessageText("Фриланс площадка для лабораторных работ.\n" +
                        "Если Вам нужна лабораторная, хотите лучше разобраться\n" +
                        "в учебных материалах и заработать на этом - значит Вам сюда.\n" +
                        "@MakeLabs_bot");

        result
                .setId("my id")
                .setDescription("Мы сделаем Ваши рутинные задания!")
                .setInputMessageContent(content)
                .setTitle("Лабораторные? Самостоятельные? Вам сюда!");
        aiq
                .setPersonal(true)
                .setInlineQueryId(inlineId)
                .setResults(result);

        try {
            longPollingBot.execute(aiq);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
