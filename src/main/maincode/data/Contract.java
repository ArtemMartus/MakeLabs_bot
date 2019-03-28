package maincode.data;

import maincode.helper.Log;
import maincode.model.Analytics;
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
    //TODO make contract store date of applying, date of purchase, date start processing, date start reviewing, date give off to client

    public static final String FRESH_NEW = "Создан новый заказ, не подтверждён";
    public static final String APPLIED = "Заказ подтверждён, не оплачен";
    public static final String CANCELED = "Заказ отменён";
    public static final String PURCHASED = "Заказ оплачен, в обработке";
    public static final String PROCESSING = "Заказ принят, ожидайте конец конкурса";
    public static final String REVIEWING = "Конкурсные работы рассматриваются, ожидайте";
    public static final String GIVEOFF = "Ваш заказ выполнен!";

    private static final String base_uri = "./users_database/";
    private static Random random = new Random();
    private static final Calendar calendar = Calendar.getInstance();

    private String name = "";
    private String additional = "";
    private String comment = "";
    private Integer price = 0;

    private Long unixDateOfApplying = -1L;
    private Long unixDateOfPurchase = -1L;
    private Long unixDateOfStartProcessing = -1L;
    private Long unixDateOfStartReviewing = -1L;
    private Long unixDateOfGiveOff = -1L;
    private Long unixDateOfEndingContest = -1L;
    private Long unixDateOfCanceling = -1L;
    private String typeHash = "";
    private String status;
    private int id;

    public Contract() {
        id = generateRandomId();
        status = FRESH_NEW;
    }

    public Contract(String name, String additional, String comment, Integer price, Long unixDateOfApplying,
                    Long unixDateOfPurchase, Long unixDateOfStartProcessing, Long unixDateOfStartReviewing,
                    Long unixDateOfGiveOff, Long hoursOfContest, Long unixDateOfCanceling, String status, int id) {
        setName(name);
        this.additional = additional;
        this.comment = comment;
        this.price = price;
        this.unixDateOfApplying = unixDateOfApplying;
        this.unixDateOfPurchase = unixDateOfPurchase;
        this.unixDateOfStartProcessing = unixDateOfStartProcessing;
        this.unixDateOfStartReviewing = unixDateOfStartReviewing;
        this.unixDateOfGiveOff = unixDateOfGiveOff;
        this.unixDateOfEndingContest = hoursOfContest;
        this.status = status;
        this.id = id;
        this.unixDateOfCanceling = unixDateOfCanceling;
    }

    public static int generateRandomId() {
        int i = random.nextInt();
        i = i < 0 ? -i : i;
        return i;
    }

    public boolean isFreshNew() {
        return name.isEmpty()
                && additional.isEmpty()
                && price == 0
                && unixDateOfApplying < 0
                && unixDateOfPurchase < 0
                && unixDateOfStartProcessing < 0
                && unixDateOfStartReviewing < 0
                && unixDateOfGiveOff < 0
                && unixDateOfEndingContest < 0
                && unixDateOfCanceling < 0
                && status.equals(FRESH_NEW)
                && comment.isEmpty();
    }


    public void setUpAllIncluding(PostWorkData data) {
        String hash = Integer.toHexString(data.getDescription().hashCode());
        if (isFreshNew() || !hash.equals(typeHash)) {
            id = generateRandomId();
            setName(data.getDescription());
            this.additional = "";
            this.comment = "";
            this.price = 0;
            unixDateOfApplying
                    = unixDateOfPurchase
                    = unixDateOfStartProcessing
                    = unixDateOfStartReviewing
                    = unixDateOfGiveOff
                    = unixDateOfEndingContest
                    = unixDateOfCanceling
                    = -1L;
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

    private Long unixNow() {
        return calendar.getTimeInMillis() / 1000L;
    }

    private void setStatus(String status, PostWorkData data) {
        this.status = status;
        Analytics.getInstance().updatePostWorkDataStatus(data, status + " for price " + price);
    }

    public void apply(PostWorkData data) {
        unixDateOfApplying = unixNow();
        setStatus(APPLIED, data);
    }

    public void paid(PostWorkData data) {
        unixDateOfPurchase = unixNow();
        setStatus(PURCHASED, data);
    }

    public void calcel(PostWorkData data) {
        unixDateOfCanceling = unixNow();
        setStatus(CANCELED, data);
    }

    public void process(PostWorkData data) {
        unixDateOfStartProcessing = unixNow();
        setStatus(PROCESSING, data);
    }

    public void endContest(PostWorkData data) {
        unixDateOfEndingContest = unixNow();
    }

    public void review(PostWorkData data) {
        unixDateOfStartReviewing = unixNow();
        setStatus(REVIEWING, data);
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

            unixDateOfApplying = object.getLong("unixDateOfApplying");
            unixDateOfStartProcessing = object.getLong("unixDateOfStartProcessing");
            unixDateOfPurchase = object.getLong("unixDateOfPurchase");
            unixDateOfStartReviewing = object.getLong("unixDateOfStartReviewing");
            unixDateOfGiveOff = object.getLong("unixDateOfGiveOff");
            unixDateOfEndingContest = object.getLong("unixDateOfEndingContest");

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

        dataset.put("unixDateOfApplying", unixDateOfApplying);
        dataset.put("unixDateOfEndingContest", unixDateOfEndingContest);
        dataset.put("unixDateOfStartProcessing", unixDateOfStartProcessing);
        dataset.put("unixDateOfPurchase", unixDateOfPurchase);
        dataset.put("unixDateOfStartReviewing", unixDateOfStartReviewing);
        dataset.put("unixDateOfGiveOff", unixDateOfGiveOff);


        dataset.put("typeHash", typeHash);
        dataset.put("status", status);

        JSONObject object = new JSONObject(dataset);

        String filename = base_uri + destinationPath;



        Log.Info("Generating form " + filename + " " + object.toString(), Log.VERBOSE);


        Path path = Paths.get(filename);
        File file = new File(path.getParent().toUri());


        try {
            if (file.mkdirs()
                    || (file.exists()
                    && file.isDirectory()))
                Files.write(path, object.toString().getBytes());
            else
                System.err.println("Cannot access " + file.getAbsolutePath() + " directory");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCheckoutText(PostWorkData data) {
        int overall_price = 0;
        setName(data.getDescription());
        StringBuilder editedText = new StringBuilder(getName());
        editedText.append("\n");
        for (Pair<String, Integer> pair : data.getParams()) {
            String name = pair.getFirst();
            boolean checked = isSet(name);
            int price = pair.getSecond();
            if (price < 0)
                continue; // it is not actual payment related button. like 'back' button or so
            editedText.append(name).append(" ").append(price).append("₴");
            if (checked) {
                overall_price += price;
                editedText.append("\t\t✅ ");
            } else {
                editedText.append("\t\t❌ ");
            }
            editedText.append("\n");
        }
        if (overall_price > 0)
            editedText.append("\nИтого:\t").append(overall_price).append("₴");
        setPrice(overall_price);
        return editedText.toString();
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
        switch (status) {//TODO make more complex status handling with actual status plus actual status date
            //Done?
            case APPLIED: {
                builder
                        .append("\nЗаказ принят ")
                        .append(Analytics.getTime(unixDateOfApplying));
                break;
            }
            case PURCHASED: {
                builder
                        .append("\nЗаказ оплачен ")
                        .append(Analytics.getTime(unixDateOfPurchase));
                break;
            }
            case PROCESSING: {
                builder
                        .append("\nКонкурс начат ")
                        .append(Analytics.getTime(unixDateOfStartProcessing))
                        .append("\nКонец конкурса ")
                        .append(Analytics.getTime(unixDateOfEndingContest));
                break;
            }
            case REVIEWING: {
                builder
                        .append("\nВыбор лучшей работы ")
                        .append(Analytics.getTime(unixDateOfStartReviewing));
                break;
            }
            case GIVEOFF: {
                builder
                        .append("\nРабота сдана ")
                        .append(Analytics.getTime(unixDateOfGiveOff));
                break;
            }
        }
        return builder.toString();
    }
}