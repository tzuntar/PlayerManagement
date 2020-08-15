package com.redcreator37.playermanagement.DataModels;

import com.redcreator37.playermanagement.PlayerRoutines;

import java.util.Map;
import java.util.Objects;

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
    private final String uuid;

    /**
     * Constructs a new PlayerTag instance with this username
     * and uuid
     *
     * @param username the player's in-game username
     * @param uuid     the player's unique UUID
     */
    public PlayerTag(String username, String uuid) {
        this.username = username;
        this.uuid = uuid;
    }

    /**
     * A convenience constructor which fetches the UUID for the
     * player with this username from the database.
     * <br>
     * Note: for this action to succeed, the player must already be
     * registered in the database.
     * <br>
     * This constructor is provided only for convenience purposes
     * and is not to be user in place of the normal constructor.
     *
     * @param username   the player's username
     * @param playerList the list of all players in the database
     */
    public PlayerTag(String username, Map<String, ServerPlayer> playerList) {
        this.username = username;
        this.uuid = PlayerRoutines.uuidFromUsername(playerList, username);
    }

    public String getUsername() {
        return username;
    }

    public String getUuid() {
        return uuid;
    }

    /**
     * Provides hash code functionality
     *
     * @return this object's hash code
     */
    @Override
    public int hashCode() {
        int result = 17;
        result *= 37 + Objects.hashCode(getUsername());
        result *= 37 + Objects.hashCode(getUuid());
        return result;
    }

}
