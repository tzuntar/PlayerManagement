package com.redcreator37.playermanagement.DataModels;

public class PlayerTag {

    private final String username;

    private final String uuid;

    public PlayerTag(String username, String uuid) {
        this.username = username;
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getUuid() {
        return uuid;
    }
}
