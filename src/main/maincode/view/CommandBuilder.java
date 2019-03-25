package maincode.view;

public class CommandBuilder {
    private final String currentState;
    private final String command;

    public CommandBuilder(String currentState, String command) {
        this.currentState = currentState;
        this.command = command;
    }

    public void doAction() {

    }

    public String getURI() {
        return currentState + command;
    }
}
