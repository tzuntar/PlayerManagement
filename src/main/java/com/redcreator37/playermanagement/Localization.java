package com.redcreator37.playermanagement;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Handles all localization-specific aspects of the plugin
 */
public class Localization {

    /**
     * The {@link ResourceBundle} object with all user-facing messages
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static ResourceBundle strings =
            getBundleFromLangCode("Strings", "en_US").get();

    /**
     * Returns the localized message with the matching tag from the
     * strings {@link ResourceBundle}
     *
     * @param key the localization key
     * @return the message (or {@code null} if the key isn't valid)
     */
    public static String lc(String key) {
        return StringEscapeUtils.unescapeJava(strings.getString(key));
    }

    /**
     * Returns the {@link ResourceBundle} with the matching name for this
     * language code
     *
     * @param baseName the full name of the {@link ResourceBundle} to retrieve
     * @param langCode a code in the language_country format
     *                 (ex. {@code en_US})
     * @return the matching {@link ResourceBundle} or an empty {@link Optional}
     * if the matching bundle-language combination wasn't found
     */
    private static Optional<ResourceBundle> getBundleFromLangCode(String baseName, String langCode) {
        try {
            InputStream stream = PlayerManagement.class.getClassLoader()
                    .getResourceAsStream(baseName + "_" + langCode + ".properties");
            assert stream != null;
            Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            return Optional.of(new PropertyResourceBundle(reader));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Loads messages for this language code from the specified
     * {@link ResourceBundle}
     *
     * @param baseName the full name of the {@link ResourceBundle} to retrieve
     * @param langCode a code in the language_country format
     *                 (ex. {@code en_US})
     * @return {@code true} if the bundle was loaded successfully, {@code false}
     * otherwise
     */
    @SuppressWarnings("SameParameterValue")
    static boolean changeLanguage(String baseName, String langCode) {
        Optional<ResourceBundle> bundle = getBundleFromLangCode(baseName, langCode);
        if (!bundle.isPresent()) return false;
        strings = bundle.get();
        return true;
    }

}
