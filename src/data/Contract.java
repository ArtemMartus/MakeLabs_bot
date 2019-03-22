package data;

import org.glassfish.grizzly.utils.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Contract implements Serializable {
    static Random random = new Random();
    private String name = "";
    private String additional = "";
    private String comment = "";
    private Integer price = 0;
    private Boolean applied = false;
    private int id;

    public Contract() {
        id = generateRandomId();
    }

    public Contract(String name, String additional, String comment, Integer price, Boolean applied, int id) {
        //super();
        this.name = name;
        this.additional = additional;
        this.comment = comment;
        this.price = price;
        this.applied = applied;
        this.id = id;
    }

    public Contract(String name, String additional, String comment, Integer price) {
        super();
        this.name = name;
        this.additional = additional;
        this.comment = comment;
        this.price = price;
        applied = false;
    }

    public static int generateRandomId() {
        int i = random.nextInt();
        i = i < 0 ? -i : i;
        return i;
    }

    public boolean isFreshNew() {
        return name.isEmpty() && additional.isEmpty() && price == 0 && !applied && comment.isEmpty();
    }

    public void setUpAllIncluding(PostWorkData data) {
        id = generateRandomId();
        this.name = data.getDescription();
        for (Pair<String, Integer> pair : data.getParams()) {
            int price = pair.getSecond();
            if (price < 0)
                continue;
            toogle(pair.getFirst());
            this.price += price;
        }
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

    public int getId() {
        return id;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (id == 0)
            id = generateRandomId();
        builder
                .append("Заказ ")
                .append(id)
                .append("\t")
                .append(name)
                .append("\nВключено:")
                .append("\n");
        for (String str : getStuffSet()) {
            builder.append("\t").append(str).append("\n");
        }
        builder
                .append("Итоговая цена = ")
                .append(price)
                .append("\n");
        return builder.toString();
    }
}