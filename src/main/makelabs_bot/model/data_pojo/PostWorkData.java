/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.model.data_pojo;

import main.makelabs_bot.helper.Log;
import main.makelabs_bot.model.Analytics;
import org.glassfish.grizzly.utils.Pair;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PostWorkData {

    private Long id;
    private List<Pair<String, Integer>> params = new LinkedList<>();
    private String description;
    private Long createdByUid;
    private Long created = System.currentTimeMillis() / 1000L;
    private String uri;
    private Boolean has_child = false;


    public PostWorkData(Long id, String params, String description, Long createdByUid, Long created, String uri,
                        Boolean isEndpoint) {
        this.id = id;
        setParams(params);
        this.description = description;
        this.createdByUid = createdByUid;
        this.created = created;
        this.uri = uri;
        this.has_child = !isEndpoint;
    }

    public PostWorkData(String jsonParams, String description, Long createdByUid, String uri) {
        setParams(jsonParams);
        this.description = description;
        this.createdByUid = createdByUid;
        this.uri = uri;
//        save(); not testable
    }

    public void save() {
        Analytics.getInstance().getMakeLabs_bot().model.saveWorkData(this);
        if ((id == null || id < 0))
            id = Analytics.getInstance().getMakeLabs_bot().model.getWorkDataId(this);
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
                ", params=" + getJsonParams() +
                ", description='" + description + '\'' +
                ", createdByUid=" + createdByUid +
                ", created=" + created +
                ", uri='" + uri + '\'' +
                ", has_child=" + has_child +
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

    public Long getCreated() {
        return created;
    }

    public Boolean isEndpoint() {
        return !has_child;
    }

    public void setEndpoint(Boolean endpoint) {
        has_child = !endpoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostWorkData)) return false;
        PostWorkData that = (PostWorkData) o;
        return getId().equals(that.getId()) &&
                getParams().equals(that.getParams()) &&
                getDescription().equals(that.getDescription()) &&
                getCreatedByUid().equals(that.getCreatedByUid()) &&
                getCreated().equals(that.getCreated()) &&
                getUri().equals(that.getUri()) &&
                has_child.equals(that.has_child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getParams(), getDescription(), getCreatedByUid(), getCreated(), getUri(), has_child);
    }
}
