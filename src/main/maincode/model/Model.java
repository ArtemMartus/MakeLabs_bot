package maincode.model;

import maincode.data.ContractUser;
import maincode.data.PostWorkData;
import maincode.helper.Log;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
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
    }

    public ContractUser getUser(Integer id) {
        analytics.checkTime();
        return contractUserMap.get(id);
    }

    public void setUser(Integer id, ContractUser user) {
        contractUserMap.put(id, user);
        analytics.checkTime();
    }

    public Integer getMessageId(Integer uid) {
        analytics.checkTime();

        return messageMap.get(uid);
    }

    public void setMessageId(Integer uid, Integer messageId) {
        if (uid != null)
            messageMap.put(uid, messageId);
        analytics.checkTime();

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
        analytics.checkTime();
    }

}
