/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.model.data_pojo;

import java.io.Serializable;
import java.util.Objects;


public class ContractUser implements Serializable {
    public static final int USER = 0;
    public static final int EMPLOYEE = 1;
    public static final int MODERATOR = 2;
    public static final int ADMIN = 3;
    public static final int CREATOR = 0xff;

    private long id;
    private String username;
    private String firstname;
    private String lastname;//can be null
    private int userType = USER;
    private String stateUri = "/";
    private int messageId = 0;
    private int spentMoney = 0;
    private int earnedMoney = 0;
    private int ordersOrdered = 0;
    private int ordersMade = 0;
    private int ordersReviewed = 0;
    private int ordersGaveOff = 0;
    private int paymentsAccepted = 0;

    /*
id INT PRIMARY KEY NOT NULL unique,
username TEXT NOT NULL,
firstname TEXT NOT NULL,

lastname text null,
usertype int not null default 0,
state_uri TEXT NOT NULL,

messageId INT not null default 0,
spent_money int not null default 0,
earned_money int not null default 0,
orders_ordered int not null default 0,

orders_made int not null default 0,
orders_reviewed int not null default 0,
orders_gaveoff int not null default 0,
payments_accepted int not null default 0
     */


    public ContractUser(long id, String username, String firstname, String lastname, int messageId) {
        this.id = id;// we have to manually set id to telegram's one
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.messageId = messageId;
    }

    public ContractUser(long id, String username, String firstname, String lastname, int userType, String stateUri,
                        int messageId, int spentMoney, int earnedMoney, int ordersOrdered,
                        int ordersMade, int ordersReviewed, int ordersGaveOff, int paymentsAccepted) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userType = userType;
        this.stateUri = stateUri;
        this.messageId = messageId;
        this.spentMoney = spentMoney;
        this.earnedMoney = earnedMoney;
        this.ordersOrdered = ordersOrdered;
        this.ordersMade = ordersMade;
        this.ordersReviewed = ordersReviewed;
        this.ordersGaveOff = ordersGaveOff;
        this.paymentsAccepted = paymentsAccepted;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public static final String remLast(String str) {
        if (str.length() == 1 || !str.contains("/"))
            return str;
        int slash = str.lastIndexOf("/");
        if (slash == 0)
            return "/";
        return str.substring(0, slash);
    }

    public String goBack() {
        stateUri = remLast(stateUri);
        return stateUri;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getStateUri() {
        return stateUri;
    }

    public void setStateUri(String stateUri) {
        this.stateUri = stateUri;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getSpentMoney() {
        return spentMoney;
    }

    public void setSpentMoney(int spentMoney) {
        this.spentMoney = spentMoney;
    }

    public int getEarnedMoney() {
        return earnedMoney;
    }

    public void setEarnedMoney(int earnedMoney) {
        this.earnedMoney = earnedMoney;
    }

    public int getOrdersOrdered() {
        return ordersOrdered;
    }

    public void setOrdersOrdered(int ordersOrdered) {
        this.ordersOrdered = ordersOrdered;
    }

    public int getOrdersMade() {
        return ordersMade;
    }

    public void setOrdersMade(Integer ordersMade) {
        this.ordersMade = ordersMade;
    }

    public int getOrdersReviewed() {
        return ordersReviewed;
    }

    public void setOrdersReviewed(int ordersReviewed) {
        this.ordersReviewed = ordersReviewed;
    }

    public int getOrdersGaveOff() {
        return ordersGaveOff;
    }

    public void setOrdersGaveOff(int ordersGaveOff) {
        this.ordersGaveOff = ordersGaveOff;
    }

    public int getPaymentsAccepted() {
        return paymentsAccepted;
    }

    public void setPaymentsAccepted(int paymentsAccepted) {
        this.paymentsAccepted = paymentsAccepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContractUser)) return false;
        ContractUser that = (ContractUser) o;
        return getId() == that.getId() &&
                getUserType() == that.getUserType() &&
                getMessageId() == that.getMessageId() &&
                getSpentMoney() == that.getSpentMoney() &&
                getEarnedMoney() == that.getEarnedMoney() &&
                getOrdersOrdered() == that.getOrdersOrdered() &&
                getOrdersMade() == that.getOrdersMade() &&
                getOrdersReviewed() == that.getOrdersReviewed() &&
                getOrdersGaveOff() == that.getOrdersGaveOff() &&
                getPaymentsAccepted() == that.getPaymentsAccepted() &&
                getUsername().equals(that.getUsername()) &&
                getFirstname().equals(that.getFirstname()) &&
                Objects.equals(getLastname(), that.getLastname()) &&
                getStateUri().equals(that.getStateUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getFirstname(), getLastname(), getUserType(), getStateUri(), getMessageId(), getSpentMoney(), getEarnedMoney(), getOrdersOrdered(), getOrdersMade(), getOrdersReviewed(), getOrdersGaveOff(), getPaymentsAccepted());
    }
}