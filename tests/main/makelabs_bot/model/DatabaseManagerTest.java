/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.model;

import main.makelabs_bot.model.data_pojo.Contract;
import main.makelabs_bot.model.data_pojo.ContractUser;
import main.makelabs_bot.model.data_pojo.PostWorkData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseManagerTest {

    private static DatabaseManager databaseManager;

    @BeforeEach
    void setUp() {
        DatabaseManager.databaseName = "testDatabase";
        databaseManager = DatabaseManager.getInstance();
    }

    @Test
    void getAllAppliedNotPaidContracts() {
        Contract contract = new Contract(1337, 371, "test contract",
                "#function1##function2#", "comment", 333);
        databaseManager.saveContract(contract);
        Long contractId = databaseManager.getContractId(contract);
        contract = databaseManager.getContract(contractId);
        List<Contract> contracts = databaseManager.getAllAppliedNotPaidContracts();
        assertTrue(contracts.contains(contract));
        databaseManager.removeContract(contractId);
    }


    @Test
    void saveUser() {
        ContractUser contractUser = new ContractUser(13372290, "nigga", "jay", "z", 134);
        databaseManager.saveUser(contractUser);
        ContractUser get = databaseManager.getUserById(13372290);
        assertEquals(get, contractUser);
        databaseManager.removeUser(contractUser.getId());
    }

    @Test
    void getMessageId() {
        ContractUser contractUser = new ContractUser(13372290, "nigga", "jay", "z", 134);
        databaseManager.saveUser(contractUser);
        Integer messageId = databaseManager.getMessageId(contractUser.getId());
        assertEquals(messageId, contractUser.getMessageId());
        databaseManager.removeUser(contractUser.getId());
    }

    @Test
    void saveMessageIdForUser() {
        ContractUser contractUser = new ContractUser(13372290, "nigga", "jay", "z", 134);
        databaseManager.saveUser(contractUser);
        databaseManager.saveMessageIdForUser(contractUser.getId(), 431);
        Integer messageId = databaseManager.getMessageId(contractUser.getId());
        assertEquals(431, messageId);
        databaseManager.removeUser(contractUser.getId());
    }

    @Test
    void getWorkData() {
        PostWorkData postWorkData = new PostWorkData("{}", "my description", 28L, "test/");
        databaseManager.saveWorkData(postWorkData);
        Long dataId = databaseManager.getWorkDataId(postWorkData);
        PostWorkData got1 = databaseManager.getWorkData("test/");
        PostWorkData got2 = databaseManager.getWorkData(dataId);
        assertEquals(got1, got2);
        databaseManager.removeWorkData("test/");
    }

}