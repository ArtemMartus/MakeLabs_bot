/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.model;

import main.makelabs_bot.helper.Log;
import main.makelabs_bot.model.data_pojo.Contract;
import main.makelabs_bot.model.data_pojo.ContractUser;
import main.makelabs_bot.model.data_pojo.PostWorkData;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public class Model {

    private final Analytics analytics;
    private final DatabaseManager databaseManager = DatabaseManager.getInstance();

    public Model() {
        analytics = Analytics.getInstance();
        Log.Info("Model initialized", Log.MODEL);
    }

    public synchronized List<Contract> getAllAppliedNotPaidContracts() {
        return databaseManager.getAllAppliedNotPaidContracts();
    }

    public ContractUser getUser(Integer id) {
        return databaseManager.getUserById(id);
    }

    public void setUser(ContractUser user) {
        if (user == null)
            return;
        databaseManager.saveUser(user);
    }

    public Integer getMessageId(Integer byUid) {
        return databaseManager.getMessageId(byUid);
    }

    public void setMessageId(Integer uid, Integer messageId) {
        if (uid != null)
            databaseManager.saveMessageIdForUser(uid, messageId);
    }

    public PostWorkData getPostWorkData(String byUri, User userRequested) {
        PostWorkData postWorkData = databaseManager.getWorkData(byUri);
        analytics.updatePostWorkDataRequested(postWorkData, userRequested);
        return postWorkData;
    }

    public void setPostWorkData(PostWorkData postWorkData) {
        Log.Info("Updating work data " + postWorkData.getDescription(), Log.MODEL);
        databaseManager.saveWorkData(postWorkData);
    }

    public Contract getContract(Long byId) {
        return databaseManager.getContract(byId);
    }

    public PostWorkData getWorkData(Long byId) {
        return databaseManager.getWorkData(byId);
    }

    public void saveContract(Contract contract) {
        databaseManager.saveContract(contract);
    }

    public Long getContractId(Contract contract) {
        return databaseManager.getContractId(contract);
    }

    public void saveWorkData(PostWorkData postWorkData) {
        databaseManager.saveWorkData(postWorkData);
    }


    public Long getWorkDataId(PostWorkData postWorkData) {
        return databaseManager.getWorkDataId(postWorkData);
    }

    public void saveContractUser(ContractUser contractUser) {
        databaseManager.saveUser(contractUser);
    }

    public List<Contract> getAllUserContracts(ContractUser contractUser) {
        return databaseManager.getAllUserContracts(contractUser);
    }

    public Contract getUnapprovedContract(ContractUser contractUser) {
        return databaseManager.getUnapprovedContract(contractUser);
    }
}
