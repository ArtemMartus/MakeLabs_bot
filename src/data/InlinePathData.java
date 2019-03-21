package data;

import main.MyClass;

import java.util.ArrayList;
import java.util.List;

public class InlinePathData {
    String description;
    List<InButton> buttons;

    public InlinePathData() {
        buttons = new ArrayList<>();
        buttons.add(new InButton("back", "Назад"));
    }

    public InlinePathData(String description) {
        super();
        this.description = description;
    }

    public InlinePathData(String description, List<InButton> buttons) {
        this.description = description;
        this.buttons = buttons;
    }

    public static final String remLast(String str) {
        if (str.length() == 1 || str.indexOf("/", 0) == -1)
            return str;
        int slash = str.lastIndexOf("/");
        if (slash == 0)
            return "/";
        return str.substring(0, slash);
    }

    public InButton handleButton(String code) {
        if (buttons == null || buttons.size() == 0)
            return null;
        for (InButton btn : buttons) {
            String[] ptrs = MyClass.decodeCommand(btn.code);
            Log.Info("\tDEBUG\tChecking if " + ptrs[0] + " equals to " + code, Log.EXTENDED);
            if (ptrs[0].equals(code)) {
                Log.Info("\tDEBUG\tYes!", Log.EXTENDED);
                return btn;
            }
            Log.Info("\tDEBUG\tNope", Log.EXTENDED);
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<InButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<InButton> buttons) {
        this.buttons = buttons;
    }
}
