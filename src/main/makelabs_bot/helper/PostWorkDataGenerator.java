/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.helper;

import main.makelabs_bot.controllers.PostWorkController;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PostWorkDataGenerator {
    private static Random random = new Random();

    private static void writeFile(String filePath, JSONObject object) {
        if ((filePath.charAt(filePath.length() - 1)) != '/')
            filePath += '/';
        Path path = Paths.get(PostWorkController.getBase_path() + filePath + random.nextLong());
        try {
            if (!Files.exists(path)) {
                File file = new File(path.getParent().toUri());
                file.mkdirs();
                Files.createFile(path);
            }
            Files.write(path, object.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateFolderDescriptor(String filePath, String description) {
        HashMap<String, String> testSr = new HashMap<>();

        testSr.put("name", "subject");
        testSr.put("description", description);

        JSONObject object = new JSONObject(testSr);

        Log.Info("Generating folder descriptor " + filePath + " " + object.toString(), Log.VERBOSE);

        writeFile(filePath, object);
    }

    public static void generateForm(String filePath, String description, Map<String, Integer> buttons) {
        HashMap<String, String> testSr = new HashMap<>();

        testSr.put("name", "subject");
        testSr.put("description", description);

        JSONObject object = new JSONObject(testSr);
        object.put("objects", buttons);

        Log.Info("Generating form " + filePath + " " + object.toString(), Log.VERBOSE);

        writeFile(filePath, object);
    }
}
