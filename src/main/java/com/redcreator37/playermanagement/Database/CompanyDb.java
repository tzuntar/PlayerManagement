package com.redcreator37.playermanagement.Database;

import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.PlayerManagement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
     * @param db  database path
     * @throws SQLException on error
     */
    private static void runCompanySqlUpdate(String sql, Company c, String db, boolean update) throws SQLException {
        Connection con = SharedDb.connect(db);
        PreparedStatement st = con.prepareStatement(sql);
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
        con.close();
    }

    /**
     * Returns the list of all companies in the database
     *
     * @param db database path
     * @return the company list
     * @throws SQLException on error
     */
    public static List<Company> getAllCompanies(String db) throws SQLException {
        String cmd = "SELECT * FROM companies";
        List<Company> companies = new ArrayList<>();
        Connection con = SharedDb.connect(db);
        Statement st = con.createStatement();
        ResultSet set = st.executeQuery(cmd);

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
            companies.add(c);
        }

        con.close();
        return companies;
    }

    /**
     * Adds another company to the database
     *
     * @param c  the Company object to be inserted
     * @param db database path
     * @throws SQLException on error
     */
    public static void insertCompany(Company c, String db) throws SQLException {
        String cmd = "INSERT INTO companies(name, description, money," +
                " employees, owner, established, paycheck) VALUES(?, ?, ?, ?, ?, ?, ?)";
        runCompanySqlUpdate(cmd, c, db, false);
    }

    /**
     * Updates the data of an existing company in the database
     *
     * @param c  the Company object to be updated
     * @param db database path
     * @throws SQLException on error
     */
    public static void updateCompany(Company c, String db) throws SQLException {
        String cmd = "UPDATE companies SET name = ?, description = ?, money = ?," +
                " employees = ?, owner = ?, established = ?, paycheck = ? WHERE id = ?";
        runCompanySqlUpdate(cmd, c, db, true);
    }

    /**
     * Returns the Company object with the matching name
     *
     * @param companies the list of all companies
     * @param entered   the entered string
     * @return the matching Company object, or null if the
     * company with this name wasn't found
     */
    public static Company getCompanyFromString(List<Company> companies, String entered) {
        return companies.stream().filter(c -> c.getName().equals(entered))
                .findFirst().orElse(null);
    }

    /**
     * Updates the data for this company in the database
     *
     * @param player  the player that'll see any output
     * @param company the company to update
     */
    public static void updateCompanyData(Player player, Company company) {
        try {
            updateCompany(company, PlayerManagement.databasePath);
            PlayerManagement.companies = getAllCompanies(PlayerManagement.databasePath);
            player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "Company data saved.");
        } catch (SQLException ex) {
            player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "Error while saving company data: " + ChatColor.RED
                    + ex.getMessage());
        }
    }

}
