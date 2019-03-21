package data;

import java.io.Serializable;
import java.util.List;

import static data.InlinePathData.remLast;

public class ContractUser implements Serializable {
    private String username;
    private String firstname;
    private List<Contract> contracts;
    private String state;

    public ContractUser(String username, String firstname, List<Contract> contracts) {
        this.username = username;
        this.firstname = firstname;
        this.contracts = contracts;
        state = "/";
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

    public void goBack() {
        state = remLast(state);
    }
}