/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.model.data_pojo;

import main.makelabs_bot.helper.Log;
import org.glassfish.grizzly.utils.Pair;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PostWorkData {

    private Long id;
    private List<Pair<String, Integer>> params = new LinkedList<>();
    private String description;
    private Long createdByUid;
    private Timestamp created = new Timestamp(new Date().getTime());
    private String uri;
    private Boolean has_child = false;


    public PostWorkData(Long id, String params, String description, Long createdByUid, Timestamp created, String uri,
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
        //todo think about button data we need
        // button name: String
        // button uri it leads to: String
        // button price if any: Int
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

    public float getOverallPrice() {
        return 0.0f;//todo implement overall price for work
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Timestamp getCreated() {
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
                Objects.equals(getParams(), that.getParams()) &&
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
