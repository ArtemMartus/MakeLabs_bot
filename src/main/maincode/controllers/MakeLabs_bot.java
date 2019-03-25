package maincode.controllers;

import maincode.data.Contract;
import maincode.data.ContractUser;
import maincode.data.DataClass;
import maincode.data.PostWorkData;
import maincode.helper.Log;
import maincode.model.Analytics;
import maincode.model.Model;
import maincode.view.View;
import maincode.viewmodel.ViewModel;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MakeLabs_bot extends TelegramLongPollingBot {

    private final Model model;
    private final View view;
    private final ViewModel viewModel;
    private final DataClass dataClass;
    private final Analytics analytics;
    private HashMap<String, PostWorkData> dataset;

    public MakeLabs_bot(DataClass dataClass) {
        model = new Model();
        viewModel = new ViewModel(model);
        view = new View();
        viewModel.addObserver(view);

        this.dataClass = dataClass;
        dataset = new HashMap<>();
        analytics = Analytics.getInstance();
        analytics.setMakeLabs_bot(this);

        /*PostWorkController.loadWork();

        dataset.put("/", PostWorkController.getData("/"));
        dataset.put("/Сделать заказ", PostWorkController.getData("/Сделать заказ"));
*/
    }


    public Message sendMessage(String text, Long chatId, User toUser) {
        analytics.updateSentMessages(toUser);

        SendMessage sendMessage = new SendMessage(chatId, text);
        Message message = null;
        try {
            message = execute(sendMessage);
        } catch (Exception ex) {
            Log.Info("Exception in Send:" + ex.getMessage());
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
            ex.printStackTrace();
        }
        return result;
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
            usr = new ContractUser(userId, username, firstname);
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
        return null;

//        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> layout = new ArrayList<>();
//
//        List<InlineKeyboardButton> row = new LinkedList<>();
//        PostWorkData workData = dataset.get(user.getState());
//        if (workData == null) {
//            workData = PostWorkController.getData(user.getState());
//            dataset.put(user.getState(), workData);
//        }
//        List<Pair<String, Integer>> data = workData.getParams();
//
//        Log.Info("Found " + data.size() + " buttons for " + user.getState(), Log.VERBOSE);
//
//        int buttons = data.size();
//        //final int chars_in_a_row = 62; //desktop
//        final int chars_in_a_row = 48;  //mobile
//        final int columns = 3;
//
//        Log.Info("Buttons maincode.maincode.data before sort ");
//        for (Pair<String, Integer> pair : data)
//            Log.Info("\t" + pair.getFirst() + " = " + pair.getSecond());
//
//        data.sort((Comparator.comparingInt(o -> o.getFirst().length())));
//
//        Log.Info("Buttons maincode.maincode.data after sort ");
//        for (Pair<String, Integer> pair : data)
//            Log.Info("\t" + pair.getFirst() + " = " + pair.getSecond());
//
//        List<InlineKeyboardButton> appendToTheEndButtons = new LinkedList<>();
//
//        while (buttons > 0) {
//            for (int i = 0, cch = chars_in_a_row; i < columns && buttons > 0; ++i, buttons--) {
//
//                int current_id = data.size() - buttons;
//                String buttonText = data.get(current_id).getFirst();
//                int price = data.get(current_id).getSecond();
//
//                cch -= buttonText.length();
//                Log.Info("Adding " + buttonText + " to layout", Log.VERBOSE);
//
//                if (price == -99) {
//                    appendToTheEndButtons.add(
//                            new InlineKeyboardButton(buttonText).setCallbackData(buttonText)
//                    );
//                } else {
//                    row.add(
//                            new InlineKeyboardButton(buttonText).setCallbackData(buttonText)
//                    );
//                }
//
//
//                if (cch <= 0 && row.size() >= 1 ||
//                        (current_id + 1 < data.size() &&
//                                cch < data.get(current_id + 1).getFirst().length())) {
//                    layout.add(row);
//                    row = new LinkedList<>();
//                    //buttonText = shortenName(buttonText);
//                }
//
//            }
//            if (row.size() > 0) {
//                layout.add(row);
//                row = new LinkedList<>();
//            }
//        }
//
//        /*
//        for (InButton button : maincode.maincode.data) {
//            Log.Info("Adding " + button.getText() + "(" + button.getCode() + ") to layout", Log.VERBOSE);
//            String buttonText = button.getText();
//            if (buttonText.length() > chars)
//                buttonText = shortenName(buttonText);
//            row.add(
//                    new InlineKeyboardButton(buttonText).setCallbackData(button.getCode())
//            );
//        }*/
//
//        layout.add(appendToTheEndButtons);
//
//        return markup.setKeyboard(layout);
    }

    private int testMessageId(Integer uid, Long chatId, int messageId) {
        EditMessageText e = new EditMessageText();
        e
                .setChatId(chatId)
                .setMessageId(messageId)
                .setText("...");
        try {
            Message ret = (Message) execute(e);
            if (ret.getText().equals(e.getText()))
                return messageId;
        } catch (Exception ex) {
            Log.Info("Exception in testMessageId:" + ex.getMessage());
            //ex.printStackTrace();
            dataClass.setMessageId(uid, 0);
            return 0;
        }
        return messageId;
    }

    private int getMessageId(Integer uid, Long chatId) {
        int mid = dataClass.getMessageId(uid);
        if (mid == 0) {
            SendMessage s = new SendMessage(chatId, ".");
            try {
                mid = execute(s).getMessageId();
            } catch (Exception ex) {
                Log.Info("Exception in getMessageId:" + ex.getMessage());
                //e.printStackTrace();
                return 0;
            }
            dataClass.setMessageId(uid, mid);
        }
        return testMessageId(uid, chatId, mid);
    }

    private void Send(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId, text);
        try {
            execute(sendMessage);
        } catch (Exception ex) {
            Log.Info("Exception in Send:" + ex.getMessage());
            //ex.printStackTrace();
        }
    }

    public Boolean sendCallbackAnswer(String queryID, String caption, User toUser) {
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

    private String getCheckoutText(Contract contract, PostWorkData data) {
//        int overall_price = 0;
//        contract.setName(data.getDescription());
//        StringBuilder editedText = new StringBuilder(contract.getName());
//        editedText.append("\n");
//        for (Pair<String, Integer> pair : data.getParams()) {
//            String name = pair.getFirst();
//            boolean checked = contract.isSet(name);
//            int price = pair.getSecond();
//            if (price < 0)
//                continue; // it is not actual payment related button. like 'back' button or so
//            editedText.append(name).append(" ").append(price).append("₴");
//            if (checked) {
//                overall_price += price;
//                editedText.append("\t\t✅ ");
//            } else {
//                editedText.append("\t\t❌ ");
//            }
//            editedText.append("\n");
//        }
//        if (overall_price > 0)
//            editedText.append("\nИтого:\t").append(overall_price).append("₴");
//        contract.setPrice(overall_price);
//        return editedText.toString();
        return null;
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

    private String getDate() {
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = Calendar.getInstance().getTime();
        return df.format(today);
    }

    void answerInline(Update update) {

//        String id = update.getInlineQuery().getId();
//        if (id == null || id.isEmpty()) {
//            Log.Info("Got a strange inline query without an id");
//            return;
//        } else
//            Log.Info("Inline query " + id);
//        Log.Info("Someone is mentioning this bot inline " + getDate(), Log.MAIN);
//
//        User fromUser = update.getInlineQuery().getFrom();
//        if (fromUser != null)
//            Log.Info(userDataString(fromUser), Log.EXTENDED);
//
//        AnswerInlineQuery aiq = new AnswerInlineQuery();
//        InlineQueryResultArticle result = new InlineQueryResultArticle();
//        InputTextMessageContent content = new InputTextMessageContent();
//
//        content
//                .setMessageText("Фриланс площадка для лабораторных работ.\n" +
//                        "Если Вам нужна лабораторная, хотите лучше разобраться\n" +
//                        "в учебных материалах и заработать на этом - значит Вам сюда.\n" +
//                        "@MakeLabs_bot");
//
//        result
//                .setId(id)
//                .setDescription("Мы сделаем Ваши рутинные задания!")
//                .setInputMessageContent(content)
//                .setTitle("Лабораторные? Самостоятельные? Вам сюда!");
//        aiq
//                .setPersonal(true)
//                .setInlineQueryId(id)
//                .setResults(result);
//
//        try {
//            execute(aiq);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    @Override
    public void onUpdateReceived(Update update) {

        viewModel.setUpdate(update);


        /*if (update.hasInlineQuery()) {
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

        int messageId = testMessageId(user.getId(), chatId, user.getMessageId());

        if (messageId == 0) {
            messageId = getMessageId(uid, chatId);
            user.setMessageId(messageId);
        }

        if (messageId == 0) {
            Log.Info("messageId==0. Continuing...");
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

        boolean handledCommand = false;

        if (update.hasMessage() && update.getMessage().hasText()) {
            String gotMessage = update.getMessage().getText();
            Log.Info("He writes: " + gotMessage, Log.EXTENDED);
            if (gotMessage.equals("/start")) {
                dataClass.setMessageId(uid, 0);
                messageId = getMessageId(uid, chatId);
                user.setMessageId(messageId);
                user.setState("/");
                handledCommand = true;
            }
        }


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

        String command = getLastName(text);

        String editedText = null;

        PostWorkData data = dataset.get(validText);
        if (data == null) {
            data = PostWorkController.getData(validText);
            dataset.put(validText, data);
        }


        if (!validText.equals(text)) {
            switch (command) {
                case "Сотрудничество": {
                    Send("Присоединяйтесь к комманде Make Labs\n" +
                            "Если Вы срочно хотите заработать денег\n" +
                            "и способны выполнять лабораторные работы\n" +
                            "пишите этому боту @MakeLabsJob_bot\n" +
                            "", chatId);
                    handledCommand = true;
                    break;
                }
                case "О нас": {
                    Send("Make Labs это бот-помошник созданный с целью\n" +
                            "избавить студентов от рутинных заданий\n" +
                            "чтобы Вы могли заниматься любимыми делами\n" +
                            "не переживая о незданных самостоятельных работах\n" +
                            "Telegram: @upsage", chatId);
                    handledCommand = true;
                    break;
                }// TODO make /help command works
                //TODO make contract store date of applying, boolean isPurchased, date of purchase, date start processing, date start reviewing, date give off to client
                //TODO make more complex status handling with actual status plus actual status date
                //TODO make ability to choose whether the work should be made just to pass 60% with 0.75 price in 5 days or \n
                // made normally for regular price in 5 days or made finest quality in 5 days for double price\n
                // or made in 1 day with fines quality with 4 times price
                //Possibly last one to-do should use new uri like /users_database/123_dir/123abc_dir/checkout  and be a copy of sampled layout with changed prices

                //TODO start planning Jobs bot
                //we can handle new jobs by asking employees for pdf task details and writing our own prices. It would be the safest way
                //TODO make sales counting for each postWorkData
                case "Мои заказы": {
                    if (user.getContracts().size() > 0) {
                        for (Contract contract : user.getContracts()) {
                            Send(contract.toString(), chatId);
                        }

                        dataClass.setMessageId(uid, 0);
                        messageId = getMessageId(uid, chatId);
                        user.setMessageId(messageId);
                        validText = text = "/";
                        command = "";
                        user.setState(text);
                        data = dataset.get(text);

                    } else {
                        if (query != null) {
                            alreadySendCallbackAnswer = true;
                            sendCallbackAnswer(query, "У Вас отсутствуют активные заказы");
                        } else
                            Send("У Вас отсутствуют активные заказы", chatId);
                    }
                    handledCommand = true;
                    break;
                }
                case "Назад": {
                    user.goBack();
                    validText = user.getState();
                    text = validText;
                    command = "";
                    data = dataset.get(user.getState());
                    handledCommand = true;
                    break;
                }
                case "Подтвердить": {
                    Contract contract = user.getUnAppliedContract();
                    contract.apply();
                    contract.writeTo(user.getId() + "_dir/" + contract.getHash());
                    validText = text = "/";
                    command = "";
                    user.setState(text);
                    data = dataset.get(text);
                    handledCommand = true;
                    break;
                }
                default: {

                    Log.Info("Got unhandled command: " + command);
                    //This is most possibly checkout form

                    Contract contract = user.getUnAppliedContract();


                    contract.setUpAllIncluding(data);

                    contract.toogle(command);

                    editedText = getCheckoutText(contract, data);

                    handledCommand = true;

                }

            }
        }

        if (data == null) {
            Log.Info("Some strange shit makes maincode.maincode.data set to null...");
            data = dataset.get(validText);
            if (data == null) {
                Log.Info("Oh, never mind. Stupid on-demand loading 'dataset' didn't have it");
                data = PostWorkController.getData(validText);
                dataset.put(validText, data);
            }
            if (data == null) {
                Log.Info("It is still null... Do PostWorkController has it??");
            }
        }


        if (!handledCommand && editedText == null) {
            boolean is_not_endpoint = true;
            for (Pair<String, Integer> pair : data.getParams()) {
                String str = validText;
                if (!str.equals("/"))
                    str += "/";
                str += pair.getFirst();
                if (pair.getSecond() > 0 && !PostWorkController.pathExists(str)) {
                    is_not_endpoint = false;
                    break;
                }
            }
            if (!is_not_endpoint) {// If we just loaded last branch show the recipe for unapply contract
                Contract contract = user.getUnAppliedContract();
                contract.setUpAllIncluding(data);
                editedText = getCheckoutText(contract, data);
            }
        }


        Log.Info("\tText = " + text + "\n\tCommand = " + command + "\n\tValid Text = " + validText);


        InlineKeyboardMarkup keyboardMarkup = getMarkup(user);


        if (editedText == null)
            editedText = data.getDescription();//getCheckoutText(maincode.maincode.data);

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

        user.save();*/
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
