import com.redcreator37.playermanagement.DataModels.Job;
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

/**
 * Tests the database creation functionality
 */
public class CreateDbTest {

    /**
     * Attempts to set up a blank database
     */
    @Test
    @SuppressWarnings("UnusedAssignment")
    public void setUpDatabaseTest() {
        String dbPath = "target/test.db";
        File dbFile = new File(dbPath);
        if (dbFile.exists() && !dbFile.delete())
            Assert.fail("Could not delete the existing " + dbPath);

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
            new HashMap<>();
            new HashMap<>();
            new HashMap<>();
            new ArrayList<>();
            System.out.println("Created an empty database");

            try {
                jobDb.getAll();
                companyDb.getAll();
                transactionDb.getAll();
                playerDb.getAll();
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
