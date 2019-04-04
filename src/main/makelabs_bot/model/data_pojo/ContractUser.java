/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.model.data_pojo;

import java.io.Serializable;

import static main.makelabs_bot.controllers.PostWorkController.remLast;

public class ContractUser implements Serializable {
    public static final int USER = 0;
    public static final int EMPLOYEE = 1;
    public static final int MODERATOR = 2;
    public static final int ADMIN = 3;
    public static final int CREATOR = 0xff;

    //    private static final String base_uri = "./users_database/";
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
//    private Boolean waitingForComment = false;

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

//    public ContractUser(Integer id, String username, String firstname) {
//        if (!setId(id)) {
//            this.username = username;
//            this.firstname = firstname;
//        }
//        if (state == null
//                || state.isEmpty())
//            setState("/");
//    }
//
//


    public ContractUser(String username, String firstname, String lastname, Integer messageId) {
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

    //    private boolean tryLoad() {
//        String fileName = base_uri + id;
//        if (!Files.exists(Paths.get(fileName))) // nothing to load
//            return false;
//
//        try {
//            String fileData = new String(Files.readAllBytes(Paths.get(fileName))); // Read from file
//            JSONObject object = new JSONObject(fileData);
//
//            id = object.getInt("id");
//            if (object.has("username"))
//                username = object.getString("username");
//            if (object.has("firstname"))
//                firstname = object.getString("firstname");
//            if (object.has("state"))
//                state = object.getString("state");
//            else
//                state = "/";
//            if (object.has("messageId"))
//                messageId = object.getInt("messageId");
//            else
//                messageId = null;
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return false;
//        }
//
//        Log.Info(fileName + " is valid ContractUser JSON");
//
//        File dir = new File(base_uri);
//        File[] files = dir.listFiles((dir1, name) -> {
//            Log.Info("Looking at file '" + name + "'");
//            return name.startsWith(id.toString());
//        });
//        if (files != null)
//            for (File file : files) {
//                if (!file.exists() || !file.isDirectory())
//                    continue;
//                File[] contents = file.listFiles();
//                if (contents == null || contents.length == 0)
//                    continue;
//                Log.Info("Found " + contents.length + " contracts in " + file.getPath());
//                for (File testContract : contents) {
//                    if (testContract.isFile()) {
//                        Contract contract = new Contract();
//                        if (contract.loadFrom(testContract.getPath())) {
//                            if (!contracts.contains(contract)) {
//                                contracts.add(contract);
//                                Log.Info(testContract.getPath() + " successfully loaded contract", Log.VERBOSE);
//                            } else
//                                Log.Info("Contracts list already has contract №" + contract.getId(), Log.VERBOSE);
//                        } else {
//                            Log.Info(testContract.getPath() + " failed loading contract", Log.VERBOSE);
//                        }
//                    } else {
//                        Log.Info(testContract.getPath() + " is not a file", Log.VERBOSE);
//                    }
//                }
//
//            }
//        return true;
//    }

//    public Contract getUnAppliedContract() {
//        if (hasContracts()) {
//            for (Contract item : contracts)
//                if (!item.isFreshNew())
//                    return item;
//        }
//        Contract contract = new Contract();
//        contracts.add(contract);
//        return contract;
//    }

//    public void save() {
//        HashMap<String, Object> dataset = new HashMap<>();
//        dataset.put("id", id);
//        dataset.put("username", username);
//        dataset.put("firstname", firstname);
//        dataset.put("state", state);
//        dataset.put("messageId", messageId);
//
//        JSONObject object = new JSONObject(dataset);
//
//        String fileName = base_uri + id;
//
//        //start
//        Log.Info("Generating contract user " + fileName + " " + object.toString(), Log.EVERYTHING);
//
//        Path path = Paths.get(fileName);
//        File file = new File(path.getParent().toUri());
//
//
//        try {
//            if (file.mkdirs()
//                    || (file.exists()
//                    && file.isDirectory()))
//                Files.write(path, object.toString().getBytes());
//            else
//                System.err.println("Cannot get access " + file.getAbsolutePath() + " directory");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //end
//    }


//    public void setComment(String handleMessage) {
//        Contract contract = getUnAppliedContract();
//        if (!contract.isFreshNew())
//            contract.setComment(handleMessage);
//    }


}