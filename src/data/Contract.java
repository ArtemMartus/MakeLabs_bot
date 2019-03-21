package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Contract implements Serializable {
    private String name;
    private String additional;
    private String comment;
    private Integer price;
    private Boolean applied;

    public Contract() {
    }

    public Contract(String name, String additional, String comment, Integer price, Boolean applied) {
        this.name = name;
        this.additional = additional;
        this.comment = comment;
        this.price = price;
        this.applied = applied;
    }

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

    public void setName(String name) {
        this.name = name;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public boolean isSet(String ptr) {
        return additional.contains("#" + ptr + "#");
    }

    public boolean toogle(String ptr) {
        if (isSet(ptr)) {
            additional = additional.replace("#" + ptr + "#", "");
            return false;
        } else {
            additional += "#" + ptr + "#";
            return true;
        }
    }

    public List<String> getStuffSet() {
        List<String> str = new ArrayList<>();
        Pattern pattern = Pattern.compile("#(.*?)#");
        Matcher matcher = pattern.matcher(additional);
        while (matcher.find()) {
            str.add(matcher.group(1));
        }
        return str;
    }
}