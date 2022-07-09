package com.redcreator37.playermanagement;

import com.redcreator37.playermanagement.DataModels.Company;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class EconomyTests {

    @Test
    public void changeCompanyWageTest() {
        Company c = new Company.Builder(0, "Company", "2000-01-01")
                .withDescription("Test Company")
                .withBalance(BigDecimal.ZERO)
                .withWage(BigDecimal.ZERO)
                .withEmployees(0)
                .withOwner(null).build();
        c.setWage(new BigDecimal("10.02354"));
        Assert.assertEquals(c.getWage().toString(), "10.02354");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCompanyWageTest() {
        new Company.Builder(0, "Company", "2000-01-01")
                .withWage(BigDecimal.valueOf(-1)).build();
    }

}
