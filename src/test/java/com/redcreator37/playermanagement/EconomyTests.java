package com.redcreator37.playermanagement;

import com.redcreator37.playermanagement.DataModels.Company;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class EconomyTests {

    @Test
    public void changeWageTest() {
        Company c = new Company(0, "Company", "Desc", "0", 0, null, "0", "0");
        c.setWage(new BigDecimal("10.02354"));
        Assert.assertEquals(c.getWage().toString(), "10.02354");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidWageTest() {
        new Company(0, "Company", "Desc", "0", 0, null, "0", "0")
                .setWage(new BigDecimal(-1));
    }

}
