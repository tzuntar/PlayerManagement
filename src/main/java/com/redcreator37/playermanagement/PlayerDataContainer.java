package com.redcreator37.playermanagement;

import com.redcreator37.playermanagement.DataModels.ServerPlayer;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * ToDo: complete this class and fix all related errors
 */
public class PlayerDataContainer {

    /**
     * Contains the data for all players on the server
     * The key is the player UUID, the value is the matching
     * ServerPlayer object
     */
    private final Map<UUID, ServerPlayer> players;

    /**
     * Constructs a new PlayerDataContainer instance
     *
     * @param players a filled player data map
     */
    public PlayerDataContainer(Map<UUID, ServerPlayer> players) {
        this.players = players;
    }

    /**
     * Returns the UUID of the ServerPlayer with the matching username
     *
     * @param username the entered username
     * @return the matching UUID string, or {@code null} if the
     * player with this username wasn't found
     */
    public String uuidFromUsername(String username) {
        // FIXME: migrate the return type from String to UUID
        return players.values().stream().filter(pl -> pl.getUsername().equals(username))
                .map(ServerPlayer::getUuid)
                .findFirst().orElse(null);
    }

    public ServerPlayer byUuid(UUID uuid) {
        return players.get(uuid);
    }

    public void setToUuid(UUID uuid, ServerPlayer player) {
        players.put(uuid, player);
    }

    public Map<UUID, ServerPlayer> getPlayers() {
        return players;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(players);
    }
}
