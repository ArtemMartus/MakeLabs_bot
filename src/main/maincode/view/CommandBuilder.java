package maincode.view;

import maincode.controllers.PostWorkController;

public class CommandBuilder {
    private final String currentState;
    private final String command;

    public CommandBuilder(String currentState, String command) {
        this.currentState = currentState;
        this.command = command;
    }

    public String getURI() {
        String base = currentState;
        if (!base.endsWith("/"))
            base += "/";
        if (command.startsWith("/") && command.length() > 1)
            base += command.substring(1);
        else if (!command.equals("/"))
            base += command;
        return base;
    }

    public String getValidURI() {
        String uri = getURI();
        return PostWorkController.validifyPath(uri);
    }

    public String getCommand() {
        String uri = getURI();
        int lastSlash = uri.lastIndexOf("/");
        return uri.substring(lastSlash + 1);
    }
}
