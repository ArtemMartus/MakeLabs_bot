package maincode.view;

import maincode.controllers.PostWorkController;

public class CommandBuilder implements MessageHandler {
    private String currentState;
    private String command;
    private String uri;
    private String validUri;
    private String validCommand;
    private boolean handled = false;

    public CommandBuilder(String currentState, String command) {
        this.currentState = currentState;
        this.command = command;

        //debug?
        handle();
    }

    public String getURI() {
        return uri;
    }

    public String getValidURI() {
        return validUri;
    }

    public String getCommand() {
        return validCommand;
    }

    public void setHome() {
        uri = validUri = "/";
        validCommand = "";
    }

    public void clearCommand() {
        validCommand = "";
    }

    public boolean baseUrlValid() {
        return validUri.equals(uri);
    }

    public void updateState(String state) {
        uri = state;
        if (uri != null
                && !uri.endsWith("/"))
            uri += "/";
        if (command != null) {
            if (command.startsWith("/")
                    && command.length() > 1)
                uri += command.substring(1);
            else if (!command.equals("/"))
                uri += command;

            validUri = PostWorkController.validifyPath(uri);

            int lastSlash = uri.lastIndexOf("/");
            validCommand = uri.substring(lastSlash + 1);
        } else {
            setHome();
        }
    }

    @Override
    public void handle() {
        if (handled)
            return;

        updateState(currentState);
        handled = true;
    }

    @Override
    public boolean isValid() {
        return currentState != null
                && !currentState.isEmpty()
                && command != null
                && !command.isEmpty();
    }

}
