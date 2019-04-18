/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package services.creator;

class AdminClient {
    public Long hashToken;
    public Long sessionId;
    public String username;
    public String password;

    public AdminClient(String username, String password, Long hashToken) {
        this.hashToken = hashToken;
        sessionId = 137913791379L;
        this.username = username;
        this.password = password;
    }
}