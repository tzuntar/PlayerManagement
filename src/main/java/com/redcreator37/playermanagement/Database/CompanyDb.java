package com.redcreator37.playermanagement.Database;

import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.PlayerTag;
import org.bukkit.Bukkit;

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
     * @param sql the SQL command. Example: {@code INSERT INTO
     *            contacts (name, surname) VALUES (?, ?)}
     * @param c   the Company object to get the data from
     * @throws SQLException on errors
     */
    @Override
    void runSqlUpdate(String sql, Company c, boolean update) throws SQLException {
        PreparedStatement st = db.prepareStatement(sql);
        st.closeOnCompletion();
        st.setString(1, c.toString());
        st.setString(2, c.getDescription());
        st.setString(3, c.getBalance().toString());
        st.setInt(4, c.getEmployees());
        if (c.getOwner().isPresent())
            st.setString(5, c.getOwner().get().getUuid().toString());
        else st.setNull(5, Types.VARCHAR);
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
        Statement st = db.createStatement();
        st.closeOnCompletion();
        return companyDataFromResultSet(st.executeQuery(sql));
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
     * Returns the company with this name from the database
     *
     * @param name the company name to look for
     * @return the matching Company object or {@code null} if
     * there are no matching companies
     * @throws SQLException on errors
     */
    public Company getByName(String name) throws SQLException {
        PreparedStatement st = db.prepareStatement("SELECT * FROM companies WHERE name = ?");
        st.setString(1, name);
        st.closeOnCompletion();
        ResultSet set = st.executeQuery();
        Map<String, Company> result = companyDataFromResultSet(set);
        return result.get(name);
    }

    /**
     * Traverses this result set and returns a {@link Map} with all
     * companies in this result set
     *
     * @param set the {@link ResultSet} to traverse
     * @return a {@link Map} containing all data in the set
     * @throws SQLException on errors
     */
    private Map<String, Company> companyDataFromResultSet(ResultSet set) throws SQLException {
        Map<String, Company> companyMap = new HashMap<>();
        while (set.next()) {
            UUID uuid = UUID.fromString(set.getString("owner"));
            PlayerTag ownerTag = new PlayerTag(Bukkit.getOfflinePlayer(uuid).getName(), uuid);
            Company c = new Company(set.getInt("id"),
                    set.getString("name"),
                    set.getString("description"),
                    set.getString("money"),
                    set.getInt("employees"),
                    ownerTag,
                    set.getString("established"),
                    set.getString("paycheck"));
            companyMap.put(c.toString(), c);
        }
        set.close();
        return companyMap;
    }

    /**
     * Returns the list of companies owned by the player with
     * this UUID
     *
     * @param uuid the owner's UUID
     * @return the list of matching companies
     * @throws SQLException on errors
     */
    public Map<String, Company> getCompaniesByOwner(UUID uuid) throws SQLException {
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
     * Updates this company instance in the database and returns its
     * updated version
     *
     * @param company the Company object to update
     * @return the updated version of this company object
     * @throws SQLException on errors
     */
    public Company updateAndGet(Company company) throws SQLException {
        String cmd = "UPDATE companies SET name = ?, description = ?, money = ?," +
                " employees = ?, owner = ?, established = ?, paycheck = ? WHERE id = ?";
        runSqlUpdate(cmd, company, true);
        Company updated = getByName(company.toString());
        if (updated == null)
            throw new IllegalStateException("The updated value has been written but re-loading has failed");
        return updated;
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

}
