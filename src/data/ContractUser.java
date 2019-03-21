package data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static controllers.PostWorkController.remLast;

public class ContractUser implements Serializable {
    private static final String base_uri = "./users_database/";
    private Integer id;
    private String username;
    private String firstname;
    private List<Contract> contracts = new ArrayList<>();
    private String state;

    public ContractUser(Integer id) {
        this.id = id;
        tryLoad();
    }

    public ContractUser(Integer id, String username, String firstname, List<Contract> contracts) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.contracts = contracts;
        state = "/";
    }

    public ContractUser(Integer id, String username, String firstname, List<Contract> contracts, String state) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.contracts = contracts;
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
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

    private void tryLoad() {
        String fileName = base_uri + id;
        if (!Files.exists(Paths.get(fileName))) // nothing to load
            return;

        try {
            String fileData = new String(Files.readAllBytes(Paths.get(fileName))); // Read from file
            JSONObject object = new JSONObject(fileData);
            username = object.getString("username");
            firstname = object.getString("firstname");
            state = object.getString("state");

            if (object.has("objects")) {
                JSONArray jsonArray = object.getJSONArray("objects");
                for (int i = 0; i < jsonArray.length(); ++i) {

                    Map<String, Object> objMap = jsonArray.getJSONObject(i).toMap();
                    Set<String> keys = objMap.keySet();

                    System.out.println(jsonArray.getJSONObject(i).toString());

                    for (String key : keys) {
                        String name = (String) objMap.get("name");
                        String additional = (String) objMap.get("additional");
                        String comment = (String) objMap.get("comment");
                        Integer price = (Integer) objMap.get("price");
                        Boolean applied = (Boolean) objMap.get("applied");

                        contracts.add(new Contract(name, additional, comment, price, applied));
                        Log.Info("Loaded contract " + name + " " + price + "₴ for user " + username + "[" + id + "]|" + firstname);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        Log.Info(fileName + " is valid ContractUser JSON");
    }

    public void save() {
        HashMap<String, String> testSr = new HashMap<>();

        testSr.put("username", username);
        testSr.put("firstname", firstname);
        testSr.put("state", state);

        JSONObject object = new JSONObject(testSr);
        object.put("objects", contracts);

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