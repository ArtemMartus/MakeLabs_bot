/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package makelabs_bot.view;

public interface MessageHandler {
    void handle();

    boolean isValid();
}
