package data;

import org.glassfish.grizzly.utils.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PostWorkData implements Serializable {
    private String path;
    private String name;
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
            name = object.getString("name");
            path = object.getString("path");

            JSONArray arr = object.getJSONArray("params");
            for (int i = 0; i < arr.length(); i++) {
                String paramName = arr.getJSONObject(i).getString("name");
                Integer paramPrice = arr.getJSONObject(i).getInt("price");
                params.add(new Pair<>(paramName, paramPrice));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        isValid = true;
        Log.Info(fileName + " is valid PostWork JSON");
        for (Pair<String, Integer> pair : params)
            Log.Info(pair.getFirst() + " " + pair.getSecond());
    }

    public boolean isValid() {
        return isValid;
    }

    public List<Pair<String, Integer>> getParams() {
        return params;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }
}
