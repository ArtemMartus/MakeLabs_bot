package main;

import data.*;
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

import static data.InlinePathData.remLast;

public class MyClass extends TelegramLongPollingBot {

    private final DataClass dataClass;
    private HashMap<String, InlinePathData> dataset;

    public MyClass(DataClass dataClass) {
        this.dataClass = dataClass;
        dataset = new HashMap<>();
        dataset.put("/", new InlinePathData("Домашняя страница" +
                "\nЗдесь вы можете посмотреть статус, сделать новый заказ" +
                "\nИ связаться с нами",
                Arrays.asList(
                        new InButton("orders", "Мои заказы"),
                        new InButton("make", "Сделать заказ"),
                        new InButton("support", "О нас")
                )));
        dataset.put("/make", new InlinePathData("Выберете предмет:",
                Arrays.asList(
                        new InButton("aip", "АиП"),
                        new InButton("back", "<-")
                )));
        dataset.put("/make/aip", new InlinePathData("Алгоритмизация и Программирование\nВыберете работу:",
                Arrays.asList(
                        new InButton("sr1", "Ср1"),
                        new InButton("sr2", "Ср2"),
                        new InButton("back", "<-")
                )));

        dataset.put("/make/aip/sr1", new InlinePathData("Самостоятельная работа 1\nВыберете нужное:",
                Arrays.asList(
                        new InButton("code:65:true", "Код"),
                        new InButton("sa+mpz:85:true", "Схема Алгоритма и Математическая Постановка Задачи"),
                        new InButton("tests:35:true", "Тесты"),
                        new InButton("comments:120:true", "Комментарии"),
                        new InButton("apply", "Готово"),
                        new InButton("back", "<-")
                )));
        dataset.put("/make/aip/sr2", new InlinePathData("Самостоятельная работа 2\nВыберете нужное:",
                Arrays.asList(
                        new InButton("code:65:true", "Код"),
                        new InButton("sa+mpz:85:true", "Схема Алгоритма и Математическая Постановка Задачи"),
                        new InButton("tests:35:true", "Тесты"),
                        new InButton("comments:120:true", "Комментарии"),
                        new InButton("apply", "Готово"),
                        new InButton("back", "<-")
                )));

    }

    public static String getLastName(String path) {
        int lastSlash = path.lastIndexOf("/");
        return path.substring(lastSlash + 1, path.length());
    }

    public static String[] decodeCommand(String command) {
        return command.split(":");
    }

    public static String encodeIntoCommand(List<Object> data) {
        StringBuilder sb = new StringBuilder();
        for (Object o : data)
            sb.append(o.toString()).append(":");
        return sb.substring(0, sb.length() - 1);
    }

    private String validifyPath(String path) {
        if (dataset.get(path) != null)
            return path;
        if (dataset.get(remLast(path)) != null)
            return remLast(path);
        Log.Info("validifyPath returns home url");
        return "/";
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
            usr = new ContractUser(username, firstname, list);
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
        List<InButton> data = dataset.get(validifyPath(user.getState())).getButtons();
        Log.Info("Found " + data.size() + " buttons for " + user.getState(), Log.VERBOSE);
        int chars = 55 / data.size();
        for (InButton button : data) {
            Log.Info("Adding " + button.getText() + "(" + button.getCode() + ") to layout", Log.VERBOSE);
            String buttonText = button.getText();
            if (buttonText.length() > chars)
                buttonText = shortenName(buttonText);
            row.add(
                    new InlineKeyboardButton(buttonText).setCallbackData(button.getCode())
            );
        }

        layout.add(row);

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

    private String getCheckoutText(InlinePathData data) {
        int overall_price = 0;
        StringBuilder editedText = new StringBuilder(data.getDescription());
        editedText.append("\n");
        boolean add_price = false;
        for (InButton c : data.getButtons()) {
            String[] c_ptrs = decodeCommand(c.getCode());
            if (c_ptrs.length <= 2) {
                Log.Info("Stepped onto " + c.getText() + " continuing getCheckoutText cycle");
                continue;
            }
            add_price = true;
            int c_price = Integer.parseInt(c_ptrs[1]);
            boolean c_checked = Boolean.parseBoolean(c_ptrs[2]);
            editedText.append(c.getText()).append(" ").append(c_price).append("₴");
            if (c_checked) {
                overall_price += c_price;
                editedText.append(" ✅ ");
            } else {
                editedText.append(" ❌ ");
            }
            editedText.append("\n");
        }
        if (add_price)
            editedText.append("\nИтого:\t").append(overall_price).append("₴");
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
        Integer uid = getUserId(update);
        Long chatId = getChatId(update);
        ContractUser user = getUser(update);
        if (user == null) {
            Log.Info("user==null. Continuing...");
            return;
        }

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
            Log.Info("He writes: " + update.getMessage().getText() + "\n", Log.EXTENDED);

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
            Log.Info("Current user url = " + text);
        }

        String validText = validifyPath(text);

        user.setState(validText);


        String command = getLastName(text);

        InlinePathData data = dataset.get(validText);

        if (!validText.equals(text)) {
            switch (command) {
                case "support": {
                    Send("Make Labs это бот-помошник созданный с целью\n" +
                            "избавить студентов от рутинных заданий\n" +
                            "чтобы Вы могли заниматься любимыми делами\n" +
                            "не переживая о незданных самостоятельных работах\n" +
                            "", chatId);
                }
                case "orders": {
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
                }
                break;
                case "back": {
                    user.goBack();
                    data = dataset.get(user.getState());
                }
                break;
                default: {
                    String[] ptrs = decodeCommand(command);
                    if (ptrs.length == 3) {
                        InButton button = data.handleButton(ptrs[0]);
                        if (button == null) {
                            Log.Info("Couldn't find button " + ptrs[0] + " under " + validText);
                            text = command = "/";
                            user.setState(text);
                            data = dataset.get(user.getState());
                        } else {
                            try {

                                Integer price = Integer.parseInt(ptrs[1]);
                                boolean checked = Boolean.parseBoolean(ptrs[2]);
                                checked = !checked;
                                button.setCode(encodeIntoCommand(Arrays.asList(ptrs[0], price, checked)));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        Log.Info("Looking at {" + text + "} where command=" + command + " and validText=" + validText);


        InlineKeyboardMarkup keyboardMarkup = getMarkup(user);
        String editedText = getCheckoutText(data);

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
