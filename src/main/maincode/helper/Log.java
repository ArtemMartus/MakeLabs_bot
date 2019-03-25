package maincode.helper;

public class Log {
    public static final int MAIN = 90;
    public static final int EXTENDED = 75;
    public static final int DEBUG = 50;
    public static final int VERBOSE = 25;
    public static final int EVERYTHING = 10;
    private static int level;

    public static void Info(String str) {
        if (DEBUG >= Log.level)
            System.out.println(str);
    }

    public static void Info(String str, int level) {
        if (level >= Log.level)
            System.out.println(str);
    }

    public static void setShowLevel(int level) {
        Log.level = level;
    }
}
