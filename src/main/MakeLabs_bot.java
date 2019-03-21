package main;

import controllers.PostWorkController;
import data.Contract;
import data.ContractUser;
import data.Log;
import data.PostWorkData;
import org.glassfish.grizzly.utils.Pair;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static controllers.PostWorkController.getLastName;
import static controllers.PostWorkController.validifyPath;

public class MakeLabs_bot extends TelegramLongPollingBot {

    private final DataClass dataClass;
    private HashMap<String, PostWorkData> dataset;

    public MakeLabs_bot(DataClass dataClass) {
        this.dataClass = dataClass;
        dataset = new HashMap<>();

        PostWorkController.loadWork();

        dataset.put("/", PostWorkController.getData("/"));
        dataset.put("/Сделать заказ", PostWorkController.getData("/Сделать заказ"));

    }


    private ContractUser getUser(Update update) {
        Integer userId = getUserId(update);
        String username = "";
        String firstname = "";
        if (update.hasMessage()) {
            username = update.getMessage().getFrom().getUserName();
            firstname = update.getMessage().getFrom().getFirstName();
        }
        if (update.hasCallbackQuery()) {
            username = update.getCallbackQuery().getFrom().getUserName();
            firstname = update.getCallbackQuery().getFrom().getFirstName();
        }

        if (userId == 0) {
            Log.Info("userId == 0 Invalid update", Log.VERBOSE);
            return null;
        }
        ContractUser usr = dataClass.getUser(userId);
        if (usr == null) {
            List<Contract> list = new ArrayList<>();
            usr = new ContractUser(userId, username, firstname, list);
            dataClass.setUser(userId, usr);
        }
        return usr;
    }

    private Integer getUserId(Update update) {
        Integer userId = 0;
        if (update.hasMessage()) {
            userId = update.getMessage().getFrom().getId();
        }
        if (update.hasCallbackQuery()) {
            userId = update.getCallbackQuery().getFrom().getId();
        }
        return userId;
    }

