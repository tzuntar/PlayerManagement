package com.redcreator37.playermanagement;

import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.redcreator37.playermanagement.Localization.lc;

/**
 * Common player routines
 */
public final class PlayerRoutines {

    /**
     * Non-instantiable
     */
    private PlayerRoutines() {
    }

    /**
     * Returns {@code true} if this ServerPlayer doesn't exist
     * and sends the message to the invoker
     *
     * @param invoker the command invoker which will see any output
     * @param target  the target ServerPlayer to assert whether it's null
     * @param entered the entered player username
     * @return true if the ServerPlayer object is null, false otherwise
     */
    public static boolean checkPlayerNonExistent(Player invoker, ServerPlayer target, String entered) {
        if (target == null) {
            invoker.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                    + lc("unknown-player")
                    + ChatColor.GREEN + entered + ChatColor.GOLD + ".");
            return true;
        }
        return false;
    }

    /**
     * Checks whether this {@link Player} lacks this permission
     *
     * @param player     the target player
     * @param permission the permission to check
     * @return true if the user has the specified permission, else otherwise
     */
    public static boolean lacksPermission(Player player, String permission) {
        if (!player.hasPermission(permission)) {
            player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                    + lc("no-permission"));
            return true;
        }
        return false;
    }

    /**
     * Checks if this player's inventory is full
     *
     * @param player the player whose inventory to check
     * @return true if the inventory is full, false otherwise
     */
    public static boolean checkInventoryFull(Player player) {
        if (player.getInventory().firstEmpty() == -1) {  // firstEmpty() returns -1 if it's full
            player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                    + lc("inventory-full"));
            return true;
        }
        return false;
    }

    /**
     * Attempts to get the player from this CommandSender object
     *
     * @param sender the CommandSender object
     * @return the matching player or null
     */
    public static Player playerFromSender(CommandSender sender) {
        assert sender != null;
        if (!(sender instanceof Player)) {
            sender.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                    + lc("player-only-command"));
            return null;
        }
        return (Player) sender;
    }

    /**
     * Returns the current date in a string
     *
     * @param dateFormat the date format (ex. {@code yyyy-MM-dd})
     * @return the current date in the specified format
     */
    public static String getCurrentDate(String dateFormat) {
        return new SimpleDateFormat(dateFormat)
                .format(Calendar.getInstance().getTime());
    }

    /**
     * Returns the BigDecimal from this string or null
     * if the string doesn't contain a valid number
     *
     * @param player  the player who entered the number
     * @param entered the entered String
     * @return matching BigDecimal or null if invalid
     */
    public static BigDecimal getEnteredBigDecimal(Player player, String entered) {
        try {
            return new BigDecimal(entered);
        } catch (Exception e) {
            player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                    + lc("invalid-number")
                    + ChatColor.GREEN + entered);
            return null;
        }
    }

    /**
     * Returns a formatted number string
     *
     * @param decimal the number to format
     * @return the formatted string
     */
    public static String formatDecimal(BigDecimal decimal) {
        return NumberFormat.getCurrencyInstance(Locale.US)
                .format(decimal.doubleValue());
    }

    /**
     * Truncates this string to the specified amount of characters
     *
     * @param str    the input string
     * @param length the maximal length
     * @return the truncated string
     */
    public static String truncate(String str, int length) {
        return str.substring(0, Math.min(str.length(), length));
    }

    /**
     * Returns true if this list contains the string
     *
     * @param list the array list to search through
     * @param s    the string to look for
     * @return true if the string is found, false otherwise
     */
    public static boolean checkIfContains(List<String> list, String s) {
        return list.stream().anyMatch(s1 -> s1.equals(s));
    }

    /**
     * Returns the value or "N/A" if the value is null / empty
     *
     * @param value the input string
     * @return value or "N/A" if null/empty
     */
    public static String getValueOrEmpty(String value) {
        return value == null || value.trim().equals("") ? "N/A" : value;
    }

    /**
     * Converts this list into a string array
     *
     * @param list the input list
     * @return the string array
     */
    public static String[] stringListToArray(List<String> list) {
        return list.toArray(new String[0]);
    }

}
