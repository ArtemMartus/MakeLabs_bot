package main.makelabs_bot.view;

public interface MessageHandler {
    void handle();

    boolean isValid();
}