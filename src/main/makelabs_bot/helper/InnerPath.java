/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.helper;

import main.makelabs_bot.model.DatabaseManager;

public class InnerPath {
    private String data;

    public InnerPath() {
        data = "/";
    }

    public InnerPath(String data) {
        setData(data);
    }

    public String getLast() {
        return data.substring(data.lastIndexOf("/"));
    }

    public String getPath() {
        return data;
    }

    public boolean isWorkData() {
        return DatabaseManager.getInstance().isWorkDataUriValid(data);
    }

    public String goBack() {
        if (data.length() > 1) {
            data = data.substring(0, data.lastIndexOf("/"));
        }
        return data;
    }

    public String goHome() {
        setData("/");
        return getPath();
    }

    public boolean isAbsolute() {
        return data.startsWith("/");
    }

    public void setData(String data) {
        /*if(!data.startsWith("/"))
            data =*/
        this.data = data;
    }
}
