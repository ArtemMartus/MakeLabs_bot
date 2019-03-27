package maincode.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;

public class Log {
    public static final int MAIN = 90;
    public static final int EXTENDED = 75;
    public static final int DEBUG = 50;
    public static final int VERBOSE = 25;
    public static final int EVERYTHING = 10;
    public static final int ANALYTICS = 65;
    private static int level;
    private static Long logStart = -1L;

    private static void setLogStart() {
        if (logStart > 0)
            return;
        logStart = Calendar.getInstance().getTimeInMillis() / 1000L;
    }


    public static void Info(String str) {
        Info(str, DEBUG);
    }

    public static void Info(String str, int level) {
        setLogStart();
        if (level == ANALYTICS) {
            str = "[Stats]" + str;
            try {
                File logsDir = new File("./logs");
                if (logsDir.mkdir() || (logsDir.exists() && logsDir.isDirectory()))
                    Files.write(Paths.get("./logs/" + logStart), str.getBytes());
                else
                    System.err.println("Could not open " + logsDir.getAbsolutePath() + " directory.\nOmitting logging");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (level >= Log.level) {
            System.out.println(str);
        }
    }

    public static void setShowLevel(int level) {
        setLogStart();
        Log.level = level;
    }
}
