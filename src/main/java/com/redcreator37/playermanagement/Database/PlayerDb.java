package com.redcreator37.playermanagement.Database;

import com.redcreator37.playermanagement.DataModels.PlayerTag;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.PlayerManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Player-related database routines
 */
public class PlayerDb extends SharedDb<ServerPlayer, Map<UUID, ServerPlayer>> {

    /**
     * Constructs a new PlayerDb instance
     *
     * @param db the database connection
     */
    public PlayerDb(Connection db) {
        super(db);
    }

    /**
     * Executes the specified sql update query
     *
     * @param sql    the SQL command. Example: {@code INSERT INTO
     *               contacts (name, surname) VALUES (?, ?)}
     * @param player the player object to get the data from
     * @throws SQLException on errors
     */
    @Override
    void runSqlUpdate(String sql, ServerPlayer player, boolean update) throws SQLException {
        PreparedStatement st = db.prepareStatement(sql);
        st.closeOnCompletion();
        st.setString(1, player.getUsername());
        st.setString(2, player.getUuid().toString());
        st.setString(3, player.getName());
        st.setString(4, player.getJoinDate());
        if (player.getJob().isPresent())
            st.setString(5, player.getJob().get().getName());
        else st.setNull(5, Types.VARCHAR);
        if (player.getCompany().isPresent())
            st.setString(6, player.getCompany().get().getName());
        else st.setNull(6, Types.VARCHAR);
        if (player.getNotes().isPresent())
            st.setString(7, player.getNotes().get());
        else st.setNull(7, Types.VARCHAR);
        st.setInt(8, player.getPunishments());
        if (update) st.setInt(9, player.getId());
        st.executeUpdate();
    }

    /**
     * Runs the specified SQL query to return the list of
     * server players in the database
     *
     * @param sql sql query to run
     * @return the list of players in the database
     * @throws SQLException on errors
     */
    @Override
    public Map<UUID, ServerPlayer> commonQuery(String sql) throws SQLException {
        Statement st = db.createStatement();
        st.closeOnCompletion();
        ResultSet set = st.executeQuery(sql);
        return playerDataFromResultSet(set);
    }

    /**
     * Returns the list of all server players
     *
     * @return the player list
     * @throws SQLException on errors
     */
    @Override
    public Map<UUID, ServerPlayer> getAll() throws SQLException {
        String cmd = "SELECT players.* FROM players LEFT JOIN jobs ON jobs.name = players.job" +
                " LEFT JOIN companies ON companies.name = players.company;";
        return commonQuery(cmd);
    }

    /**
     * Returns the player with this UUID from the database
     *
     * @param uuid the UUID to look for
     * @return the matching {@link ServerPlayer} object
     * @throws SQLException on errors
     */
    public ServerPlayer getPlayerByUuid(UUID uuid) throws SQLException {
        PreparedStatement st = db.prepareStatement("SELECT * FROM players WHERE uuid = ?");
        st.setString(1, uuid.toString());
        st.closeOnCompletion();
        ResultSet set = st.executeQuery();
        Map<UUID, ServerPlayer> result = playerDataFromResultSet(set);
        return result.get(uuid);
    }

    /**
     * Iterates through this {@link ResultSet}, retrieves all {@link ServerPlayer}
     * objects inside and closes the set
     *
     * @param set the set to iterate through
     * @return a {@link Map} containing the ServerPlayer objects
     * @throws SQLException on errors
     */
    private Map<UUID, ServerPlayer> playerDataFromResultSet(ResultSet set) throws SQLException {
        Map<UUID, ServerPlayer> playerMap = new HashMap<>();
        while (set.next()) {
            ServerPlayer p = new ServerPlayer(set.getInt("id"),
                    new PlayerTag(set.getString("username"),
                            UUID.fromString(set.getString("uuid"))));
            p.setName(set.getString("name"));
            p.setJoinDate(set.getString("join_date"));
            p.setJob(PlayerManagement.jobs.get(set.getString("job")));
            p.setCompany(PlayerManagement.companies.get(set.getString("company")));
            p.setNotes(set.getString("notes"));
            p.setPunishments(set.getInt("punishments"));
            playerMap.put(p.getUuid(), p);
        }
        set.close();
        return playerMap;
    }

    /**
     * Adds another player to the database
     *
     * @param player the ServerPlayer object to be inserted
     * @throws SQLException on errors
     */
    @Override
    public void insert(ServerPlayer player) throws SQLException {
        String cmd = "INSERT INTO players(username, uuid, name, join_date," +
                "job, company, notes, punishments) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        runSqlUpdate(cmd, player, false);
    }

    /**
     * Updates the data of an existing player in the database
     *
     * @param player the ServerPlayer object to be updated
     * @throws SQLException on errors
     */
    @Override
    public void update(ServerPlayer player) throws SQLException {
        String cmd = "UPDATE players SET username = ?, uuid = ?, name = ?," +
                "join_date = ?, job = ?, company = ?, notes = ?, punishments = ? " +
                "WHERE id = ?";
        runSqlUpdate(cmd, player, true);
    }

    /**
     * Updates the data for this player and returns its updated version
     * from the database
     *
     * @param player the {@link ServerPlayer} to update
     * @return the updated version of the aforementioned {@link ServerPlayer}
     * @throws SQLException on errors or if the passed {@link ServerPlayer}
     *                      object doesn't exist in the database
     */
    public ServerPlayer updateAndGet(ServerPlayer player) throws SQLException {
        String cmd = "UPDATE players SET username = ?, uuid = ?, name = ?," +
                "join_date = ?, job = ?, company = ?, notes = ?, punishments = ? " +
                "WHERE id = ?";
        runSqlUpdate(cmd, player, true);
        ServerPlayer updated = getPlayerByUuid(player.getUuid());
        if (updated == null)
            throw new IllegalStateException("The updated value has been written but re-loading has failed");
        return updated;
    }

    /**
     * Removes this player from the database
     *
     * @param player the player to remove
     * @throws SQLException on errors
     */
    @Override
    public void remove(ServerPlayer player) throws SQLException {
        String cmd = "DELETE FROM players WHERE id = " + player.getId() + ";";
        db.prepareStatement(cmd).executeUpdate();
    }

}
