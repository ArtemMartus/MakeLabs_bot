package maincode.controllers;

import maincode.data.PostWorkData;
import maincode.helper.Log;
import org.glassfish.grizzly.utils.Pair;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostWorkController {

    private static final String base_path = "./post_work";
    private static boolean loaded = false;
    private static HashMap<String, PostWorkData> workMap = new HashMap<>();

    public static String internPath(String path) {
        int indexOfBase = path.indexOf(base_path);
        String ret;
        if (indexOfBase != -1)
            ret = path.substring(indexOfBase + base_path.length());
        else
            ret = path.substring(base_path.length());
        if (ret.isEmpty())
            return "/".concat(ret);
        else
            return ret;
    }

    private static void loadDirectory(File dirFile) {
        Log.Info("Loading " + dirFile.getPath() + " directory");

        File[] contents = dirFile.listFiles();
        if (contents == null || contents.length == 0) {
            Log.Info(dirFile.getPath() + " is empty");
            return;
        }

        boolean hasDescriptor = false;
        for (File f : contents) {
            if (f.isDirectory()) {
                loadDirectory(f);
            } else if (!hasDescriptor) {
                try {
                    PostWorkData workData = new PostWorkData(f.getAbsolutePath());
                    if (!workData.isValid()) {
                        Log.Info(f.getAbsolutePath() + " is not a valid workData. Continuing...", Log.EXTENDED);
                        continue;
                    }

                    String iURI = internPath(dirFile.getPath());
                    PostWorkData duplicateCheck = workMap.get(iURI);
                    if (duplicateCheck == null) {
                        workMap.put(iURI, workData);
                        Log.Info("Added to work map");
                        hasDescriptor = true;
                    } else
                        Log.Info(duplicateCheck.getFileName() + " already owns same path('" + iURI + "') as " + f.getPath());


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        if (!hasDescriptor) {
            Log.Info(dirFile.getPath() + " doesn't have any descriptor");
        }
    }

    public static void loadWork() {
        File dataDirectory = new File(base_path);
        if (dataDirectory.mkdir())
            Log.Info(base_path + " directory created");
        File[] contents = dataDirectory.listFiles();
        if (contents == null || contents.length == 0) {
            Log.Info(base_path + " directory is empty. Fill it");
        } else
            loadDirectory(dataDirectory);

        loaded = true;
        Log.Info("PostWorkController initialized");

        PostWorkData make = getData("/Сделать заказ");
        updateData(make);
    }

    private static PostWorkData updateData(PostWorkData data) {
        if (!loaded)
            loadWork();

        List<PostWorkData> make_children = getChildren(data.getIURI());
        List<Pair<String, Integer>> make_buttons = data.getParams();
        if (make_buttons.size() < make_children.size() + 1) {
            Log.Info("Updating " + data.getIURI() + " params...");
            for (PostWorkData subject : make_children) {
                make_buttons.add(new Pair<>(subject.getDescription(), -1));
            }
            //make_buttons = Lists.reverse(make_buttons);
            data.setParams(make_buttons);
            data.save();
        }
        return data;
    }

    public static boolean pathExists(String uri) {
        if (!loaded)
            loadWork();
        return workMap.containsKey(uri);
    }

    public static PostWorkData getData(String uri) {
        if (!loaded)
            loadWork();

        PostWorkData data = workMap.get(uri);
        if (data != null)
            data = updateData(data);
        return data;
    }

    public static List<PostWorkData> getChildren(String uri) {
        if (!loaded)
            loadWork();

        List<PostWorkData> children = new ArrayList<>();
        File dir = new File(base_path + uri);
        File[] files = dir.listFiles();
        if (files != null)
            for (File file : files) {
                if (file.isDirectory()) {
                    String iURI = internPath(file.getPath());
                    PostWorkData possibleChild = workMap.get(iURI);
                    if (possibleChild != null)
                        children.add(possibleChild);
                }
            }


        return children;
    }

//    public static String getLastName(String path) {
//        int lastSlash = path.lastIndexOf("/");
//        return path.substring(lastSlash + 1, path.length());
//    }

    public static String getBase_path() {
        return base_path;
    }

    public static final String remLast(String str) {
        if (str.length() == 1 || str.indexOf("/") == -1)
            return str;
        int slash = str.lastIndexOf("/");
        if (slash == 0)
            return "/";
        return str.substring(0, slash);
    }

    public static String validifyPath(String string) {
        if (!loaded)
            loadWork();

        Path path = Paths.get(string);
        if (pathExists(string))
            return string;
        if (pathExists(path.getParent().toString()))
            return path.getParent().toString();
        Log.Info("validifyPath returns home '/'. Couldn't find " + string);
        return "/";
    }
}
