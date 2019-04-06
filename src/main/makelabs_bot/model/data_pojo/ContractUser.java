/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.model.data_pojo;

import java.io.Serializable;


public class ContractUser implements Serializable {
    public static final int USER = 0;
    public static final int EMPLOYEE = 1;
    public static final int MODERATOR = 2;
    public static final int ADMIN = 3;
    public static final int CREATOR = 0xff;

    private Integer id;
    private String username;
    private String firstname;
    private String lastname;//can be null
    private int userType = USER;
    private String stateUri = "/";
    private Integer messageId = 0;
    private Integer spentMoney = 0;
    private Integer earnedMoney = 0;
    private Integer ordersOrdered = 0;
    private Integer ordersMade = 0;
    private Integer ordersReviewed = 0;
    private Integer ordersGaveOff = 0;
    private Integer paymentsAccepted = 0;

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


    public ContractUser(Integer id, String username, String firstname, String lastname, Integer messageId) {
        this.id = id;// we have to manually set id to telegram's one
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.messageId = messageId;
    }

    public ContractUser(Integer id, String username, String firstname, String lastname, int userType, String stateUri,
                        Integer messageId, Integer spentMoney, Integer earnedMoney, Integer ordersOrdered,
                        Integer ordersMade, Integer ordersReviewed, Integer ordersGaveOff, Integer paymentsAccepted) {
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

    public Integer getId() {
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

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getSpentMoney() {
        return spentMoney;
    }

    public void setSpentMoney(Integer spentMoney) {
        this.spentMoney = spentMoney;
    }

    public Integer getEarnedMoney() {
        return earnedMoney;
    }

    public void setEarnedMoney(Integer earnedMoney) {
        this.earnedMoney = earnedMoney;
    }

    public Integer getOrdersOrdered() {
        return ordersOrdered;
    }

    public void setOrdersOrdered(Integer ordersOrdered) {
        this.ordersOrdered = ordersOrdered;
    }

    public Integer getOrdersMade() {
        return ordersMade;
    }

    public void setOrdersMade(Integer ordersMade) {
        this.ordersMade = ordersMade;
    }

    public Integer getOrdersReviewed() {
        return ordersReviewed;
    }

    public void setOrdersReviewed(Integer ordersReviewed) {
        this.ordersReviewed = ordersReviewed;
    }

    public Integer getOrdersGaveOff() {
        return ordersGaveOff;
    }

    public void setOrdersGaveOff(Integer ordersGaveOff) {
        this.ordersGaveOff = ordersGaveOff;
    }

    public Integer getPaymentsAccepted() {
        return paymentsAccepted;
    }

    public void setPaymentsAccepted(Integer paymentsAccepted) {
        this.paymentsAccepted = paymentsAccepted;
    }



}