/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Log {
    public static final int MAIN = 90;
    public static final int EXTENDED = 75;
    public static final int DEBUG = 50;
    public static final int VERBOSE = 25;
    public static final int EVERYTHING = 10;
    public static final int ANALYTICS = 65;
    public static final int PAYMENT_SERVICE = 66;
    public static final int DATABASE_MANAGER = 67;
    public static final int MODEL = 68;
    private static int level;
    private static String logStart;

    private static void setLogStart() {
        if (logStart != null)
            return;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss z");
        logStart = sdf.format(new Date()) + ".txt";
    }


    public static void Info(String str) {
        Info(str, DEBUG);
    }

    public synchronized static void Info(String str, int level) {
        setLogStart();
        if (str.length() > 2) {
            str = "\t" + str;
            switch (level) {
                case ANALYTICS:
                    str = "[ANALYTICS]\t\t" + str;
                    break;
                case MAIN:
                    str = "[MAIN]\t\t\t" + str;
                    break;
                case EXTENDED:
                    str = "[EXTENDED]\t\t" + str;
                    break;
                case DEBUG:
                    str = "[DEBUG]\t\t\t" + str;
                    break;
                case VERBOSE:
                    str = "[VERBOSE]\t\t" + str;
                    break;
                case EVERYTHING:
                    str = "[EVERYTHING]\t" + str;
                    break;
                case PAYMENT_SERVICE:
                    str = "[PAYMENT_SERVICE]\t" + str;
                    break;
                case DATABASE_MANAGER:
                    str = "[DATABASE_MANAGER]\t" + str;
                    break;
                case MODEL:
                    str = "[MODEL]\t\t\t" + str;
                    break;
                default:
            }
        }
        if (level >= Log.level) {
            System.out.println(str);
        }
        try {
            File logsDir = new File("./logs");
            if (logsDir.mkdir() || (logsDir.exists() && logsDir.isDirectory())) {

                String logFileName = "./logs/" + logStart;
                File logFile = new File(logFileName);
                boolean newFile = logFile.createNewFile();
                // new file created if newFile == true
                String logFileData = str + "\n\r";
                Files.write(Paths.get(logFileName), logFileData.getBytes(), StandardOpenOption.APPEND);
            } else
                System.err.println("Could not open " + logsDir.getAbsolutePath() + " directory.\nOmitting logging");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setShowLevel(int level) {
        setLogStart();
        Log.level = level;
    }

    public static String getLogFile() {
        return logStart;
    }
}
