import com.redcreator37.playermanagement.DataModels.Job;
import com.redcreator37.playermanagement.DataModels.PlayerTag;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
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

/**
 * Tests the database creation functionality
 */
public class DatabaseInterfaceTests {

    /**
     * The path to the database to use in tests
     */
    private static final String DB_PAtH = "target/test.db";

    /**
     * Tests the database creation/operation/deletion capabilities
     */
    @Test
    public void databaseInterfaceTests() {
        setUpDatabaseTest();
        setAndGetValueTest();
        removeDatabaseTest();
    }

    /**
     * Attempts to set up a blank database
     */
    private void setUpDatabaseTest() {
        File dbFile = new File(DB_PAtH);
        if (dbFile.exists() && !dbFile.delete())
            Assert.fail("Could not delete the existing " + DB_PAtH);

        try (Connection db = SharedDb.connect(DB_PAtH)) {
            SharedDb.createTables(db);
            JobDb jobDb = new JobDb(db);
            CompanyDb companyDb = new CompanyDb(db);
            TransactionDb transactionDb = new TransactionDb(db);
            PlayerDb playerDb = new PlayerDb(db);
            // insert a blank job using a bogus id
            jobDb.insert(new Job(4097, "N/A", "N/A"));
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
        Assert.assertTrue(true);    // already passed at this point
    }

    /**
     * Attempts to get the N/A job and company values from the
     * database and insert a blank N/A player entry
     */
    private void setAndGetValueTest() {
        System.out.println("Inserting some data...");
        try (Connection db = SharedDb.connect(DB_PAtH)) {
            ServerPlayer p = new ServerPlayer(4097,
                    new PlayerTag("N/A", "N/A"));
            p.setJob(new JobDb(db).getAll().get("N/A"));
            p.setCompany(new CompanyDb(db).getAll().get("N/A"));
            new PlayerDb(db).insert(p);
        } catch (SQLException e) {
            Assert.fail("Error while reading from the database: " + e.getMessage());
        }
        Assert.assertTrue(true);    // already passed at this point
    }

    /**
     * Cleans up by attempting to remove the testing database
     */
    private void removeDatabaseTest() {
        System.out.println("Cleaning up...");
        if (!new File(DB_PAtH).delete()) Assert.fail("Deleting the database failed");
        Assert.assertTrue(true);    // already passed at this point
    }

}
