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

    public Integer getMessageId(Integer uid) {
        return databaseManager.getMessageIdBy(uid);
    }

    public void setMessageId(Integer uid, Integer messageId) {
        if (uid != null)
            databaseManager.saveMessageIdForUser(uid, messageId);
    }

    public PostWorkData getPostWorkData(String uri, User userRequested) {
        PostWorkData postWorkData = databaseManager.getWorkDataBy(uri);
        analytics.updatePostWorkDataRequested(postWorkData, userRequested);
        return postWorkData;
    }

    public void setPostWorkData(PostWorkData postWorkData) {
        Log.Info("Updating work data " + postWorkData.getDescription(), Log.MODEL);
        databaseManager.saveWorkData(postWorkData);
    }
    // todo complete this model

    public Contract getContract(Long byId) {
        //todo get contract from database
        return null;
    }

    public PostWorkData getWorkData(Long byId) {
        //todo get workdata by id
        return null;
    }

    public void saveContract(Contract contract) {
        //todo save contract to database if not exists or update current record by searching with uid and work_id
    }

    public Long getContractId(Contract contract) {
        //todo load contract id from database
        return null;
    }

    public void saveWorkData(PostWorkData postWorkData) {
        //todo save work data_pojo to database or update existing record
    }


    public Long getWorkDataId(PostWorkData postWorkData) {
        //todo retrieve id from database
        return null;
    }
}
