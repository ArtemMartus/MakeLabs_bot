/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.model;

import main.makelabs_bot.helper.Log;
import main.makelabs_bot.model.data_pojo.Contract;
import main.makelabs_bot.model.data_pojo.ContractUser;
import main.makelabs_bot.model.data_pojo.PostWorkData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseManagerTest {

    private static DatabaseManager databaseManager;

    @BeforeEach
    void setUp() {
        Log.setShowLevel(Log.EVERYTHING);
        DatabaseManager.databaseName = "testDatabase";
        databaseManager = DatabaseManager.getInstance();
    }

    @Test
    void getAllAppliedNotPaidContracts() {
        try {
            Contract contract = new Contract(1L, 5L, "test contract",
                    "#function1##function2#", "comment", 333.0f);
            databaseManager.saveContract(contract);
            Long contractId = databaseManager.getContractId(contract);
            contract = databaseManager.getContract(contractId);
            List<Contract> contracts = databaseManager.getAllAppliedNotPaidContracts();
            assertTrue(contracts.contains(contract));
            databaseManager.removeContract(contractId);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    @Test
    void saveUser() {
        try {
            ContractUser contractUser = new ContractUser(13372290L, "nigga", "jay");
            databaseManager.saveUser(contractUser);
            ContractUser get = databaseManager.getUserById(13372290L);
            assertEquals(get, contractUser);
            databaseManager.removeUser(contractUser.getId());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void getMessageId() {
        try {
            ContractUser contractUser = new ContractUser(13372290, "nigga", "jay", "z", 134);
            databaseManager.saveUser(contractUser);
            Integer messageId = databaseManager.getMessageId(contractUser.getId());
            assertEquals(messageId, contractUser.getMessageId());
            databaseManager.removeUser(contractUser.getId());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void saveMessageIdForUser() {
        try {
            ContractUser contractUser = new ContractUser(13372290, "nigga", "jay", "z");
            databaseManager.saveUser(contractUser);
            databaseManager.saveMessageIdForUser(contractUser.getId(), 431);
            Integer messageId = databaseManager.getMessageId(contractUser.getId());
            assertEquals(431, messageId);
            databaseManager.removeUser(contractUser.getId());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void getWorkData() {
        try {
            PostWorkData postWorkData = new PostWorkData("{}", "my description", 1L, "/hello");
            databaseManager.saveWorkData(postWorkData);
            Long dataId = databaseManager.getWorkDataId(postWorkData);
            PostWorkData got1 = databaseManager.getWorkData("/hello");
            PostWorkData got2 = databaseManager.getWorkData(dataId);
            assertEquals(got1, got2);
            databaseManager.removeWorkData("/hello");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void GetAllUserContracts() {
        try {
            ContractUser contractUser = new ContractUser(13372290, "nigga", "jay", "z");
            databaseManager.saveUser(contractUser);
            Contract contract = new Contract(13372290, 5L, "test contract",
                    "#function1##function2#", "comment", 333.0f);
            databaseManager.saveContract(contract);
            Contract contract2 = new Contract(13372290, 5L, "test contract2",
                    "#function31##functio3n2#", "comment2", 333.0f);
            databaseManager.saveContract(contract2);
            contract = databaseManager.getContract(databaseManager.getContractId(contract));
            contract2 = databaseManager.getContract(databaseManager.getContractId(contract2));
            List<Contract> contracts = databaseManager.getAllUserContracts(contractUser);
            for (Contract c : contracts)
                Log.Info(c.toString());
            assertTrue(contracts.contains(contract));
            assertTrue(contracts.contains(contract2));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}