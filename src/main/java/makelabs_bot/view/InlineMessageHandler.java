/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package makelabs_bot.view;

import helper.Log;
import makelabs_bot.model.Analytics;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

public class InlineMessageHandler implements MessageHandler {
    private final User toUser;
    private final String inlineId;

    public InlineMessageHandler(User toUser, String inlineId) {
        this.toUser = toUser;
        this.inlineId = inlineId;
    }

    @Override
    public void handle() {
        Log.Info("Inline message");

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

        if (Analytics.getInstance().getMakeLabs_bot().answerInlineQuery(aiq, toUser)) {
            Log.Info("Answered successfully");
        } else {
            Log.Info("Some issue with answering inline query");
        }

    }

    @Override
    public boolean isValid() {
        return toUser != null
                && inlineId != null
                && !inlineId.isEmpty();
    }
}
