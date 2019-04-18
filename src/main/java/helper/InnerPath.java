/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package helper;

import makelabs_bot.model.DatabaseManager;

import java.util.Objects;

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

        if (index < 0) {
            if (data.length() > 0)
                return "";
            else
                return data;
        }
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

    @Override
    public String toString() {
        return getPath();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InnerPath)) return false;
        InnerPath innerPath = (InnerPath) o;
        return data.equals(innerPath.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    public String addCommand(String gotMessage) {
        if (gotMessage == null)
            return data;
        if (data.length() > 0 && data.endsWith("/"))
            data = data.substring(0, data.length() - 1);
        if (gotMessage.length() > 0 && gotMessage.startsWith("/"))
            gotMessage = gotMessage.substring(1);
        data = data + "/" + gotMessage;
        if (data.length() > 0 && data.endsWith("/"))
            data = data.substring(0, data.length() - 1);
        if (data.isEmpty())
            return goHome();
        return data;
    }
}
