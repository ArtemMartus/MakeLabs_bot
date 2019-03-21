package controllers;

import data.Log;
import data.PostWorkData;

import java.io.File;
import java.util.HashMap;

public class PostWorkController {

    private HashMap<String, PostWorkData> workMap = new HashMap<>();

    public void loadWork() {
        File directory = new File("./post_work/");
        if (directory.mkdir())
            Log.Info("post_work directory created");
        File[] contents = directory.listFiles();
        if (contents == null) {
            Log.Info("post_work directory is empty. Fill it");
            return;
        }
        for (File f : contents) {
            Log.Info(f.getAbsolutePath());
            try {
                PostWorkData workData = new PostWorkData(f.getAbsolutePath());

                PostWorkData duplicateCheck = workMap.get(workData.getPath());
                if (duplicateCheck == null) {
                    workMap.put(workData.getPath(), workData);
                    Log.Info("Added to work map");
                } else {
                    Log.Info(duplicateCheck.getFileName() + " already owns same path as " + f.getAbsolutePath());
                    continue;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
