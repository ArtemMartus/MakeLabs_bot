package main.maincode.makelabs_bot.view;

import main.maincode.makelabs_bot.data.PostWorkData;
import org.glassfish.grizzly.utils.Pair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class InlineKeyboardManager implements MessageHandler {
    private PostWorkData workData;
    private InlineKeyboardMarkup keyboardMarkup;

    public InlineKeyboardManager(PostWorkData workData) {
        this.workData = workData;
    }

    public InlineKeyboardMarkup getMarkup() {
        if (keyboardMarkup == null)
            handle();
        return keyboardMarkup;
    }

    public PostWorkData getWorkData() {
        return workData;
    }

    public void updateData(PostWorkData workData) {

        if (this.keyboardMarkup != null
                && this.workData == workData)
            return;

        this.workData = workData;
        if (workData == null)
            return;

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> layout = new ArrayList<>();

        List<InlineKeyboardButton> row = new LinkedList<>();

        List<Pair<String, Integer>> data = workData.getParams();

        //Log.Info("Found " + data.size() + " buttons for " + user.getState(), Log.VERBOSE);

        int buttons = data.size();
        //final int chars_in_a_row = 62; //desktop
        final int chars_in_a_row = 48;  //mobile
        final int columns = 3;

//        Log.Info("Buttons data before sort ");
//        for (Pair<String, Integer> pair : data)
//            Log.Info("\t" + pair.getFirst() + " = " + pair.getSecond());

        data.sort((Comparator.comparingInt(o -> o.getFirst().length())));

//        Log.Info("Buttons data after sort ");
//        for (Pair<String, Integer> pair : data)
//            Log.Info("\t" + pair.getFirst() + " = " + pair.getSecond());

        List<InlineKeyboardButton> appendToTheEndButtons = new LinkedList<>();

        while (buttons > 0) {
            for (int i = 0, cch = chars_in_a_row; i < columns && buttons > 0; ++i, buttons--) {

                int current_id = data.size() - buttons;
                String buttonText = data.get(current_id).getFirst();
                int price = data.get(current_id).getSecond();

                cch -= buttonText.length();
//                Log.Info("Adding " + buttonText + " to layout", Log.VERBOSE);

                if (price == -99) {
                    appendToTheEndButtons.add(
                            new InlineKeyboardButton(buttonText).setCallbackData(buttonText)
                    );
                } else {
                    row.add(
                            new InlineKeyboardButton(buttonText).setCallbackData(buttonText)
                    );
                }


                if (cch <= 0 && row.size() >= 1 ||
                        (current_id + 1 < data.size() &&
                                cch < data.get(current_id + 1).getFirst().length())) {
                    layout.add(row);
                    row = new LinkedList<>();
                    //buttonText = shortenName(buttonText);
                }

            }
            if (row.size() > 0) {
                layout.add(row);
                row = new LinkedList<>();
            }
        }

        layout.add(appendToTheEndButtons);

        keyboardMarkup = markup.setKeyboard(layout);
    }

    @Override
    public void handle() {
        updateData(workData);
    }

    @Override
    public boolean isValid() {
        return workData != null
                && workData.isValid();
    }
}