    private Long getChatId(Update update) {
        Long chatId = 0L;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        }
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        return chatId;
    }

    private String shortenName(String name) {
        StringBuilder shortName = new StringBuilder();
        for (int i = 0; i < name.length(); ++i) {
            if (Character.isUpperCase(name.charAt(i)))
                shortName.append(name.charAt(i));
        }
        return shortName.toString();
    }

    private InlineKeyboardMarkup getMarkup(ContractUser user) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> layout = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<Pair<String, Integer>> data = dataset.get(user.getState()).getParams();

        Log.Info("Found " + data.size() + " buttons for " + user.getState(), Log.VERBOSE);

        int buttons = data.size();
        //final int chars_in_a_row = 62; //desktop
        final int chars_in_a_row = 48;  //mobile
        final int columns = 3;
        while (buttons > 0) {
            for (int i = 0, cch = chars_in_a_row; i < columns && buttons > 0; ++i, buttons--) {
                String buttonText = data.get(data.size() - buttons).getFirst();
                cch -= buttonText.length();
                Log.Info("Adding " + buttonText + " to layout", Log.VERBOSE);

                row.add(
                        new InlineKeyboardButton(buttonText).setCallbackData(buttonText)
                );
                //TODO make contracts
                //TODO make buttons even more beautiful
                //TODO make back button last in layout
                if ((cch <= 0 && row.size() >= 1)) {
                    layout.add(row);
                    row = new ArrayList<>();
                    //buttonText = shortenName(buttonText);
                }

            }
            layout.add(row);
            row = new ArrayList<>();
        }

        /*
        for (InButton button : data) {
            Log.Info("Adding " + button.getText() + "(" + button.getCode() + ") to layout", Log.VERBOSE);
            String buttonText = button.getText();
            if (buttonText.length() > chars)
                buttonText = shortenName(buttonText);
            row.add(
                    new InlineKeyboardButton(buttonText).setCallbackData(button.getCode())
            );
        }*/


        return markup.setKeyboard(layout);
    }

    private Integer getMessageId(Integer uid, Long chatId) {
        Integer mid = dataClass.getMessageId(uid);
        if (mid == null) {
            SendMessage s = new SendMessage(chatId, ".");
            try {
                mid = execute(s).getMessageId();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            dataClass.setMessageId(uid, mid);
        }
        EditMessageText e = new EditMessageText();
        e.setChatId(chatId).setMessageId(mid).setText("...");
        try {
            execute(e);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataClass.setMessageId(uid, null);
            return null;
        }
        return mid;
    }

    private void Send(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId, text);
        try {
            execute(sendMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendCallbackAnswer(CallbackQuery query, String caption) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(query.getId());
        if (!caption.isEmpty())
            answerCallbackQuery.setText(caption);
        try {
            execute(answerCallbackQuery);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getCheckoutText(Contract contract, PostWorkData data) {
        int overall_price = 0;
        contract.setName(data.getDescription());
        StringBuilder editedText = new StringBuilder(contract.getName());
        editedText.append("\n");
        for (Pair<String, Integer> pair : data.getParams()) {
            String name = pair.getFirst();
            boolean checked = contract.isSet(name);
            int price = pair.getSecond();
            if (price < 0)
                continue; // it is not actual payment related button. like 'back' button or so
            editedText.append(name).append(" ").append(price).append("₴");
            if (checked) {
                overall_price += price;
                editedText.append(" ✅ ");
            } else {
                editedText.append(" ❌ ");
            }
            editedText.append("\n");
        }
        editedText.append("\nИтого:\t").append(overall_price).append("₴");
        contract.setPrice(overall_price);
        return editedText.toString();
    }

    private String userDataString(User user) {
        String str = "UID:" + user.getId() + "\n";
        str += "Username:" + user.getUserName() + "\n";
        if (user.getFirstName() != null)
            str += "First name:" + user.getFirstName() + "\n";
        if (user.getLastName() != null)
            str += "Last name:" + user.getLastName() + "\n";
        str += "LANG:" + user.getLanguageCode() + "\n";
        str += "BOT:" + user.getBot() + "\n";
        str += "DATE:" + getDate() + "\n";
        return str;
    }

    private String getDate(Long unixtimestamp) {
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = new Date(unixtimestamp * 1000);
        return df.format(today);
    }

    private String getDate() {
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = Calendar.getInstance().getTime();
        return df.format(today);
    }

    void answerInline(Update update) {
        Log.Info("Someone is mentioning this bot inline " + getDate(), Log.MAIN);

        User fromUser = update.getInlineQuery().getFrom();
        if (fromUser != null)
            Log.Info(userDataString(fromUser), Log.EXTENDED);

        AnswerInlineQuery aiq = new AnswerInlineQuery();
        InlineQueryResultArticle result = new InlineQueryResultArticle();
        result
                .setDescription("Мы сделаем Ваши рутинные задания!")
                .setTitle("Лабораторные? Самостоятельные? Вам сюда!");
        aiq
                .setPersonal(true)
                .setInlineQueryId(update.getInlineQuery().getId())
                .setResults(result);

        try {
            execute(aiq);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Log.Info("Got an update " + update.getUpdateId() + "\n", Log.MAIN);


        if (update.hasInlineQuery()) {
            answerInline(update);
            return;
        }

        ContractUser user = getUser(update);

        if (user == null) {
            Log.Info("user==null. Continuing...");
            return;
        }

        Long chatId = getChatId(update);
        Integer uid = user.getId();

        Integer messageId = getMessageId(uid, chatId);
        if (messageId == null) {
            Log.Info("messageId==null. Continuing...");
            return;
        }


        User fromUser = null;
        if (update.hasMessage()) {
            fromUser = update.getMessage().getFrom();
        } else if (update.hasCallbackQuery()) {
            fromUser = update.getCallbackQuery().getFrom();
        }
        Log.Info("Someone sent message to this bot", Log.MAIN);

        if (fromUser != null)
            Log.Info(userDataString(fromUser), Log.EXTENDED);
        if (update.hasMessage() && update.getMessage().hasText())
            Log.Info("He writes: " + update.getMessage().getText(), Log.EXTENDED);


        CallbackQuery query = update.getCallbackQuery();
        boolean alreadySendCallbackAnswer = false;
        String text = "";
        if (query == null) {
            text = user.getState();

        } else {
            String data = query.getData();
            text = user.getState();
            if (!user.getState().equals("/"))
                text += "/";
            text += data;
            Log.Info("Current user uri = " + text);
        }

        String validText = validifyPath(text);

        user.setState(validText);
        if (dataset.get(validText) == null) {
            dataset.put(validText, PostWorkController.getData(validText));
        }

        String command = getLastName(text);

        PostWorkData data = dataset.get(validText);

        if (!validText.equals(text)) {
            switch (command) {
                case "Сотрудничество": {
                    Send("Присоединяйтесь к комманде Make Labs\n" +
                            "Если Вы срочно хотите заработать денег\n" +
                            "и способны выполнять лабораторные работы\n" +
                            "пишите этому боту @MakeLabsJob_bot\n" +
                            "", chatId);
                    break;
                }
                case "О нас": {
                    Send("Make Labs это бот-помошник созданный с целью\n" +
                            "избавить студентов от рутинных заданий\n" +
                            "чтобы Вы могли заниматься любимыми делами\n" +
                            "не переживая о незданных самостоятельных работах\n" +
                            "Telegram: @upsage", chatId);
                    break;
                }
                case "Мои заказы": {
                    if (user.getContracts().size() > 0) {
                        for (Contract contract : user.getContracts()) {
                            Send(contract.toString(), chatId);
                        }
                    } else {
                        if (query != null) {
                            alreadySendCallbackAnswer = true;
                            sendCallbackAnswer(query, "У Вас отсутствуют активные заказы");
                        } else
                            Send("У Вас отсутствуют активные заказы", chatId);
                    }
                    break;
                }
                case "Назад": {
                    user.goBack();
                    data = dataset.get(user.getState());
                    break;
                }
                default: {
                    Log.Info("Got unhandled command: " + command);
                }

            }
        }

        Log.Info("Looking at {" + text + "} where command=" + command + " and validText=" + validText);


        InlineKeyboardMarkup keyboardMarkup = getMarkup(user);
        String editedText = data.getDescription();//getCheckoutText(data);

        EditMessageText e = new EditMessageText();
        e
                .setChatId(chatId)
                .setMessageId(messageId)
                .setText(editedText)
                .setReplyMarkup(keyboardMarkup);
        try {
            execute(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!alreadySendCallbackAnswer && query != null) {
            sendCallbackAnswer(query, "");
        }
    }

    @Override
    public String getBotUsername() {
        return "MakeLabs_bot";
    }

    @Override
    public String getBotToken() {
        return "847705016:AAE0Fds6WiSEJsB-eQr9X-IoAj3fnKXxlFo";
    }
}
