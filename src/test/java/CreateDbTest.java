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
        String dbPath = "target/test.db";
        File dbFile = new File(dbPath);
        if (dbFile.exists() && !dbFile.delete())
            Assert.fail("Could not delete the existing " + dbPath);

        List<Transaction> transactions;
        Map<String, Company> companies;
        Map<String, Job> jobs;
        Map<String, ServerPlayer> players;

        JobDb jobDb = null;
        CompanyDb companyDb = null;
        TransactionDb transactionDb = null;
        PlayerDb playerDb = null;
        try (Connection db = SharedDb.connect(dbPath)) {
            SharedDb.createTables(db);
            jobDb = new JobDb(db);
            companyDb = new CompanyDb(db);
            transactionDb = new TransactionDb(db);
            playerDb = new PlayerDb(db);
            // insert a blank job using a bogus id
            jobDb.insert(new Job(4097, "N/A", "N/A"));
            players = new HashMap<>();
            jobs = new HashMap<>();
            companies = new HashMap<>();
            transactions = new ArrayList<>();
            System.out.println("Created an empty database");

            try {
                jobs = jobDb.getAll();   // it has to be done in this exact order!
                companies = companyDb.getAll();
                transactions = transactionDb.getAll();
                players = playerDb.getAll();
                System.out.println("Player database loaded successfully");
            } catch (SQLException e) {
                Assert.fail("Error while reading from the database: " + e.getMessage());
            }
        } catch (SQLException e) {
            Assert.fail("Creating the database failed: " + e.getMessage());
        }

        System.out.println("Cleaning up...");
        if (!dbFile.delete()) Assert.fail("Deleting the database failed");
        Assert.assertTrue(true);    // already passed at this point
    }

}
