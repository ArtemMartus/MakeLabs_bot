package maincode.data;

import maincode.helper.Log;
import org.glassfish.grizzly.utils.Pair;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Contract implements Serializable {
    public static final String FRESH_NEW = "Создан новый заказ, не подтверждён";
    public static final String APPLIED = "Заказ подтверждён";
    private static final String base_uri = "./users_database/";
    private static Random random = new Random();
    private String name = "";
    private String additional = "";
    private String comment = "";
    private Integer price = 0;
    private Boolean applied = false;
    private String typeHash = "";
    private String status = "";
    private int id;

    public Contract() {
        id = generateRandomId();
        status = FRESH_NEW;
    }

    public Contract(String name, String additional, String comment, Integer price, Boolean applied, int id, String status) {
        //super();
        setName(name);
        this.additional = additional;
        this.comment = comment;
        this.price = price;
        this.applied = applied;
        this.id = id;
        this.status = status;
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
        String hash = Integer.toHexString(data.getDescription().hashCode());
        if (isFreshNew() || !hash.equals(typeHash)) {
            id = generateRandomId();
            setName(data.getDescription());
            this.additional = "";
            this.comment = "";
            this.price = 0;
            this.applied = false;
            status = FRESH_NEW;

            for (Pair<String, Integer> pair : data.getParams()) {
                int price = pair.getSecond();
                if (price < 0)
                    continue;
                toogle(pair.getFirst());
                this.price += price;
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getAdditional() {
        return additional;
    }

    public String getTypeHash() {
        return typeHash;
    }

    public String getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }

    public Integer getPrice() {
        return price;
    }

    public void apply() {
        applied = true;
        status = APPLIED;
    }

    public Boolean getApplied() {
        return applied;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        typeHash = Integer.toHexString(name.hashCode());
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contract)) return false;
        Contract contract = (Contract) o;
        return typeHash.equals(contract.typeHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAdditional(), getPrice(), getApplied(), getTypeHash(), getId());
    }

    public String getHash() {
        return Integer.toHexString(hashCode());
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

    public boolean loadFrom(String destinationPath) {
        String fileName = destinationPath;
        if (!Files.exists(Paths.get(fileName))) // nothing to load
            return false;

        try {
            String fileData = new String(Files.readAllBytes(Paths.get(fileName))); // Read from file
            JSONObject object = new JSONObject(fileData);

            name = object.getString("name");
            additional = object.getString("additional");
            comment = object.getString("comment");
            price = object.getInt("price");
            applied = object.getBoolean("applied");
            typeHash = object.getString("typeHash");
            status = object.getString("status");
            id = object.getInt("id");


        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        Log.Info(fileName + " is valid Contract JSON");
        return true;
    }

    public void writeTo(String destinationPath) {
        HashMap<String, Object> dataset = new HashMap<>();

        dataset.put("id", id);
        dataset.put("name", name);
        dataset.put("additional", additional);
        dataset.put("comment", comment);
        dataset.put("price", price);
        dataset.put("applied", applied);
        dataset.put("typeHash", typeHash);
        dataset.put("status", status);

        JSONObject object = new JSONObject(dataset);

        String filename = base_uri + destinationPath;

        Path path = Paths.get(filename);

        Log.Info("Generating form " + filename + " " + object.toString(), Log.VERBOSE);


        File file = new File(path.getParent().toString());
        file.mkdirs();

        try {
            Files.write(path, object.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            builder.append(" - - - ").append(str).append("\n");
        }
        builder
                .append("Итоговая цена = ")
                .append(price)
                .append("\n")
                .append("Статус = ")
                .append(status);
        return builder.toString();
    }
}