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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Tests the database interface capabilities
 */
public class DatabaseInterfaceTests {

    /**
     * The path to the database to use in tests
     */
    private static final String DB_PATH = "target/test.db";

    /**
     * The generic placeholder value to use when filling fields
     */
    private static final String GENERIC_PH = "N/A";

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
        File dbFile = new File(DB_PATH);
        if (dbFile.exists() && !dbFile.delete())
            Assert.fail("Could not delete the existing " + DB_PATH);

        try (Connection db = SharedDb.connect(DB_PATH)) {
            SharedDb.createTables(db, DatabaseInterfaceTests.class
                    .getClassLoader().getResourceAsStream("GenerateDb.sql"));
            JobDb jobDb = new JobDb(db);
            // insert a blank job using a bogus id
            jobDb.insert(new Job(4097, GENERIC_PH, GENERIC_PH));
            System.out.println("Created an empty database");

            try {
                jobDb.getAll();
                new CompanyDb(db).getAll();
                new TransactionDb(db).getAll();
                new PlayerDb(db).getAll();
                System.out.println("Player database loaded successfully");
            } catch (SQLException e) {
                Assert.fail("Error while reading from the database: " + e.getMessage());
            }
        } catch (SQLException | IOException e) {
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
        try (Connection db = SharedDb.connect(DB_PATH)) {
            ServerPlayer p = new ServerPlayer(4097,
                    new PlayerTag(GENERIC_PH, GENERIC_PH));
            p.setJob(new JobDb(db).getAll().get(GENERIC_PH));
            p.setCompany(new CompanyDb(db).getAll().get(GENERIC_PH));
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
        if (!new File(DB_PATH).delete()) Assert.fail("Deleting the database failed");
        Assert.assertTrue(true);    // already passed at this point
    }

}
