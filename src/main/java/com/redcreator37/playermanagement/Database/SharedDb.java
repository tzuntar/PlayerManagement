package com.redcreator37.playermanagement.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Common database routines
 */
public final class SharedDb {

    /**
     * Noninstantiable
     */
    private SharedDb() {
    }

    /**
     * Attempts to connect to the specified database
     *
     * @param database database path
     * @throws SQLException on error
     */
    static Connection connect(String database) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + database);
    }

    /**
     * Create possibly nonexistent database tables
     *
     * @param database database path
     * @throws SQLException on error
     */
    public static void createDatabaseTables(String database) throws SQLException {
        Connection con = connect(database);
        con.setAutoCommit(true);
        String cmd = "create table if not exists players\n(\n" +
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
        PreparedStatement st = con.prepareStatement(cmd);
        st.execute();

        cmd = "create table if not exists jobs\n(\n" +
                "    id          integer not null\n" +
                "        primary key autoincrement,\n" +
                "    name        text    not null,\n" +
                "    description text default ''\n);";
        PreparedStatement st2 = con.prepareStatement(cmd);
        st2.execute();

        cmd = "create table if not exists companies\n(\n" +
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
        PreparedStatement st3 = con.prepareStatement(cmd);
        st3.execute();

        cmd = "create table if not exists transactions\n(\n" +
                "    id          integer not null\n" +
                "        constraint transactions_pk\n" +
                "            primary key autoincrement,\n" +
                "    companyId   integer not null\n" +
                "        references companies,\n" +
                "    direction   TEXT    not null,\n" +
                "    title       TEXT,\n" +
                "    description text,\n" +
                "    amount      TEXT default '0' not null);";
        PreparedStatement st4 = con.prepareStatement(cmd);
        st4.execute();

        con.close();
    }

}
