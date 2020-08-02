package com.redcreator37.playermanagement.Database;

import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.PlayerManagement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Company-related database routines
 */
public final class CompanyDb {

    /**
     * Noninstantiable
     */
    private CompanyDb() {
    }

    /**
     * Executes the specified sql update query
     *
     * @param sql the SQL command. Example: <code>INSERT INTO
     *            contacts (name, surname) VALUES (?, ?)</code>
     * @param c   the Company object to get the data from
     * @param db  the database connection
     * @throws SQLException on error
     */
    private static void runCompanySqlUpdate(String sql, Company c, Connection db, boolean update) throws SQLException {
        PreparedStatement st = db.prepareStatement(sql);
        st.closeOnCompletion();
        st.setString(1, c.getName());
        st.setString(2, c.getDescription());
        st.setString(3, c.getBalance().toString());
        st.setInt(4, c.getEmployees());
        st.setString(5, c.getOwner());
        st.setString(6, c.getEstablishedDate());
        st.setString(7, c.getPaycheck().toString());
        if (update) st.setInt(8, c.getId());
        st.executeUpdate();
    }

    /**
     * Returns the list of all companies in the database
     *
     * @param db the database connection
     * @return the company list
     * @throws SQLException on error
     */
    public static Map<String, Company> getAllCompanies(Connection db) throws SQLException {
        String cmd = "SELECT * FROM companies";
        Map<String, Company> companies = new HashMap<>();
        ResultSet set = db.createStatement().executeQuery(cmd);

        // loop through the records
        while (set.next()) {
            Company c = new Company(set.getInt("id"),
                    set.getString("name"),
                    set.getString("description"),
                    set.getString("money"),
                    set.getInt("employees"),
                    set.getString("owner"),
                    set.getString("established"),
                    set.getString("paycheck"));
            companies.put(c.getName(), c);
        }
        return companies;
    }

    /**
     * Adds another company to the database
     *
     * @param c  the Company object to be inserted
     * @param db the database connection
     * @throws SQLException on error
     */
    public static void insertCompany(Company c, Connection db) throws SQLException {
        String cmd = "INSERT INTO companies(name, description, money," +
                " employees, owner, established, paycheck) VALUES(?, ?, ?, ?, ?, ?, ?)";
        runCompanySqlUpdate(cmd, c, db, false);
    }

    /**
     * Updates the data of an existing company in the database
     *
     * @param c  the Company object to be updated
     * @param db the database connection
     * @throws SQLException on error
     */
    public static void updateCompany(Company c, Connection db) throws SQLException {
        String cmd = "UPDATE companies SET name = ?, description = ?, money = ?," +
                " employees = ?, owner = ?, established = ?, paycheck = ? WHERE id = ?";
        runCompanySqlUpdate(cmd, c, db, true);
    }

    /**
     * Updates the data for this company in the database
     *
     * @param player  the player that'll see any output
     * @param company the company to update
     */
    public static void updateCompanyData(Player player, Company company) {
        try {
            updateCompany(company, PlayerManagement.database);
            PlayerManagement.companies = getAllCompanies(PlayerManagement.database);
            player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "Company data saved.");
        } catch (SQLException ex) {
            player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "Error while saving company data: " + ChatColor.RED
                    + ex.getMessage());
        }
    }

}
