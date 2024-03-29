/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package makelabs_bot.viewmodel;

import makelabs_bot.model.Analytics;

public class BackgroundService implements Runnable {
    private final Thread thread;

    public BackgroundService() {
        thread = new Thread(this);
        thread.setName("background service");
        thread.setDaemon(true);
        thread.start();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true) {

            Analytics.getInstance().checkTime();

            try {
                Thread.sleep(333);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
