/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package makelabs_bot.model;

import makelabs_bot.helper.Log;
import makelabs_bot.model.data_pojo.Contract;
import makelabs_bot.model.data_pojo.ContractUser;
import makelabs_bot.model.data_pojo.PostWorkData;
import org.telegram.telegrambots.meta.api.objects.User;

import java.sql.SQLException;
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

    public ContractUser getUser(Long id) {
        return databaseManager.getUserById(id);
    }

    public void setUser(ContractUser user) {
        if (user == null)
            return;
        try {
            databaseManager.saveUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer getMessageId(Long byUid) {
        return databaseManager.getMessageId(byUid);
    }

    public void setMessageId(Long uid, Integer messageId) {
        if (uid != null)
            databaseManager.saveMessageIdForUser(uid, messageId);
    }

    public PostWorkData getPostWorkData(String byUri, User userRequested) {
        PostWorkData postWorkData = databaseManager.getWorkData(byUri);
        analytics.updatePostWorkDataRequested(postWorkData, userRequested);
        return postWorkData;
    }

    public void setPostWorkData(PostWorkData postWorkData) {
        try {
            databaseManager.saveWorkData(postWorkData);
            Log.Info("Updating work data " + postWorkData.getDescription(), Log.MODEL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Contract getContract(Long byId) {
        return databaseManager.getContract(byId);
    }

    public PostWorkData getWorkData(Long byId) {
        return databaseManager.getWorkData(byId);
    }

    public void saveContract(Contract contract) {
        try {
            databaseManager.saveContract(contract);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Long getContractId(Contract contract) {
        return databaseManager.getContractId(contract);
    }

    public void saveWorkData(PostWorkData postWorkData) {
        try {
            databaseManager.saveWorkData(postWorkData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Long getWorkDataId(PostWorkData postWorkData) {
        return databaseManager.getWorkDataId(postWorkData);
    }

    public void saveContractUser(ContractUser contractUser) {
        try {
            databaseManager.saveUser(contractUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Contract> getAllUserContracts(ContractUser contractUser) {
        return databaseManager.getAllUserContracts(contractUser);
    }

    public Contract getUnapprovedContract(ContractUser contractUser) {
        return databaseManager.getUnapprovedContract(contractUser);
    }
}
