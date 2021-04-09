package com.redcreator37.playermanagement;

import com.redcreator37.playermanagement.Containers.PlayerDataContainer;
import com.redcreator37.playermanagement.DataModels.PlayerTag;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRoutinesTests {

    @Test
    public void userFromUuidTest() {
        UUID randomUuid = UUID.randomUUID();
        ServerPlayer pl = new ServerPlayer(0, new PlayerTag("Player", randomUuid));
        Map<UUID, ServerPlayer> playerMap = new HashMap<UUID, ServerPlayer>() {{
            put(randomUuid, pl);
        }};
        PlayerDataContainer container = new PlayerDataContainer(playerMap);
        Assert.assertEquals(pl.getUuid(), container.uuidFromUsername("Player"));
    }

    @Test
    public void invalidUserFromUuidTest() {
        Map<UUID, ServerPlayer> playerMap = new HashMap<>();
        PlayerDataContainer container = new PlayerDataContainer(playerMap);
        Assert.assertNull(container.uuidFromUsername("SomeInvalidPlayer"));
    }

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
        Assert.assertEquals("-$3.12", PlayerRoutines
                .formatDecimal(BigDecimal.valueOf(-3.12333)));
    }

    /**
     * Tests if the date format method fails properly
     */
    @Test(expected = IllegalArgumentException.class)
    public void formatDateInvalid() {
        PlayerRoutines.getCurrentDate("yyyy-mm.DD:HH.tt");
    }

    /**
     * Tests the getValueOrEmpty method in PlayerRoutines class by
     * submitting a null value
     */
    @Test
    public void getValueOrEmptyNullValueTest() {
        Assert.assertEquals("N/A", PlayerRoutines.getValueOrEmpty(null));
    }

}
