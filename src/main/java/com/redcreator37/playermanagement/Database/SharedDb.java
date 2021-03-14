package com.redcreator37.playermanagement.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Common database routines
 *
 * @param <T> the type of the data to operate on
 * @param <R> the result that'll be returned by data retrieval
 *            methods (used to allow different return values than maps)
 */
public abstract class SharedDb<T, R> {

    final Connection db;

    /**
     * Constructs a new SharedDb instance
     *
     * @param db the database connection
     */
    public SharedDb(Connection db) {
        this.db = db;
    }

    /**
     * Attempts to connect to the specified database
     *
     * @param database database path
     * @return the open database connection
     * @throws SQLException on errors
     */
    public static Connection connect(String database) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:sqlite:" + database);
        con.setAutoCommit(true);
        return con;
    }

    /**
     * Executes this sql update query
     *
     * @param sql    the SQL command. Example: <code>INSERT INTO
     *               contacts(name, surname) VALUES(?,?)</code>
     * @param t      the object containing the data
     * @param update controls whether to run an update or an insert
     *               operation
     * @throws SQLException on errors
     */
    abstract void runSqlUpdate(String sql, T t, boolean update) throws SQLException;

    /**
     * Runs this sql query and returns the list of found objects in
     * the database
     *
     * @param sql the query to run
     * @return the list of objects in the database
     * @throws SQLException on errors
     */
    abstract R commonQuery(String sql) throws SQLException;

    /**
     * Returns the list of all objects in the database
     *
     * @return the list of objects
     * @throws SQLException on errors
     */
    public abstract R getAll() throws SQLException;

    /**
     * Inserts this object into the database
     *
     * @param t the object to insert
     * @throws SQLException on errors
     */
    public abstract void insert(T t) throws SQLException;

    /**
     * Updates the data of an existing object in the database
     *
     * @param t the object to update
     * @throws SQLException on errors
     */
    public abstract void update(T t) throws SQLException;

    /**
     * Removes this object from the database
     *
     * @param t the object to remove
     * @throws SQLException on errors
     */
    public abstract void remove(T t) throws SQLException;

    /**
     * Creates possibly nonexistent database tables
     *
     * @param con       database connection
     * @param sqlStream the stream to the file which contains SQL
     *                  queries to execute to generate the tables
     * @throws SQLException on errors
     */
    public static void createTables(Connection con, InputStream sqlStream) throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(sqlStream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
            if (line.startsWith("--")) {
                con.prepareStatement(builder.toString()).execute();
                builder = new StringBuilder();
            } else builder.append(line).append(" ");
    }

}
