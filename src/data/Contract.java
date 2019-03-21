package data;

import java.io.Serializable;

public class Contract implements Serializable {
    private String name;
    private String additional;
    private String comment;
    private Integer price;
    private Boolean applied;

    public Contract(String name, String additional, String comment, Integer price) {
        this.name = name;
        this.additional = additional;
        this.comment = comment;
        this.price = price;
        applied = false;
    }

    public String getName() {
        return name;
    }

    public String getAdditional() {
        return additional;
    }

    public String getComment() {
        return comment;
    }

    public Integer getPrice() {
        return price;
    }

    public void apply() {
        applied = true;
    }

    public Boolean getApplied() {
        return applied;
    }
}