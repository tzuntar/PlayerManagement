package com.redcreator37.playermanagement.Database;

import com.redcreator37.playermanagement.DataModels.Transaction;
import com.redcreator37.playermanagement.PlayerManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction-related database routines
 */
public class TransactionDb extends SharedDb<Transaction, List<Transaction>> {

    /**
     * Constructs a new TransactionDb instance
     *
     * @param db the database connection
     */
    public TransactionDb(Connection db) {
        super(db);
    }

    /**
     * Executes the specified sql update query
     *
     * @param sql the SQL command. Example: <code>INSERT INTO
     *            contacts (name, surname) VALUES (?, ?)</code>
     * @param t   the Transaction object to get the data from
     * @throws SQLException on error
     */
    @Override
    void runSqlUpdate(String sql, Transaction t, boolean update) throws SQLException {
        PreparedStatement st = db.prepareStatement(sql);
        st.closeOnCompletion();
        st.setInt(1, t.getCompanyId());
        st.setString(2, t.getDirection());
        st.setString(3, t.getTitle());
        st.setString(4, t.getDescription());
        st.setString(5, t.getAmount().toString());
        st.executeUpdate();
    }

    /**
     * Runs this sql query and returns the list of found objects in
     * the database
     *
     * @param sql the query to run
     * @return the list of objects in the database
     * @implNote not implemented
     */
    @Override
    List<Transaction> commonQuery(String sql) {
        return null;
    }

    /**
     * Returns the list of all transactions in the database
     *
     * @return the transaction list
     * @throws SQLException on error
     */
    @Override
    public List<Transaction> getAll() throws SQLException {
        String cmd = "SELECT * FROM transactions";
        List<Transaction> transactions = new ArrayList<>();
        ResultSet set = db.createStatement().executeQuery(cmd);

        // loop through the records
        while (set.next()) {
            Transaction t = new Transaction(set.getInt("id"),
                    set.getInt("companyId"),
                    set.getString("direction"),
                    set.getString("title"),
                    set.getString("description"),
                    new BigDecimal(set.getString("amount")));
            transactions.add(t);
        }
        return transactions;
    }

    /**
     * Adds another transaction to the database
     *
     * @param t the Transaction object to be inserted
     * @throws SQLException on error
     */
    @Override
    public void insert(Transaction t) throws SQLException {
        String cmd = "INSERT INTO transactions(companyId, direction, title," +
                "description, amount) VALUES(?, ?, ?, ?, ?)";
        runSqlUpdate(cmd, t, false);
    }

    /**
     * Updates the data of an existing object in the database
     *
     * @param transaction the object to updates
     * @implNote not implemented
     */
    @Override
    public void update(Transaction transaction) {
        // TODO: implement!
    }

    /**
     * Adds a transaction in the background
     *
     * @param p the player
     * @param t the transaction
     */
    public void addAsync(Player p, Transaction t) {
        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {
                insert(t);
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + "Transaction data saved.");
            } catch (SQLException e) {
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + "Error while saving transaction" +
                        " data: " + ChatColor.RED + e.getMessage());
            }
        });
    }

}
