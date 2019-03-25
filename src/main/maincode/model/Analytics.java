package maincode.model;

import maincode.helper.Log;
import org.telegram.telegrambots.meta.api.objects.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Analytics {
    private static Analytics mInstance;
    private Long lastunixtime = 0L;
    private int mentionedInline = 0;
    private Map<User, Integer> mentionedBy = new HashMap<>();


    public static Analytics getInstance() {
        if (mInstance == null) {
            synchronized (Analytics.class) {
                mInstance = new Analytics();
                mInstance.lastunixtime = Calendar.getInstance().getTime().getTime() / 1000;
            }
        }
        return mInstance;
    }

    public void updateMentions(User fromUser) {
        mentionedInline++;
        Integer lastKey = mentionedBy.get(fromUser);
        if (lastKey == null)
            lastKey = 1;
        else
            lastKey++;
        mentionedBy.put(fromUser, lastKey);
        Log.Info(userDataString(fromUser) + "\n\tMentioned " + lastKey + " times already", Log.EVERYTHING);
    }

    public void checkTime() {
        Long current = Calendar.getInstance().getTime().getTime() / 1000L;
        if (current - lastunixtime > 10L) {
            saveCurrent(current);
        }
    }

    public void saveCurrent(Long unixtimestamp) {
        Log.Info("Stats from  " + getDate(lastunixtime) + " till " + getDate(unixtimestamp) +
                "\n\tMentioned inline : " + mentionedInline);
        for (Map.Entry<User, Integer> entry : mentionedBy.entrySet()) {
            Log.Info(userDataString(entry.getKey()) + "\t--\t--\t" + entry.getValue() + " times");
        }

        mentionedBy.clear();
        mentionedInline = 0;
        lastunixtime = unixtimestamp;
    }

    private String getDate(Long time) {
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = new Date(time * 1000);
        return df.format(today);
    }


    private String userDataString(User user) {
        String str = "[" + user.getId() + ":";
        str += user.getUserName() + "]\t";
        if (user.getFirstName() != null)
            str += user.getFirstName();
        if (user.getLastName() != null)
            str += ", " + user.getLastName();
        return str;
    }


}
