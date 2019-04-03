/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.model;

import main.makelabs_bot.data.Contract;
import main.makelabs_bot.data.ContractUser;
import main.makelabs_bot.data.PostWorkData;
import main.makelabs_bot.helper.Log;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Model {
    private final Map<Integer, ContractUser> contractUserMap;
    private final Map<Integer, Integer> messageMap;
    private final Analytics analytics;
    private HashMap<String, PostWorkData> dataset;

    public Model() {
        contractUserMap = new HashMap<>();
        messageMap = new HashMap<>();
        dataset = new HashMap<>();
        analytics = Analytics.getInstance();
        Log.Info("Model initialized");
    }

    public synchronized List<Contract> getAllOpenContracts() {
        List<Contract> contracts = new LinkedList<>();
        for (ContractUser user : contractUserMap.values())
            contracts.addAll(user.getContracts());
        return contracts;
    }

    public ContractUser getUser(Integer id) {
        return contractUserMap.get(id);
    }

    public void setUser(ContractUser user) {
        if (user == null)
            return;
        contractUserMap.put(user.getId(), user);
    }

    public Integer getMessageId(Integer uid) {
        return messageMap.get(uid);
    }

    public void setMessageId(Integer uid, Integer messageId) {
        if (uid != null)
            messageMap.put(uid, messageId);
    }

    public PostWorkData getPostWorkData(String uri, User userRequested) {
        PostWorkData postWorkData = dataset.get(uri);
        analytics.updatePostWorkDataRequested(postWorkData, userRequested);
        return postWorkData;
    }

    public void setPostWorkData(String uri, PostWorkData postWorkData) {
        Log.Info("Setting new URI " + uri + " to " + postWorkData.getDescription());
        dataset.put(uri, postWorkData);
    }

    public void saveContractUser(ContractUser contractUser) {
        contractUser.save();
        setUser(contractUser);
    }

}
