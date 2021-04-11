package com.redcreator37.playermanagement.Containers;

import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.PlayerManagement;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Contains containerized global server player data
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
     * @return the matching UUID, or {@code null} if the
     * player with this username wasn't found
     */
    public UUID uuidFromUsername(String username) {
        Optional<UUID> fetchedUuid = players.values().stream().filter(pl ->
                pl.getUsername().equals(username))
                .map(ServerPlayer::getUuid).findFirst();
        return fetchedUuid.orElse(null);
    }

    /**
     * Checks whether the player with this username hasn't been registered yet
     *
     * @param username the username to check
     * @return {@code true} if the player <strong>doesn't</strong> exist,
     * {@code false} otherwise
     */
    public boolean doesNotExist(String username) {
        return uuidFromUsername(username) == null;
    }

    /**
     * Returns all employees of this {@link Company} in the data container
     *
     * @param company the {@link Company} for which to get the employees
     * @return a {@link List} containing all employees
     */
    public List<ServerPlayer> getCompanyEmployees(Company company) {
        return players.values().stream()
                .filter(pl -> pl.getCompany().toString().equals(company.toString()))
                .collect(Collectors.toList());
    }

    /**
     * Updates this {@link ServerPlayer} entry in the database
     *
     * @param player the {@link ServerPlayer} to update
     * @throws SQLException on errors
     */
    public void updatePlayerEntry(ServerPlayer player) throws SQLException {
        ServerPlayer updated = PlayerManagement.playerDb.updateAndGet(player);
        this.players.remove(player.getUuid());
        this.players.put(updated.getUuid(), updated);
    }

    /**
     * Returns the {@link ServerPlayer} matching this UUID or {@code null}
     * if no such one was found
     *
     * @param uuid the UUID to look for
     * @return the {@link ServerPlayer} or {@code null}
     */
    public ServerPlayer byUuid(UUID uuid) {
        return players.get(uuid);
    }

    /**
     * Returns the {@link ServerPlayer} with this username or {@code null}
     * if no such one was found
     *
     * @param username the username to look for
     * @return the {@link ServerPlayer} or {@code null}
     */
    public ServerPlayer byUsername(String username) {
        return this.players.get(uuidFromUsername(username));
    }

    public void setByUuid(UUID uuid, ServerPlayer player) {
        players.put(uuid, player);
    }

    /**
     * Removes this player from the database and the data set
     *
     * @param player the {@link ServerPlayer} to remove
     * @throws SQLException on errors
     */
    public void removeByUuid(ServerPlayer player) throws SQLException {
        PlayerManagement.playerDb.remove(player);
        players.remove(player.getUuid());
    }

    public Map<UUID, ServerPlayer> getPlayers() {
        return players;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(players);
    }
}
