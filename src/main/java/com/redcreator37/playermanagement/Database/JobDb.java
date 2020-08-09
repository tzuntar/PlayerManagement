package com.redcreator37.playermanagement.Database;

import com.redcreator37.playermanagement.DataModels.Job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Job-related database routines
 */
public class JobDb extends SharedDb<Job, Map<String, Job>> {

    /**
     * Constructs a new JobDb instance
     *
     * @param db the database connection
     */
    public JobDb(Connection db) {
        super(db);
    }

    /**
     * Executes the specified sql update query
     *
     * @param sql the SQL command. Example: <code>INSERT INTO
     *            contacts (name, surname) VALUES (?, ?)</code>
     * @param job the Job object to get the data froms
     * @throws SQLException on errors
     */
    @Override
    void runSqlUpdate(String sql, Job job, boolean update) throws SQLException {
        PreparedStatement st = db.prepareStatement(sql);
        st.closeOnCompletion();
        st.setString(1, job.getName());
        st.setString(2, job.getDescription());
        if (update) st.setInt(3, job.getId());
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
    Map<String, Job> commonQuery(String sql) throws SQLException {
        Map<String, Job> jobs = new HashMap<>();
        Statement st = db.createStatement();
        st.closeOnCompletion();
        ResultSet set = st.executeQuery(sql);

        // loop through the records
        while (set.next()) {
            Job j = new Job(set.getInt("id"),
                    set.getString("name"),
                    set.getString("description"));
            jobs.put(j.getName(), j);
        }
        set.close();
        return jobs;
    }

    /**
     * Returns the map of all jobs in the database
     *
     * @return the job map
     * @throws SQLException on errors
     */
    @Override
    public Map<String, Job> getAll() throws SQLException {
        return commonQuery("SELECT * FROM jobs");
    }

    /**
     * Inserts this job into the database
     *
     * @param job the Job object to be inserted
     * @throws SQLException on errors
     */
    @Override
    public void insert(Job job) throws SQLException {
        String cmd = "INSERT INTO jobs(name, description) VALUES(?, ?)";
        runSqlUpdate(cmd, job, false);
    }

    /**
     * Updates the data of an existing object in the database
     *
     * @param job the object to update
     */
    @Override
    public void update(Job job) throws SQLException {
        String cmd = "UPDATE jobs SET name = ?, description = ? WHERE id = ?";
        runSqlUpdate(cmd, job, true);
    }

    /**
     * Deletes the job with the specified id from the database
     *
     * @param id job id
     * @throws SQLException on errors
     */
    public void remove(int id) throws SQLException {
        String cmd = "DELETE FROM jobs WHERE id = " + id + ";";
        db.prepareStatement(cmd).executeUpdate();
    }

}
