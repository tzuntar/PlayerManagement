package com.redcreator37.playermanagement.Database;

import com.redcreator37.playermanagement.DataModels.Job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Job-related database routines
 */
public final class JobDb {

    /**
     * Noninstantiable
     */
    private JobDb() {
    }

    /**
     * Executes the specified sql update query
     *
     * @param sql      the SQL command. Example: <code>INSERT INTO
     *                 contacts (name, surname) VALUES (?, ?)</code>
     * @param job      the Job object to get the data from
     * @param database database path
     * @throws SQLException on error
     */
    private static void runJobSqlUpdate(String sql, Job job, String database) throws SQLException {
        Connection con = SharedDb.connect(database);
        PreparedStatement st = con.prepareStatement(sql);
        st.closeOnCompletion();
        st.setString(1, job.getName());
        st.setString(2, job.getDescription());
        st.executeUpdate();
        con.close();
    }

    /**
     * Returns the list of all jobs in the database
     *
     * @param database database path
     * @return the job list
     * @throws SQLException on error
     */
    public static List<Job> getAllJobs(String database) throws SQLException {
        String cmd = "SELECT * FROM jobs";
        List<Job> jobs = new ArrayList<>();
        Connection con = SharedDb.connect(database);
        Statement st = con.createStatement();
        ResultSet set = st.executeQuery(cmd);

        // loop through the records
        while (set.next()) {
            Job j = new Job(set.getInt("id"),
                    set.getString("name"),
                    set.getString("description"));
            jobs.add(j);
        }

        con.close();
        return jobs;
    }

    /**
     * Adds another job to the database
     *
     * @param job      the Job object to be inserted
     * @param database database path
     * @throws SQLException on error
     */
    public static void insertJob(Job job, String database) throws SQLException {
        String cmd = "INSERT INTO jobs(name, description) VALUES(?, ?)";
        runJobSqlUpdate(cmd, job, database);
    }

    /**
     * Deletes the job with the specified id from the database
     *
     * @param id       job id
     * @param database database path
     * @throws SQLException on error
     */
    public static void removeJob(int id, String database) throws SQLException {
        String cmd = "DELETE FROM jobs WHERE id = " + id + ";";
        Connection con = SharedDb.connect(database);
        PreparedStatement st = con.prepareStatement(cmd);
        st.executeUpdate();
        con.close();
    }

    /**
     * Returns the Job object with the matching name
     *
     * @param jobs    the list of all jobs
     * @param entered the entered string
     * @return the matching Job object, or null if the
     * job with this name wasn't found
     */
    public static Job getJobFromString(List<Job> jobs, String entered) {
        return jobs.stream().filter(job -> job.getName().equals(entered))
                .findFirst().orElse(null);
    }

}
