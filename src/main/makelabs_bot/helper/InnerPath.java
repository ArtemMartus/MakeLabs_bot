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
        if (data.isEmpty())
            return data;
        return data.substring(data.lastIndexOf("/") + 1);
    }

    public String getPath() {
        return data;
    }

    public boolean isWorkData() {
        return DatabaseManager.getInstance().isWorkDataUriValid(data);
    }

    public String goBack() {
        int index = data.lastIndexOf("/");
        if (index == 0 &&
                data.length() > 0)
            index++;
        if (data.length() == 0 || index < 0)
            return data;
        data = data.substring(0, index);
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
