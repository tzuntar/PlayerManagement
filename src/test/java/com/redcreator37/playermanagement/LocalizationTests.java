package com.redcreator37.playermanagement;

import org.junit.Assert;
import org.junit.Test;

public class LocalizationTests {

    @Test
    public void getStringTest() {
        if (!Localization.changeLanguage("Strings", "sl_SI"))
            Assert.fail();
        Assert.assertEquals("Zapri", Localization.lc("close"));
    }

    @Test
    public void invalidLangCodeTest() {
        Assert.assertFalse(Localization.changeLanguage("Strings", "something-SOMETHING"));
    }

}
