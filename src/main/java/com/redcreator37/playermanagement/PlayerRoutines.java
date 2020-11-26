package com.redcreator37.playermanagement;

import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.redcreator37.playermanagement.PlayerManagement.strings;

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
     * Returns the UUID of the ServerPlayer with the matching username
     *
     * @param players  the player list to get the player from
     * @param username the entered username
     * @return the matching UUID string, or null if the
     * player with this username wasn't found
     */
    public static String uuidFromUsername(Map<String, ServerPlayer> players, String username) {
        return players.values().stream().filter(pl -> pl.getUsername().equals(username))
                .map(ServerPlayer::getUuid)
                .findFirst().orElse(null);
    }

    /**
     * Returns <code>true</code> if this ServerPlayer doesn't exist
     * and sends the message to the invoker
     *
     * @param invoker the command invoker which will see any output
     * @param target  the target ServerPlayer to assert whether it's null
     * @param entered the entered player username
     * @return true if the ServerPlayer object is null, false otherwise
     */
    public static boolean checkPlayerNonExistent(Player invoker, ServerPlayer target, String entered) {
        if (target == null) {
            invoker.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + strings.getString("unknown-player")
                    + ChatColor.GREEN + entered + ChatColor.GOLD + ".");
            return true;
        }
        return false;
    }

    /**
     * Checks if this player has sufficient permissions
     *
     * @param player     the target player
     * @param permission the permission to check
     * @return true if the user has the specified permission, else otherwise
     */
    public static boolean checkPermission(Player player, String permission) {
        if (!player.hasPermission(permission)) {
            player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + strings.getString("no-permission"));
            return false;
        }
        return true;
    }

    /**
     * Checks if this player's inventory is full
     *
     * @param player the player whose inventory to check
     * @return true if the inventory is full, false otherwise
     */
    public static boolean checkInventoryFull(Player player) {
        if (player.getInventory().firstEmpty() == -1) {  // firstEmpty() returns -1 if it's full
            player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + strings.getString("inventory-full"));
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
            sender.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + strings.getString("player-only-command"));
            return null;
        }
        return (Player) sender;
    }

    /**
     * Returns the current date in a string
     *
     * @param dateFormat the date format (ex. <code>yyyy-MM-dd</code>)
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
            player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + strings.getString("invalid-number")
                    + ChatColor.GREEN + entered);
            return null;
        }
    }

    /**
     * Displays the info for the specified company in a book
     *
     * @param p the player that'll see the info
     * @param c the company to get the data from
     */
    public static void displayCompanyInfo(Player p, Company c) {
        List<String> pages = new ArrayList<>();
        BigDecimal afterPayments = c.getBalance().subtract(c.getWage()
                .multiply(BigDecimal.valueOf(c.getEmployees())));
        List<ServerPlayer> employees = PlayerManagement.players.values().stream()
                .filter(pl -> pl.getCompany().equals(c))
                .collect(Collectors.toList());

        pages.add("§1§l --< §2§l" + strings.getString("company-uppercase") + " §1§l > --"
                + "\n\n§0§l" + strings.getString("name") + " §r§2§l§o" + c
                + "\n\n§0§l" + strings.getString("description") + " §r§1§o" + c.getDescription()
                + "\n\n§0§l" + strings.getString("balance") + " §r§1" + formatDecimal(c.getBalance())
                + "\n\n§0§l" + strings.getString("employees") + " §r§1" + c.getEmployees());
        pages.add("§1§l --< §2§l" + strings.getString("company-uppercase") + " §1§l>--"
                + "\n\n§0§l" + strings.getString("wage") + " §r§1" + formatDecimal(c.getWage())
                + "\n§r" + strings.getString("paid-every") + " §1" + PlayerManagement
                .autoEcoTimeSeconds / 60 + "§r " + strings.getString("minutes")
                + "\n\n§0" + strings.getString("balance-after-payments")
                + " §r§1" + formatDecimal(afterPayments)
                + "\n\n§0§l" + strings.getString("owner") + " §r§1" + c.getOwner()
                + "\n\n§0§l" + strings.getString("established") + " §r§1" + c.getEstablishedDate());

        for (int i = 0; i < employees.size(); i++) {
            StringBuilder sb = new StringBuilder("§1§l --< §2§l" + strings
                    .getString("company-uppercase") + " §1§l>--" +
                    "\n\n§r§l" + strings.getString("employees") + "\n\n§r");
            ServerPlayer pl = employees.get(i);
            sb.append(pl).append("\n");
            for (int j = 0; j < 8; j++) {
                i++;
                if (i < employees.size()) {
                    pl = employees.get(i);
                    sb.append(pl).append("\n");
                }
            }
            pages.add(sb.toString());
        }
        PlayerCard.openBook(p, pages, "N/A", "N/A");
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
