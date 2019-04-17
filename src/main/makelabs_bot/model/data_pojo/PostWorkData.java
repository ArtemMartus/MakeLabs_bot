/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.model.data_pojo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.makelabs_bot.helper.Log;
import main.makelabs_bot.model.other_pojo.Button;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PostWorkData {

    private Long id;
    private List<Button> params = new LinkedList<>();
    private String description;
    private Long createdByUid;
    private Timestamp created = new Timestamp(new Date().getTime());
    private String uri; // todo migrate it  from string to InnerPath
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
        for (Button btn : params)
            if (btn.getName().equals(command))
                return true;
        return false;
    }

    public String getJsonParams() {
        return new Gson().toJson(params);
    }


    public List<Button> getParams() {
        return params;
    }

    public void setParams(String jsonParams) {
        Type listType = new TypeToken<List<Button>>() {
        }.getType();
        params = new Gson().fromJson(jsonParams, listType);
        Log.Info(params.toString());
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
        float price = 0.0f;
        for (Button btn : params) {
            if (btn.getPrice() != null)
                price += btn.getPrice();
        }
        return price;
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
