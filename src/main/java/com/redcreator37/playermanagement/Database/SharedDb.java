package com.redcreator37.playermanagement.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Common database routines
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
     * @throws SQLException on error
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
    public abstract void runSqlUpdate(String sql, T t, boolean update) throws SQLException;

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
     * Create possibly nonexistent database tables
     *
     * @param db the database connection to use
     * @throws SQLException on error
     */
    public static void createTables(Connection db) throws SQLException {
        String sql = "create table if not exists players\n(\n" +
                "    id          integer not null\n" +
                "        constraint players_pk\n" +
                "            primary key autoincrement,\n" +
                "    username    text    not null,\n" +
                "    uuid        text    not null,\n" +
                "    name        text default '',\n" +
                "    join_date   text default '',\n" +
                "    job         text default ''\n" +
                "        constraint players_jobs_name_fk\n" +
                "            references jobs (name),\n" +
                "    company     text default ''\n" +
                "        constraint players_companies_name_fk\n" +
                "            references companies (name),\n" +
                "    notes       text default '',\n" +
                "    punishments int  default 0 not null\n);";
        db.prepareStatement(sql).execute();
        sql = "create table if not exists jobs\n(\n" +
                "    id          integer not null\n" +
                "        primary key autoincrement,\n" +
                "    name        text    not null,\n" +
                "    description text default ''\n);";
        db.prepareStatement(sql).execute();
        sql = "create table if not exists companies\n(\n" +
                "    id          integer not null\n" +
                "        constraint companies_pk\n" +
                "            primary key autoincrement,\n" +
                "    name        text    default 'N/A' not null,\n" +
                "    description text    default '',\n" +
                "    money       text    default '0' not null,\n" +
                "    employees   integer default 0,\n" +
                "    owner       text    default ''\n" +
                "        constraint companies_players_username_fk\n" +
                "            references players (username),\n" +
                "    established text    default '',\n" +
                "    paycheck    text    default '10'\n);";
        db.prepareStatement(sql).execute();
        sql = "create table if not exists transactions\n(\n" +
                "    id          integer not null\n" +
                "        constraint transactions_pk\n" +
                "            primary key autoincrement,\n" +
                "    companyId   integer not null\n" +
                "        references companies,\n" +
                "    direction   TEXT    not null,\n" +
                "    title       TEXT,\n" +
                "    description text,\n" +
                "    amount      TEXT default '0' not null);";
        db.prepareStatement(sql).execute();
    }

}
