/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.data;

import main.makelabs_bot.controllers.MakeLabs_bot;
import main.makelabs_bot.model.Analytics;
import org.glassfish.grizzly.utils.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Contract implements Serializable {

    public static final String FRESH_NEW = "Создан новый заказ, не подтверждён";
    public static final String APPLIED = "Заказ подтверждён, не оплачен";
    //    public static final String CANCELED = "Заказ отменён";
    public static final String PURCHASED = "Заказ оплачен, в обработке";
    //    public static final String PROCESSING = "Заказ принят, ожидайте конец конкурса";
//    public static final String REVIEWING = "Конкурсные работы рассматриваются, ожидайте";
    public static final String GIVEOFF = "Ваш заказ выполнен!";

    //    private static final String base_uri = "./users_database/";
//    private static Random random = new Random();
//    private static final Calendar calendar = Calendar.getInstance();
    private final MakeLabs_bot makeLabsBot = Analytics.getInstance().getMakeLabs_bot();

    private Long id;
    private long customer_uid;
    private long work_data_id = -1L;
    private String name = "";
    private String additional = "";
    private String comment = "";
    private Integer price = 0;
    private String status;

    private Long applied = -1L;
    private Long paid = -1L;
    private Long paymentCheckedByUID = -1L;
    private Long takenByUID = -1L;

    private Long taken = -1L;
    private Long reviewByUID = -1L;
    private Long gaveOff = -1L;
    private Long gaveOffByUID = -1L;

    /*
id INT PRIMARY KEY AUTO_INCREMENT unique,
customer_uid int not null,
work_data_id int not null,
name TEXT NOT NULL,

additional TEXT not null,
comment text null,
price int not null CHECK(price >= 150),
status text not null,

applied datetime not null,
paid datetime null default null,
payment_checked_by_uid int null default null,
taken_by_uid int null default null,

taken datetime null default null,
reviewed_by_uid int null default null,
gaveoff datetime null default null,
gaveoff_by_uid int null default null
     */

    public Contract(long customer_uid) {
        this.customer_uid = customer_uid;
        setStatus(FRESH_NEW);
    }

    public Contract(long customer_uid, long work_data_id, String name, String additional, String comment, Integer price) {
        this.customer_uid = customer_uid;
        this.work_data_id = work_data_id;
        this.name = name;
        this.additional = additional;
        this.comment = comment;
        this.price = price;
        save();
    }

    public Contract(Long id, long customer_uid, long work_data_id, String name, String additional, String comment,
                    Integer price, String status, Long applied, Long paid, Long paymentCheckedByUID, Long takenByUID,
                    Long taken, Long reviewByUID, Long gaveOff, Long gaveOffByUID) {
        this.id = id;
        this.customer_uid = customer_uid;
        this.work_data_id = work_data_id;
        this.name = name;
        this.additional = additional;
        this.comment = comment;
        this.price = price;
        this.status = status;
        this.applied = applied;
        this.paid = paid;
        this.paymentCheckedByUID = paymentCheckedByUID;
        this.takenByUID = takenByUID;
        this.taken = taken;
        this.reviewByUID = reviewByUID;
        this.gaveOff = gaveOff;
        this.gaveOffByUID = gaveOffByUID;
    }

    public void setUpAllIncluding(PostWorkData data) throws Exception {
        if (data.getId() == null || data.getId() < 0) {
            throw new Exception("work data id = null");
        }
        this.work_data_id = data.getId();
        this.additional = "";
        this.comment = "";
        this.name = data.getDescription();
        this.price = 0;

//        old code down there
//        String hash = Integer.toHexString(data.getDescription().hashCode());
//        if (isFreshNew() || !hash.equals(typeHash)) {
//            id = generateRandomId();
//            setName(data.getDescription());
//            this.additional = "";
//            this.comment = "";
//            this.price = 0;
//            applied
//                    = paid
//                    = unixDateOfStartProcessing
//                    = unixDateOfStartReviewing
//                    = gaveOff
//                    = unixDateOfEndingContest
//                    = unixDateOfCanceling
//                    = -1L;
//            status = FRESH_NEW;
//
//            for (Pair<String, Integer> pair : data.getParams()) {
//                int price = pair.getSecond();
//                if (price < 0)
//                    continue;
//                toogle(pair.getFirst());
//                this.price += price;
//            }
//        }
    }

    private Long unixNow() {
        return System.currentTimeMillis() / 1000L;
    }

    public void save() {
//        we can't insert contract id into database as it's being generated automatically
        makeLabsBot.model.saveContract(this);
        if ((id == null || id < 0) && applied > 0)
            id = makeLabsBot.model.getContractId(this);
    }

    public void apply(/*PostWorkData data*/) {
//        dataURI = data.getIURI();
        applied = unixNow();
        setStatus(APPLIED);
//        writeTo(contractUser + "_applied/" + getHash());
    }

    public void paid() {
//        try {
//            Files.delete(Paths.get(base_uri + contractUser + "_applied/" + getHash()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        paid = unixNow();
        setStatus(PURCHASED);

//        writeTo(contractUser + "_paid/" + getHash());
    }

    public void giveOff() {
        gaveOff = unixNow();
        setStatus(GIVEOFF);
    }

//    public void calcel() {
//        unixDateOfCanceling = unixNow();
//        setStatus(CANCELED);
//    }
//
//    public void process() {
//        unixDateOfStartProcessing = unixNow();
//        setStatus(PROCESSING);
//    }
//
//    public void endContest() {
//        unixDateOfEndingContest = unixNow();
//    }
//
//    public void review() {
//        unixDateOfStartReviewing = unixNow();
//        setStatus(REVIEWING);
//    }

    public String getCheckoutText(PostWorkData data) {
        int overall_price = 0;
//        it's a get method and it shouldn't change any data
//        setName(data.getDescription());
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
//                it's a get method and it shouldn't change any data
//        setPrice(overall_price);
        return editedText.toString();
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

//    Move that functionality to database controller class

//    public boolean loadFrom(String destinationPath) {
//        String fileName = destinationPath;
//        if (!Files.exists(Paths.get(fileName))) // nothing to load
//            return false;
//
//        try {
//            String fileData = new String(Files.readAllBytes(Paths.get(fileName))); // Read from file
//            JSONObject object = new JSONObject(fileData);
//
//            name = object.getString("name");
//            additional = object.getString("additional");
//            comment = object.getString("comment");
//            dataURI = object.getString("dataURI");
//            price = object.getInt("price");
//
//            applied = object.getLong("applied");
//            unixDateOfStartProcessing = object.getLong("unixDateOfStartProcessing");
//            paid = object.getLong("paid");
//            unixDateOfStartReviewing = object.getLong("unixDateOfStartReviewing");
//            gaveOff = object.getLong("gaveOff");
//            unixDateOfEndingContest = object.getLong("unixDateOfEndingContest");
//            contractUser = object.getLong("contractUser");
//
//            typeHash = object.getString("typeHash");
//            status = object.getString("status");
//            id = object.getInt("id");
//
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return false;
//        }
//
//        Log.Info(fileName + " is valid Contract JSON");
//        return true;
//    }
//
//    private void writeTo(String destinationPath) {
//        HashMap<String, Object> dataset = new HashMap<>();
//
//        dataset.put("id", id);
//        dataset.put("name", name);
//        dataset.put("additional", additional);
//        dataset.put("comment", comment);
//        dataset.put("dataURI", dataURI);
//        dataset.put("price", price);
//        dataset.put("contractUser", contractUser);
//
//        dataset.put("applied", applied);
//        dataset.put("unixDateOfEndingContest", unixDateOfEndingContest);
//        dataset.put("unixDateOfStartProcessing", unixDateOfStartProcessing);
//        dataset.put("paid", paid);
//        dataset.put("unixDateOfStartReviewing", unixDateOfStartReviewing);
//        dataset.put("gaveOff", gaveOff);
//
//
//        dataset.put("typeHash", typeHash);
//        dataset.put("status", status);
//
//        JSONObject object = new JSONObject(dataset);
//
//        String filename = base_uri + destinationPath;
//
//
//
//        Log.Info("Generating form " + filename + " " + object.toString(), Log.VERBOSE);
//
//
//        Path path = Paths.get(filename);
//        File file = new File(path.getParent().toUri());
//
//
//        try {
//            if (file.mkdirs()
//                    || (file.exists()
//                    && file.isDirectory()))
//                Files.write(path, object.toString().getBytes());
//            else
//                System.err.println("Cannot access " + file.getAbsolutePath() + " directory");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder
                .append("Заказ ")
                .append(id)
                .append("\tПользователь ")
                .append(customer_uid)
                .append("\tДанные о работе ")
                .append(work_data_id)
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
        switch (status) {
            case APPLIED: {
                builder
                        .append("\nЗаказ принят ")
                        .append(Analytics.getTime(applied));
                break;
            }
            case PURCHASED: {
                builder
                        .append("\nЗаказ оплачен ")
                        .append(Analytics.getTime(paid));
                break;
            }
            case GIVEOFF: {
                builder
                        .append("\nРабота сдана ")
                        .append(Analytics.getTime(gaveOff));
                break;
            }
        }
        return builder.toString();
    }

    public long getWorkDataId() {
        return work_data_id;
    }

    public void setWorkDataId(long work_data_id) {
        this.work_data_id = work_data_id;
    }

    public Long getId() {
        return id;
    }

    public long getCustomer_uid() {
        return customer_uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    private void setStatus(String status) {
        this.status = status;
//        Currently postpone analytics
//        Analytics.getInstance().updatePostWorkDataStatus(dataURI, "<" + status + "> for price <" + price + ">");
        save();
    }

    public Long getApplied() {
        return applied;
    }

    public Long getPaid() {
        return paid;
    }

    public Long getGaveOff() {
        return gaveOff;
    }
}