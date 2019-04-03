/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.view;

public interface MessageHandler {
    void handle();

    boolean isValid();
}
