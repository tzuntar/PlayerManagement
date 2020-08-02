import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.Job;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.DataModels.Transaction;
import com.redcreator37.playermanagement.Database.CompanyDb;
import com.redcreator37.playermanagement.Database.JobDb;
import com.redcreator37.playermanagement.Database.PlayerDb;
import com.redcreator37.playermanagement.Database.SharedDb;
import com.redcreator37.playermanagement.Database.TransactionDb;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests the database creation functionality
 */
public class CreateDbTest {

    /**
     * Attempts to set up a blank database
     */
    @Test
    @SuppressWarnings({"unused", "UnusedAssignment"})
    public void setUpDatabaseTest() {
        boolean success = true;
        String dbPath = "target/test.db";
        File dbFile = new File(dbPath);
        if (dbFile.exists() && !dbFile.delete())
            Assert.fail("Could not delete the existing " + dbPath);

        List<Transaction> transactions;
        Map<String, Company> companies;
        Map<String, Job> jobs;
        Map<String, ServerPlayer> players;

        Connection db = null;
        try {
            db = SharedDb.connect(dbPath);
            SharedDb.createDatabaseTables(db);
            // insert a blank job using a bogus id
            JobDb.insertJob(new Job(4097, "N/A", "N/A"), db);
            players = new HashMap<>();
            jobs = new HashMap<>();
            companies = new HashMap<>();
            transactions = new ArrayList<>();
            System.out.println("Created an empty database");
        } catch (SQLException e) {
            System.out.println("Error while creating the database: " + e.getMessage());
            success = false;
        }

        if (db == null) Assert.fail("Establishing database connection failed!");
        try {
            jobs = JobDb.getAllJobs(db);   // it has to be done in this exact order!
            companies = CompanyDb.getAllCompanies(db);
            transactions = TransactionDb.getAllTransactions(db);
            players = PlayerDb.getAllPlayers(db);
            System.out.println("Player database loaded successfully");
        } catch (SQLException e) {
            System.out.println("Error while reading from the database: "
                    + e.getMessage());
            success = false;
        }

        System.out.println("Cleaning up...");
        // if (!dbFile.delete()) success = false; // FIXME: fails
        Assert.assertTrue(success);
    }

}
