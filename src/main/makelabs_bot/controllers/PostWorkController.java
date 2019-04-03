package main.makelabs_bot.controllers;

import main.makelabs_bot.data.PostWorkData;
import main.makelabs_bot.helper.Log;
import org.glassfish.grizzly.utils.Pair;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostWorkController {

    private static final String base_path = "./post_work";
    private static final String samples_path = "./samples";

    private static boolean loaded = false;
    private static boolean loadedSamples = false;
    private static HashMap<String, PostWorkData> workMap = new HashMap<>();
    private static HashMap<String, PostWorkData> samplesMap = new HashMap<>();

    public static String internPath(String path, boolean sample) {
        String look_path = sample ? samples_path : base_path;

        int indexOfBase = path.indexOf(look_path);
        String ret;
        if (indexOfBase != -1)
            ret = path.substring(indexOfBase + look_path.length());
        else
            ret = path.substring(look_path.length());
        if (ret.isEmpty())
            return "/".concat(ret);
        else
            return ret;
    }

    private static void loadDirectory(File dirFile, Map<String, PostWorkData> map, boolean sample) {
        Log.Info("Loading " + dirFile.getPath() + " directory");

        File[] contents = dirFile.listFiles();
        if (contents == null || contents.length == 0) {
            Log.Info(dirFile.getPath() + " is empty");
            return;
        }

        boolean hasDescriptor = false;
        for (File f : contents) {
            if (f.isDirectory()) {
                loadDirectory(f, map, sample);
            } else if (!hasDescriptor) {
                try {
                    PostWorkData workData = new PostWorkData(f.getAbsolutePath());
                    if (!workData.isValid()) {
                        Log.Info(f.getAbsolutePath() + " is not a valid workData. Continuing...", Log.EXTENDED);
                        continue;
                    }

                    String iURI = internPath(dirFile.getPath(), sample);
                    PostWorkData duplicateCheck = map.get(iURI);
                    if (duplicateCheck == null) {
                        map.put(iURI, workData);
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


    public static void loadWork(boolean sample) {

        String path = sample ? samples_path : base_path;
        Map<String, PostWorkData> map = sample ? samplesMap : workMap;

        File dataDirectory = new File(path);
        if (dataDirectory.mkdir())
            Log.Info(path + " directory created");
        File[] contents = dataDirectory.listFiles();
        if (contents == null || contents.length == 0) {
            Log.Info(path + " directory is empty. Fill it");
        } else
            loadDirectory(dataDirectory, map, sample);

        //TODO write methods for loading samples
        //  We will use samples for /mycontract and /checkout

        //TODO for (/Мои заказы) each message give a inline keyboard for 'pay','cancel','change price'
        //  it may follow /samples/myorder sample with buttons and description being added during generation process
        if (!sample) {
            loaded = true;
            Log.Info("PostWorkController initialized");

            PostWorkData make = getData("/Сделать заказ", false);
            updateData(make, false);
        } else
            loadedSamples = true;
    }


    private static PostWorkData updateData(PostWorkData data, boolean sample) {
        if ((sample && !loadedSamples)
                || (!sample && !loaded))
            loadWork(sample);

        List<PostWorkData> make_children = getChildren(data.getIURI(), sample);
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

    public static boolean pathExists(String uri, boolean sample) {
        if ((sample && !loadedSamples)
                || (!sample && !loaded))
            loadWork(sample);
        return workMap.containsKey(uri);
    }

    public static PostWorkData getData(String uri, boolean sample) {
        if ((sample && !loadedSamples)
                || (!sample && !loaded))
            loadWork(sample);

        PostWorkData data = workMap.get(uri);
        if (data != null)
            data = updateData(data, sample);
        return data;
    }


    public static List<PostWorkData> getChildren(String uri, boolean sample) {
        if ((sample && !loadedSamples)
                || (!sample && !loaded))
            loadWork(sample);

        List<PostWorkData> children = new ArrayList<>();
        File dir = new File(base_path + uri);
        File[] files = dir.listFiles();
        if (files != null)
            for (File file : files) {
                if (file.isDirectory()) {
                    String iURI = internPath(file.getPath(), sample);
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

    public static String getSamples_path() {
        return samples_path;
    }

    public static final String remLast(String str) {
        if (str.length() == 1 || !str.contains("/"))
            return str;
        int slash = str.lastIndexOf("/");
        if (slash == 0)
            return "/";
        return str.substring(0, slash);
    }

    public static String validifyPath(String string, boolean sample) {
        if ((sample && !loadedSamples)
                || (!sample && !loaded))
            loadWork(sample);

        Path path = Paths.get(string);
        if (pathExists(string, sample))
            return string;
        if (pathExists(path.getParent().toString(), sample))
            return path.getParent().toString();
        Log.Info("validifyPath returns home '/'. Couldn't find " + string);
        return "/";
    }
}
