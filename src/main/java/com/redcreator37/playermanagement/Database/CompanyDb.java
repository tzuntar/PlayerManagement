package com.redcreator37.playermanagement.Database;

import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.PlayerTag;
import com.redcreator37.playermanagement.PlayerManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Company-related database routines
 */
public class CompanyDb extends SharedDb<Company, Map<String, Company>> {

    /**
     * Constructs a new CompanyDb instance
     *
     * @param db the database connection
     */
    public CompanyDb(Connection db) {
        super(db);
    }

    /**
     * Executes the specified sql update query
     *
     * @param sql the SQL command. Example: <code>INSERT INTO
     *            contacts (name, surname) VALUES (?, ?)</code>
     * @param c   the Company object to get the data from
     * @throws SQLException on errors
     */
    @Override
    void runSqlUpdate(String sql, Company c, boolean update) throws SQLException {
        PreparedStatement st = db.prepareStatement(sql);
        st.closeOnCompletion();
        st.setString(1, c.getName());
        st.setString(2, c.getDescription());
        st.setString(3, c.getBalance().toString());
        st.setInt(4, c.getEmployees());
        st.setString(5, c.getOwner().getUuid());
        st.setString(6, c.getEstablishedDate());
        st.setString(7, c.getWage().toString());
        if (update) st.setInt(8, c.getId());
        st.executeUpdate();
    }

    /**
     * Runs this sql query and returns the list of found objects in
     * the database
     *
     * @param sql the query to run
     * @return the list of objects in the database
     * @throws SQLException on errors
     */
    @Override
    Map<String, Company> commonQuery(String sql) throws SQLException {
        Map<String, Company> companies = new HashMap<>();
        Statement st = db.createStatement();
        st.closeOnCompletion();
        ResultSet set = st.executeQuery(sql);

        // loop through the records
        while (set.next()) {
            String uuid = set.getString("owner");
            Company c = new Company(set.getInt("id"),
                    set.getString("name"),
                    set.getString("description"),
                    set.getString("money"),
                    set.getInt("employees"),
                    new PlayerTag(Bukkit.getOfflinePlayer(UUID
                            .fromString(uuid)).getName(), uuid),
                    set.getString("established"),
                    set.getString("paycheck"));
            companies.put(c.getName(), c);
        }
        set.close();
        return companies;
    }

    /**
     * Returns the list of all companies in the database
     *
     * @return the company list
     * @throws SQLException on errors
     */
    @Override
    public Map<String, Company> getAll() throws SQLException {
        return commonQuery("SELECT * FROM companies");
    }

    /**
     * Returns the list of companies owned by the player with
     * this UUID
     *
     * @param uuid the owner's UUID
     * @return the list of matching companies
     * @throws SQLException on errors
     */
    public Map<String, Company> getCompaniesByOwner(String uuid) throws SQLException {
        return commonQuery("SELECT * FROM companies WHERE owner = '" + uuid + "'");
    }

    /**
     * Adds another company to the database
     *
     * @param c the Company object to be inserted
     * @throws SQLException on errors
     */
    @Override
    public void insert(Company c) throws SQLException {
        String cmd = "INSERT INTO companies(name, description, money," +
                " employees, owner, established, paycheck) VALUES(?, ?, ?, ?, ?, ?, ?)";
        runSqlUpdate(cmd, c, false);
    }

    /**
     * Updates the data of an existing company in the database
     *
     * @param c the Company object to be updated
     * @throws SQLException on errors
     */
    @Override
    public void update(Company c) throws SQLException {
        String cmd = "UPDATE companies SET name = ?, description = ?, money = ?," +
                " employees = ?, owner = ?, established = ?, paycheck = ? WHERE id = ?";
        runSqlUpdate(cmd, c, true);
    }

    /**
     * Removes this company from the database
     *
     * @param company the company to remove
     * @throws SQLException on errors
     */
    @Override
    public void remove(Company company) throws SQLException {
        String cmd = "DELETE FROM companies WHERE id = " + company.getId() + ";";
        db.prepareStatement(cmd).executeUpdate();
    }

    /**
     * Updates the data for this company in the database
     *
     * @param player  the player that'll see any output
     * @param company the company to update
     */
    public void updateByPlayer(Player player, Company company) {
        try {
            update(company);
            PlayerManagement.companies = getAll();
            player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + PlayerManagement.strings.getString("company-data-saved"));
        } catch (SQLException ex) {
            player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + PlayerManagement.strings.getString("error-saving-company-data")
                    + ChatColor.RED + ex.getMessage());
        }
    }

}
