/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.data;

import main.makelabs_bot.controllers.MakeLabs_bot;
import main.makelabs_bot.helper.Log;
import main.makelabs_bot.model.Analytics;
import org.glassfish.grizzly.utils.Pair;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class PostWorkData {
    private MakeLabs_bot makeLabsBot = Analytics.getInstance().getMakeLabs_bot();

    private Long id;
    private List<Pair<String, Integer>> params = new LinkedList<>();
    private String description;
    private Long createdByUid;
    private Long created;
    private String uri;


    /*
    id INT PRIMARY KEY AUTO_INCREMENT unique,
    params_json text not null,
    description text not null,

    created_by_uid int not null,
    created datetime not null default now(),
    uri text not null
     */
    public PostWorkData(Long id, String jsonParams, String description, Long createdByUid, String uri) {
        this.id = id;
        setParams(jsonParams);
        this.description = description;
        this.createdByUid = createdByUid;
        this.uri = uri;
    }

    public PostWorkData(String jsonParams, String description, Long createdByUid, String uri) {
        setParams(jsonParams);
        this.description = description;
        this.createdByUid = createdByUid;
        this.uri = uri;
        save();
    }

//    public PostWorkData(String fileName) {
//        isValid = false;
//        params = new ArrayList<>();
//        this.fileName = fileName;
//
//        try {
//            String fileData = new String(Files.readAllBytes(Paths.get(fileName))); // Read from file
//            JSONObject object = new JSONObject(fileData);
//            description = object.getString("description");
//
//            if (object.has("objects")) {
//                Map<String, Object> objMap = object.getJSONObject("objects").toMap();
//                Set<String> keys = objMap.keySet();
//                for (String key : keys) {
//                    Integer paramPrice = (Integer) objMap.get(key);
//                    params.add(new Pair<>(key, paramPrice));
//                }
//                //params = Lists.reverse(params);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return;
//        }
//        isValid = true;
//        Log.Info(fileName + " is valid PostWorkData JSON");
//        for (Pair<String, Integer> pair : params)
//            Log.Info(pair.getFirst() + " " + pair.getSecond());
//    }

//    public void save() {
//        if (!isValid)
//            return;
//        HashMap<String, String> testSr = new HashMap<>();
//
//        testSr.put("description", description);
//
//        JSONObject object = new JSONObject(testSr);
//        if (hasParams()) {
//            Map<String, Integer> buttons = new HashMap<>();
//            for (Pair<String, Integer> pair : params)
//                buttons.put(pair.getFirst(), pair.getSecond());
//
//            object.put("objects", buttons);
//        }
//
//        Log.Info("Generating form " + fileName + " " + object.toString(), Log.VERBOSE);
//
//        Path path = Paths.get(fileName);
//        try {
//            Files.write(path, object.toString().getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public String getIURI() {
//        Path path = Paths.get(fileName).getParent();
//        String iURI = PostWorkController.internPath(path.toAbsolutePath().toString(), false);
//        return iURI;
//    }

    public void save() {
        makeLabsBot.model.saveWorkData(this);
        if ((id == null || id < 0))
            id = makeLabsBot.model.getWorkDataId(this);
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

    public String getJsonParams() {
        return new JSONObject(params).toString();
    }

    @Override
    public String toString() {
        return "PostWorkData{" +
                "id=" + id +
                ", params=" + params +
                ", description='" + description + '\'' +
                ", createdByUid=" + createdByUid +
                ", uri='" + uri + '\'' +
                '}';
    }

    public List<Pair<String, Integer>> getParams() {
        return params;
    }

    public void setParams(String jsonParams) {
        JSONObject rootJson = new JSONObject(jsonParams);
//      debug printout todo make it load from json
        Log.Info(rootJson.toString());
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatedByUid() {
        return createdByUid;
    }

    public void setCreatedByUid(Long createdByUid) {
        this.createdByUid = createdByUid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
