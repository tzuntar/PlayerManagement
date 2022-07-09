package com.redcreator37.playermanagement.DataModels;

import java.util.UUID;

/**
 * Represents a combination tag containing the player's in-game
 * username and UUID
 */
public class PlayerTag {

    /**
     * The player's in-game username
     */
    private final String username;

    /**
     * The player's UUID
     */
    private final UUID uuid;

    /**
     * Constructs a new PlayerTag instance with this username
     * and uuid
     *
     * @param username the player's in-game username
     * @param uuid     the player's unique UUID
     */
    public PlayerTag(String username, UUID uuid) {
        this.username = username;
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof PlayerTag))
            return false;
        PlayerTag t = (PlayerTag) obj;
        return t.uuid.equals(this.uuid)
                && t.username.equals(this.username);
    }

    @Override
    public int hashCode() {
        int c = this.username.hashCode();
        c = 31 * c + this.uuid.hashCode();
        return c;
    }

}
