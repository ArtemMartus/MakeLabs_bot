package main.makelabs_bot.data;

import main.makelabs_bot.controllers.PostWorkController;
import main.makelabs_bot.helper.Log;
import org.glassfish.grizzly.utils.Pair;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PostWorkData implements Serializable {
    private String description;
    private List<Pair<String, Integer>> params;
    private String fileName;
    private boolean isValid;

    public PostWorkData(String fileName) {
        isValid = false;
        params = new ArrayList<>();
        this.fileName = fileName;

        try {
            String fileData = new String(Files.readAllBytes(Paths.get(fileName))); // Read from file
            JSONObject object = new JSONObject(fileData);
            description = object.getString("description");

            if (object.has("objects")) {
                Map<String, Object> objMap = object.getJSONObject("objects").toMap();
                Set<String> keys = objMap.keySet();
                for (String key : keys) {
                    Integer paramPrice = (Integer) objMap.get(key);
                    params.add(new Pair<>(key, paramPrice));
                }
                //params = Lists.reverse(params);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        isValid = true;
        Log.Info(fileName + " is valid PostWorkData JSON");
        for (Pair<String, Integer> pair : params)
            Log.Info(pair.getFirst() + " " + pair.getSecond());
    }

    public void save() {
        if (!isValid)
            return;
        HashMap<String, String> testSr = new HashMap<>();

        testSr.put("description", description);

        JSONObject object = new JSONObject(testSr);
        if (hasParams()) {
            Map<String, Integer> buttons = new HashMap<>();
            for (Pair<String, Integer> pair : params)
                buttons.put(pair.getFirst(), pair.getSecond());

            object.put("objects", buttons);
        }

        Log.Info("Generating form " + fileName + " " + object.toString(), Log.VERBOSE);

        Path path = Paths.get(fileName);
        try {
            Files.write(path, object.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isValid() {
        return isValid;
    }

    public List<Pair<String, Integer>> getParams() {
        return params;
    }

    public String getDescription() {
        return description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParams(List<Pair<String, Integer>> params) {
        this.params = params;
    }

    public String getIURI() {
        Path path = Paths.get(fileName).getParent();
        String iURI = PostWorkController.internPath(path.toAbsolutePath().toString(), false);
        return iURI;
    }

    public boolean hasParams() {
        return params != null
                && params.size() > 0;
    }

    public boolean hasChild(String command) {
        if (!hasParams())
            return false;
        for (Pair<String, Integer> pair : params)
            if (pair.getFirst().equals(command))
                return true;
        return false;
    }

    @Override
    public String toString() {
        return "PostWorkData{" +
                "description='" + description + '\'' +
                ", params=" + params +
                ", fileName='" + fileName + '\'' +
                ", isValid=" + isValid +
                '}';
    }
}
