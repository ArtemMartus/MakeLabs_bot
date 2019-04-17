/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.model.other_pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Button {
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("uri")
    private String uri;
    @Expose
    @SerializedName("price")
    private Float price;

    @Expose
    @SerializedName("set")
    private Boolean set;

    public Button(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public Button(String name, String uri, Float price) {
        this.name = name;
        this.uri = uri;
        this.price = price;
    }

    public Button(String name, String uri, Float price, Boolean set) {
        this.name = name;
        this.uri = uri;
        this.price = price;
        this.set = set;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Boolean getSet() {
        return set;
    }

    public void setSet(Boolean set) {
        this.set = set;
    }

    @Override
    public String toString() {
        return "Button{" +
                "name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                ", price=" + price +
                ", set=" + set +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Button)) return false;
        Button button = (Button) o;
        return getName().equals(button.getName()) &&
                getUri().equals(button.getUri()) &&
                Objects.equals(getPrice(), button.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getUri(), getPrice());
    }
}
