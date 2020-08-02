package com.redcreator37.playermanagement.Database;

import com.redcreator37.playermanagement.DataModels.Job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
     * @param sql the SQL command. Example: <code>INSERT INTO
     *            contacts (name, surname) VALUES (?, ?)</code>
     * @param job the Job object to get the data from
     * @param db  the database connection
     * @throws SQLException on error
     */
    private static void runJobSqlUpdate(String sql, Job job, Connection db) throws SQLException {
        PreparedStatement st = db.prepareStatement(sql);
        st.closeOnCompletion();
        st.setString(1, job.getName());
        st.setString(2, job.getDescription());
        st.executeUpdate();
    }

    /**
     * Returns the list of all jobs in the database
     *
     * @param db the database connection
     * @return the job list
     * @throws SQLException on error
     */
    public static Map<String, Job> getAllJobs(Connection db) throws SQLException {
        String cmd = "SELECT * FROM jobs";
        Map<String, Job> jobs = new HashMap<>();
        ResultSet set = db.createStatement().executeQuery(cmd);

        // loop through the records
        while (set.next()) {
            Job j = new Job(set.getInt("id"),
                    set.getString("name"),
                    set.getString("description"));
            jobs.put(j.getName(), j);
        }
        return jobs;
    }

    /**
     * Adds another job to the database
     *
     * @param job the Job object to be inserted
     * @param db  the database connection
     * @throws SQLException on error
     */
    public static void insertJob(Job job, Connection db) throws SQLException {
        String cmd = "INSERT INTO jobs(name, description) VALUES(?, ?)";
        runJobSqlUpdate(cmd, job, db);
    }

    /**
     * Deletes the job with the specified id from the database
     *
     * @param id job id
     * @param db the database connection
     * @throws SQLException on error
     */
    public static void removeJob(int id, Connection db) throws SQLException {
        String cmd = "DELETE FROM jobs WHERE id = " + id + ";";
        db.prepareStatement(cmd).executeUpdate();
    }

}
