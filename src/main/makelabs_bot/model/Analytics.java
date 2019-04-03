/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.model;

import main.makelabs_bot.controllers.MakeLabs_bot;
import main.makelabs_bot.data.PostWorkData;
import main.makelabs_bot.helper.Log;
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
    private MakeLabs_bot makeLabs_bot;
    private Map<User, Integer> answeredQueriesTo = new HashMap<>();
    private Map<User, Integer> sentMessagesTo = new HashMap<>();
    private Map<User, Integer> editedMessagesTo = new HashMap<>();
    private Map<User, Integer> callbacksAnsweredTo = new HashMap<>();
    private Map<PostWorkData, Map<User, Integer>> postWorkDataRequested = new HashMap<>();
    private Map<String, Map<String, Integer>> postWorkDataStatus = new HashMap<>();


    public static Analytics getInstance() {
        if (mInstance == null) {
            synchronized (Analytics.class) {
                mInstance = new Analytics();
                mInstance.lastunixtime = Calendar.getInstance().getTime().getTime() / 1000;
            }
        }
        return mInstance;
    }

    public MakeLabs_bot getMakeLabs_bot() {
        return makeLabs_bot;
    }

    public void setMakeLabs_bot(MakeLabs_bot makeLabs_bot) {
        this.makeLabs_bot = makeLabs_bot;
    }

    public static String getTime(Long unixtime) {
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = new Date(unixtime * 1000);
        return df.format(today);
    }

    public synchronized void checkTime() {
        final int second = 1;
        final int minute = second * 60;

        Long current = Calendar.getInstance().getTimeInMillis() / 1000L;
        //System.out.println("Check time \n\t"+current+"\n\t"+lastunixtime);
        if (current - lastunixtime > minute * 15) {
            saveCurrent(current);
        }
    }

    private String shortenString(String str) {
        String out = str;
        if (str.length() > 24)
            out = out.substring(0, 16) + "..." + out.substring(out.length() - 4);
        return out;
    }

    private Integer fillInUserMap(Map<User, Integer> map, User toUser) {
        Integer lastKey = map.get(toUser);
        lastKey = lastKey == null ? 1 : ++lastKey;
        map.put(toUser, lastKey);

        return lastKey;
    }

    public void saveCurrent(Long unixtimestamp) {
        Log.Info("\n", Log.ANALYTICS);
        Log.Info("From  ["
                + getTime(lastunixtime)
                + "] -->(till)--> ["
                + getTime(unixtimestamp)
                + "]", Log.ANALYTICS);

        Log.Info("\tAnswered user's queries", Log.ANALYTICS);
        for (Map.Entry<User, Integer> entry : answeredQueriesTo.entrySet()) {
            Log.Info(userDataString(
                    entry.getKey())
                    + "\t--\t--\t answered his queries "
                    + entry.getValue()
                    + " times", Log.ANALYTICS);
        }
        Log.Info("\tSent messages to user", Log.ANALYTICS);
        for (Map.Entry<User, Integer> entry : sentMessagesTo.entrySet()) {
            Log.Info(userDataString(
                    entry.getKey())
                    + "\t--\t--\t sent "
                    + entry.getValue()
                    + " messages to user", Log.ANALYTICS);
        }
        Log.Info("\tEdited messages for user", Log.ANALYTICS);
        for (Map.Entry<User, Integer> entry : editedMessagesTo.entrySet()) {
            Log.Info(userDataString(
                    entry.getKey())
                    + "\t--\t--\t edited "
                    + entry.getValue()
                    + " messages with user", Log.ANALYTICS);
        }
        Log.Info("\tAnswered callbacks from user", Log.ANALYTICS);
        for (Map.Entry<User, Integer> entry : callbacksAnsweredTo.entrySet()) {
            Log.Info(userDataString(
                    entry.getKey())
                    + "\t--\t--\t answered "
                    + entry.getValue()
                    + " callbacks from user", Log.ANALYTICS);
        }
        Log.Info("\tUser visited URI", Log.ANALYTICS);
        for (Map.Entry<PostWorkData, Map<User, Integer>> entry : postWorkDataRequested.entrySet()) {
            for (Map.Entry<User, Integer> subentry : entry.getValue().entrySet()) {
                Log.Info(userDataString(subentry.getKey())
                        + " visited URI:'"
                        + entry.getKey().getIURI()
                        + "'\t=\t("
                        + shortenString(entry.getKey().getDescription())
                        + ")\t--\t--\t was requested by user\t"
                        + subentry.getValue()
                        + " times", Log.ANALYTICS);
            }
        }
        Log.Info("\tThe most common postWorkData status with price", Log.ANALYTICS);
        for (Map.Entry<String, Map<String, Integer>> entry : postWorkDataStatus.entrySet()) {
            for (Map.Entry<String, Integer> subentry : entry.getValue().entrySet()) {
                Log.Info("\t\t\"" +/*shortenString*/(entry.getKey())
                        + "\" got status \""
                        + subentry.getKey()
                        + "\"\t\t"
                        + subentry.getValue()
                        + " times", Log.ANALYTICS);
            }
        }

        postWorkDataRequested.clear();
        postWorkDataStatus.clear();
        callbacksAnsweredTo.clear();
        sentMessagesTo.clear();
        editedMessagesTo.clear();
        answeredQueriesTo.clear();
        lastunixtime = unixtimestamp;

        Log.Info("Stats end --------", Log.ANALYTICS);
    }

    public void updateAnsweredInlineQueries(User toUser) {
        if (toUser == null) return;
        Integer lastKey = fillInUserMap(answeredQueriesTo, toUser);
        Log.Info(userDataString(toUser)
                + "\t\tanswered "
                + lastKey
                + " queries already", Log.EVERYTHING);
    }

    public void updateSentMessages(User toUser) {
        if (toUser == null) return;
        Integer lastKey = fillInUserMap(sentMessagesTo, toUser);
        Log.Info(userDataString(toUser)
                + "\t\tgot "
                + lastKey
                + " messages already", Log.EVERYTHING);
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

    public void updateCallbackAnswered(User toUser) {
        if (toUser == null) return;
        Integer lastKey = fillInUserMap(callbacksAnsweredTo, toUser);
        Log.Info(userDataString(toUser)
                + "\t\tgot "
                + lastKey
                + " callback answers already", Log.EVERYTHING);

    }

    public void updatePostWorkDataRequested(PostWorkData postWorkData, User userRequested) {
        if (postWorkData == null) return;
        Map<User, Integer> dataPairRequested = postWorkDataRequested.get(postWorkData);
        if (dataPairRequested == null) {
            dataPairRequested = new HashMap<>();
            dataPairRequested.put(userRequested, 1);
        } else {
            Integer times = dataPairRequested.get(userRequested);
            times = times == null ? 1 : ++times;
            dataPairRequested.put(userRequested, times);
        }

        postWorkDataRequested.put(postWorkData, dataPairRequested);

        Log.Info(shortenString(postWorkData.getDescription())
                + " requested by "
                + userRequested.getUserName()
                + "["
                + userRequested.getId()
                + "]", Log.EVERYTHING);
    }

    public void updateEditedMessages(User toUser) {
        if (toUser == null) return;
        Integer lastKey = fillInUserMap(editedMessagesTo, toUser);
        Log.Info(userDataString(toUser)
                + "\t\tedited "
                + lastKey
                + " messages to him already", Log.EVERYTHING);
    }

    public void updatePostWorkDataStatus(String dataURI, String statusPlusPrice) {
        if (dataURI == null || dataURI.isEmpty()) return;
        Integer times = 1;
        Map<String, Integer> dataPairRequested = postWorkDataStatus.get(dataURI);
        if (dataPairRequested == null) {
            dataPairRequested = new HashMap<>();
            dataPairRequested.put(statusPlusPrice, times);
        } else {
            times = dataPairRequested.get(statusPlusPrice);
            times = times == null ? 1 : ++times;
            dataPairRequested.put(statusPlusPrice, times);
        }

        postWorkDataStatus.put(dataURI, dataPairRequested);

        Log.Info(shortenString(dataURI)
                + " status plus price -  "
                + statusPlusPrice
                + " - "
                + times + " items", Log.EVERYTHING);
    }
}
