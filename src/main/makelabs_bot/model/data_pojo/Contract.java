/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.model.data_pojo;

import org.glassfish.grizzly.utils.Pair;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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

    private Long id;
    private long customer_uid;
    private long work_data_id;
    private String name = "";
    private String additional = "";
    private String comment = "";
    private Float price = 500.0f;// decimal(6,2) e.g. 1234.56
    // it has to be representation of coins (still storing as int)
    // or it can be a float number (database table has to be changed)
    private String status = FRESH_NEW;//"default status when creating custom contract";

    private Timestamp applied;
    private Timestamp paid;
    private Timestamp taken;
    private Timestamp gaveOff;
    private Timestamp created;

    private Long paymentCheckedByUID;
    private Long takenByUID;
    private Long reviewByUID;
    private Long gaveOffByUID;

    public Contract(long customer_uid) {
        this.customer_uid = customer_uid;
        setStatus(FRESH_NEW);
    }

    public Contract(long customer_uid, long work_data_id, String name, String additional, String comment, Float price) {
        this.customer_uid = customer_uid;
        this.work_data_id = work_data_id;
        this.name = name;
        this.additional = additional;
        this.comment = comment;
        this.price = price;
    }

    public Contract(Long id, long customer_uid, long work_data_id, String name, String additional, String comment,
                    Float price, String status, Timestamp applied, Timestamp paid, Long paymentCheckedByUID, Long takenByUID,
                    Timestamp taken, Long reviewByUID, Timestamp gaveOff, Long gaveOffByUID, Timestamp created) {
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
        this.created = created;
    }

    public void setUpAllIncluding(PostWorkData data) throws Exception {
        if (data.getId() == null || data.getId() < 0) {
            throw new Exception("work data_pojo id = null");
        }
        this.work_data_id = data.getId();
        this.additional = "";
        this.comment = "";
        this.name = data.getDescription();
        this.price = 150.0f;
    }

//    public void save() {
//        we can't insert contract id into database as it's being generated automatically
//        Analytics.getInstance().getMakeLabs_bot().model.saveContract(this);
//        if ((id == null || id < 0) && applied != null)
//            id = Analytics.getInstance().getMakeLabs_bot().model.getContractId(this);
//    }

    public void apply() {
        applied = new Timestamp(new Date().getTime());
        setStatus(APPLIED);
    }

    public void paid() {
        paid = new Timestamp(new Date().getTime());
        setStatus(PURCHASED);
    }

    public void giveOff() {
        gaveOff = new Timestamp(new Date().getTime());
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
        return editedText.toString();
    }

    public boolean isSet(String ptr) {
        return additional.contains("#" + ptr + "#");
    }

    public boolean toggle(String ptr) {
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
        builder
                .append("Заказ ")
                .append(id)
                .append(" создан ")
                .append(created)
                .append("\tПользователь ")
                .append(customer_uid)
                .append("\tДанные о работе ")
                .append(work_data_id)
                .append("\t")
                .append(name)
                .append(" Включено:")
                .append(" ");
        for (String str : getStuffSet()) {
            builder.append(" - - - ").append(str).append(" ");
        }
        builder
                .append("Итоговая цена = ")
                .append(price)
                .append(" ")
                .append("Статус = ")
                .append(status);
        switch (status) {
            case APPLIED: {
                builder
                        .append(" Заказ принят ")
                        .append(applied.toString());
                break;
            }
            case PURCHASED: {
                builder
                        .append(" Заказ оплачен ")
                        .append(paid.toString());
                break;
            }
            case GIVEOFF: {
                builder
                        .append(" Работа сдана ")
                        .append(gaveOff.toString());
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

    public long getCustomerId() {
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

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    private void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getApplied() {
        return applied;
    }

    public Timestamp getPaid() {
        return paid;
    }

    public Timestamp getGaveOff() {
        return gaveOff;
    }

    public Long getPaymentCheckedByUID() {
        return paymentCheckedByUID;
    }

    public void setPaymentCheckedByUID(Long paymentCheckedByUID) {
        this.paymentCheckedByUID = paymentCheckedByUID;
    }

    public Long getTakenByUID() {
        return takenByUID;
    }

    public void setTakenByUID(Long takenByUID) {
        this.takenByUID = takenByUID;
    }

    public void setGaveOff(Timestamp gaveOff) {
        this.gaveOff = gaveOff;
    }

    public Timestamp getTaken() {
        return taken;
    }

    public Long getReviewByUID() {
        return reviewByUID;
    }

    public void setReviewByUID(Long reviewByUID) {
        this.reviewByUID = reviewByUID;
    }

    public void setTaken(Timestamp taken) {
        this.taken = taken;
    }

    public Long getGaveOffByUID() {
        return gaveOffByUID;
    }

    public void setGaveOffByUID(Long gaveOffByUID) {
        this.gaveOffByUID = gaveOffByUID;
    }

    public void setId(Long contractId) {
        id = contractId;
    }

    public Timestamp getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contract)) return false;
        Contract contract = (Contract) o;
        return customer_uid == contract.customer_uid &&
                work_data_id == contract.work_data_id &&
                getId().equals(contract.getId()) &&
                getName().equals(contract.getName()) &&
                getAdditional().equals(contract.getAdditional()) &&
                getPrice().equals(contract.getPrice()) &&
                Objects.equals(getApplied(), contract.getApplied()) &&
                Objects.equals(getPaid(), contract.getPaid()) &&
                Objects.equals(getTaken(), contract.getTaken()) &&
                Objects.equals(getGaveOff(), contract.getGaveOff()) &&
                created.equals(contract.created) &&
                Objects.equals(getPaymentCheckedByUID(), contract.getPaymentCheckedByUID()) &&
                Objects.equals(getTakenByUID(), contract.getTakenByUID()) &&
                Objects.equals(getReviewByUID(), contract.getReviewByUID()) &&
                Objects.equals(getGaveOffByUID(), contract.getGaveOffByUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), customer_uid, work_data_id, getName(), getAdditional(), getPrice(), getApplied(), getPaid(), getTaken(), getGaveOff(), created, getPaymentCheckedByUID(), getTakenByUID(), getReviewByUID(), getGaveOffByUID());
    }
}