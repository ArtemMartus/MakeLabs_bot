package data;

import org.json.JSONArray;
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
import java.util.Map;

import static controllers.PostWorkController.remLast;

public class ContractUser implements Serializable {
    private static final String base_uri = "./users_database/";
    private Integer id;
    private String username;
    private String firstname;
    private List<Contract> contracts = new LinkedList<>();
    private String state;
    private int messageId;

    public ContractUser(Integer id) {
        setId(id);
    }

    public ContractUser(Integer id, String username, String firstname) {
        if (!setId(id)) {
            this.username = username;
            this.firstname = firstname;
            state = "/";
        }
    }

    public ContractUser(Integer id, String username, String firstname, List<Contract> contracts, String state) {
        if (!setId(id)) {
            this.username = username;
            this.firstname = firstname;
            this.contracts = contracts;
            this.state = state;
        }
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

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
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

    public void goBack() {
        state = remLast(state);
    }

    private boolean tryLoad() {
        String fileName = base_uri + id;
        if (!Files.exists(Paths.get(fileName))) // nothing to load
            return false;

        try {
            String fileData = new String(Files.readAllBytes(Paths.get(fileName))); // Read from file
            JSONObject object = new JSONObject(fileData);

            id = object.getInt("id");
            username = object.getString("username");
            firstname = object.getString("firstname");
            state = object.getString("state");
            messageId = object.getInt("messageId");

            if (object.has("contracts")) {
                JSONArray jsonArray = object.getJSONArray("contracts");
                for (int i = 0; i < jsonArray.length(); ++i) {

                    Map<String, Object> objMap = jsonArray.getJSONObject(i).toMap();

                    Log.Info(jsonArray.getJSONObject(i).toString(), Log.VERBOSE);

                    String additional = (String) objMap.get("additional");
                    String name = (String) objMap.get("name");
                    String comment = (String) objMap.get("comment");
                    Integer price = (Integer) objMap.get("price");
                    Integer id = (Integer) objMap.get("id");
                    Boolean applied = (Boolean) objMap.get("applied");

                    contracts.add(new Contract(name, additional, comment, price, applied, id));
                    Log.Info("Loaded contract " + name + " " + price + "₴ for user " + username + "[" + id + "]|" + firstname);

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        Log.Info(fileName + " is valid ContractUser JSON");
        return true;
    }

    public Contract getUnAppliedContract() {
        if (hasContracts()) {
            for (Contract contract : contracts)
                if (!contract.getApplied())
                    return contract;
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
        dataset.put("contracts", contracts);
        if (hasContracts()) {
            Log.Info("Saving contracts into json");
        }
        dataset.put("state", state);
        dataset.put("messageId", messageId);

        JSONObject object = new JSONObject(dataset);

        String fileName = base_uri + id;

        Log.Info("Generating form " + fileName + " " + object.toString(), Log.VERBOSE);

        Path path = Paths.get(fileName);
        File file = new File(path.getParent().toUri());
        file.mkdirs();

        try {
            Files.write(path, object.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}