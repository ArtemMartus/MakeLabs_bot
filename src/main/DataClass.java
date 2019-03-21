package main;

import data.ContractUser;

import java.util.HashMap;
import java.util.Map;

public class DataClass {

    private final Map<Integer, ContractUser> contractUserMap;
    private final Map<Integer, Integer> messageMap;

    public DataClass() {
        contractUserMap = new HashMap<>();
        messageMap = new HashMap<>();
    }

    public ContractUser getUser(Integer id) {
        return contractUserMap.get(id);
    }

    public void setUser(Integer id, ContractUser user) {
        contractUserMap.put(id, user);
    }

    public Integer getMessageId(Integer uid) {
        return messageMap.get(uid);
    }

    public void setMessageId(Integer uid, Integer messageId) {
        messageMap.put(uid, messageId);
    }

}
