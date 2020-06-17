import com.redcreator37.playermanagement.PlayerRoutines;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

public class PlayerRoutinesTests {

    /**
     * Tests the string truncation functionality
     */
    @Test
    public void truncateTest() {
        String input = "HelloWorld123455BlaBlaBla  \n12345";
        String expected = "HelloWorld123455BlaBl";
        Assert.assertEquals(expected, PlayerRoutines.truncate(input, 21));
    }

    /**
     * Tests if the checkIfContains method works with empty arrays
     */
    @Test
    public void emptyArrayContainsTest() {
        Assert.assertFalse(PlayerRoutines
                .checkIfContains(new ArrayList<>(), "test"));
    }

    /**
     * Tests if the NullPointerException is thrown when passing
     * a null object
     */
    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullArrayListToArrayTest() {
        PlayerRoutines.stringListToArray(null);
    }

    /**
     * Tests the formatDecimal method on positive numbers
     */
    @Test
    public void formatDecimalTest() {
        Assert.assertEquals("$3.02", PlayerRoutines
                .formatDecimal(BigDecimal.valueOf(3.021111)));
    }

    /**
     * Tests the formatDecimal method on negative numbers
     */
    @Test
    public void formatNegativeDecimalTest() {
        Assert.assertEquals("($3.12)", PlayerRoutines
                .formatDecimal(BigDecimal.valueOf(-3.12333)));
    }

}
