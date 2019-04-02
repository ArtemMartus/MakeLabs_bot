package main.maincode.makelabs_bot.data;

import main.maincode.makelabs_bot.helper.Log;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static main.maincode.makelabs_bot.controllers.PostWorkController.remLast;

public class ContractUser implements Serializable {
    private static final String base_uri = "./users_database/";
    private Integer id;
    private String username;
    private String firstname;
    private List<Contract> contracts = new LinkedList<>();
    private String state;
    private Integer messageId;
    private Boolean waitingForComment = false;

    public ContractUser(Integer id, String username, String firstname) {
        if (!setId(id)) {
            this.username = username;
            this.firstname = firstname;
        }
        if (state == null
                || state.isEmpty())
            setState("/");
    }


    public Integer getId() {
        return id;
    }

    public boolean setId(Integer id) {
        this.id = id;
        return tryLoad();
    }

    public String getUsername() {
        return username;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean hasContracts() {
        return contracts != null && contracts.size() > 0;
    }

    public String goBack() {
        state = remLast(state);
        return state;
    }

    public Boolean getWaitingForComment() {
        return waitingForComment;
    }

    public void setWaitingForComment(Boolean waitingForComment) {
        this.waitingForComment = waitingForComment;
    }

    private boolean tryLoad() {
        String fileName = base_uri + id;
        if (!Files.exists(Paths.get(fileName))) // nothing to load
            return false;

        try {
            String fileData = new String(Files.readAllBytes(Paths.get(fileName))); // Read from file
            JSONObject object = new JSONObject(fileData);

            id = object.getInt("id");
            if (object.has("username"))
                username = object.getString("username");
            if (object.has("firstname"))
                firstname = object.getString("firstname");
            if (object.has("state"))
                state = object.getString("state");
            else
                state = "/";
            if (object.has("messageId"))
                messageId = object.getInt("messageId");
            else
                messageId = null;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        Log.Info(fileName + " is valid ContractUser JSON");

        File dir = new File(base_uri);
        File[] files = dir.listFiles((dir1, name) -> {
            Log.Info("Looking at file '" + name + "'");
            return name.startsWith(id.toString());
        });
        if (files != null)
            for (File file : files) {
                if (!file.exists() || !file.isDirectory())
                    continue;
                File[] contents = file.listFiles();
                if (contents == null || contents.length == 0)
                    continue;
                Log.Info("Found " + contents.length + " contracts in " + file.getPath());
                for (File testContract : contents) {
                    if (testContract.isFile()) {
                        Contract contract = new Contract();
                        if (contract.loadFrom(testContract.getPath())) {
                            if (!contracts.contains(contract)) {
                                contracts.add(contract);
                                Log.Info(testContract.getPath() + " successfully loaded contract", Log.VERBOSE);
                            } else
                                Log.Info("Contracts list already has contract №" + contract.getId(), Log.VERBOSE);
                        } else {
                            Log.Info(testContract.getPath() + " failed loading contract", Log.VERBOSE);
                        }
                    } else {
                        Log.Info(testContract.getPath() + " is not a file", Log.VERBOSE);
                    }
                }

            }
        return true;
    }

    public Contract getUnAppliedContract() {
        if (hasContracts()) {
            for (Contract item : contracts)
                if (!item.isFreshNew())
                    return item;
        }
        Contract contract = new Contract();
        contracts.add(contract);
        return contract;
    }

    public void save() {
        HashMap<String, Object> dataset = new HashMap<>();
        dataset.put("id", id);
        dataset.put("username", username);
        dataset.put("firstname", firstname);
//        dataset.put("contracts", contracts);//should not save contracts into ContractUser json
//        if (hasContracts()) {
//            Log.Info("Saving contracts into json", Log.VERBOSE);
//        }
        dataset.put("state", state);
        dataset.put("messageId", messageId);

        JSONObject object = new JSONObject(dataset);

        String fileName = base_uri + id;

        //start
        Log.Info("Generating contract user " + fileName + " " + object.toString(), Log.EVERYTHING);

        Path path = Paths.get(fileName);
        File file = new File(path.getParent().toUri());


        try {
            if (file.mkdirs()
                    || (file.exists()
                    && file.isDirectory()))
                Files.write(path, object.toString().getBytes());
            else
                System.err.println("Cannot get access " + file.getAbsolutePath() + " directory");
            // TODO remove same code in contract writeTo
        } catch (IOException e) {
            e.printStackTrace();
        }
        //end
    }

    @Override
    public String toString() {
        return "ContractUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstname='" + firstname + '\'' +
                ", contracts=" + contracts +
                ", state='" + state + '\'' +
                ", messageId=" + messageId +
                ", waitingForComment=" + waitingForComment +
                '}';
    }

    public void setComment(String handleMessage) {
        Contract contract = getUnAppliedContract();
        if (!contract.isFreshNew())
            contract.setComment(handleMessage);
    }


}