package com.redcreator37.playermanagement.DataModels;

import java.util.Objects;
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

    /**
     * Provides hash code functionality
     *
     * @return this object's hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getUuid());
    }

}
