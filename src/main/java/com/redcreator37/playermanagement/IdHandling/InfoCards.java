package com.redcreator37.playermanagement.IdHandling;

import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.redcreator37.playermanagement.Localization.lc;

/**
 * A common class for creating and displaying in-game book based data
 * representations
 */
public class InfoCards {

    /**
     * Constructs and displays an in-game book to the player
     *
     * @param p      the target player
     * @param pages  the contents of the book
     * @param title  the book title
     * @param author the book author
     */
    public static void openBook(Player p, List<String> pages, String title, String author) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta meta = (BookMeta) book.getItemMeta();
        Objects.requireNonNull(meta).setPages(pages);
        meta.setTitle(title);
        meta.setAuthor(author);
        book.setItemMeta(meta);
        p.openBook(book);
    }

    /**
     * Returns a formatted graph based on these values
     *
     * @param level the value, <strong>must be &lt;= max</strong>
     * @param max   the max value for reference
     * @return the color formatted string
     */
    @SuppressWarnings("SameParameterValue")
    private static String drawBarGraph(double level, double max) {
        assert level <= max;
        StringBuilder graph = new StringBuilder((int) max * 2);
        String lvl = "§2";
        if (level / max < 0.50) lvl = "§6";
        if (level / max < 0.25) lvl = "§4";
        graph.append(lvl);

        for (int i = 0; i < max / 2; i++)
            graph.append(i < level / 2 ? "■" : "  ");
        return graph.toString();
    }

    /**
     * Displays the data for the provided UUID to this player
     *
     * @param invoker the player requesting the data
     * @param player  the player for which to look up the data
     */
    public static void displayPlayerInfo(Player invoker, ServerPlayer player) {
        if (player == null) {   // invalid uuid or invalid card
            invoker.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                    + "Invalid ID card!");
            return;
        }

        OfflinePlayer offlinePl = Bukkit.getOfflinePlayer(UUID.fromString(player.getUuid()));
        String balance = "N/A";
        try {
            balance = PlayerRoutines.formatDecimal(BigDecimal
                    .valueOf(PlayerManagement.eco.getBalance(offlinePl)));
        } catch (RuntimeException ignored) {}

        Player p = offlinePl.getPlayer();
        String job = PlayerRoutines.getValueOrEmpty(player.getJob().getName()),
                company = PlayerRoutines.getValueOrEmpty(player.getCompany().getName());
        List<String> pages = new ArrayList<>();
        pages.add("§1§l ---< §9§l" + lc("player-uppercase") + " §1§l>---"
                + "\n\n§0§l" + lc("username") + " §r§1" + player
                + "\n\n§0§l" + lc("name") + " §r§1" + player.getName()
                + "\n\n§0§l" + lc("registration-date") + " §r§1" + player.getJoinDate()
                + "\n\n§0§l" + lc("job-name") + " §r§1" + job);

        pages.add("§1§l ---< §9§l" + lc("player-uppercase") + " §1§l>---"
                + "\n\n§0§l" + lc("company") + " §r§1" + company
                + "\n\n§0§l" + lc("notes") + " §r§1§o"
                + PlayerRoutines.getValueOrEmpty(player.getNotes())
                + "\n\n§0§l" + lc("money") + " §r§1" + balance
                + "\n\n§0§l" + lc("punishments") + " §r§1"
                + formatPunishments(player));

        if (p != null) {    // these are only available if the player is online
            String health = drawBarGraph(p.getHealth(), 20);
            String foodLevel = drawBarGraph(p.getFoodLevel(), 20);
            String saturationLevel = drawBarGraph(p.getSaturation(), 20);
            double armor = Objects.requireNonNull(p
                    .getAttribute(Attribute.GENERIC_ARMOR)).getValue();
            String armorLevel = drawBarGraph(armor, 20);

            // values are cast to int for formatting purposes
            pages.add("§1§l ---< §9§l" + lc("player-uppercase") + " §1§l>---"
                    + "\n\n §0§l" + lc("health")
                    + "      §1" + (int) p.getHealth() + "§8/§120"
                    + "\n §r§8[" + health + "§8]"
                    + "\n\n §0§l" + lc("food")
                    + "         §1" + p.getFoodLevel() + "§8/§120"
                    + "\n §r§8[" + foodLevel + "§8]"
                    + "\n\n §0§l" + lc("saturation")
                    + "  §1" + (int) p.getSaturation() + "§8/§120"
                    + "\n §r§8[" + saturationLevel + "§8]"
                    + "\n\n §0§l" + lc("armor")
                    + "        §1" + (int) armor + "§8/§120"
                    + "\n §r§8[" + armorLevel + "§8]");

            DecimalFormat fourPlaces = new DecimalFormat("##.0000");
            pages.add("§1§l ---< §9§l" + lc("player-uppercase") + " §1§l>---"
                    + "\n\n§0§l" + lc("location")
                    + "\n§1§lX: §r" + fourPlaces.format(p.getLocation().getX())
                    + "\n§1§lY: §r" + fourPlaces.format(p.getLocation().getY())
                    + "\n§1§lZ: §r" + fourPlaces.format(p.getLocation().getZ())
                    + "\n\n§r§l" + lc("experience")
                    + "\n§r" + lc("level") + " §r§1" + p.getLevel()
                    + "\n§r" + lc("to-next-level") + " §r§1" + p.getExpToLevel()
                    + "\n§r" + lc("total") + " §r§1" + p.getTotalExperience());
        } else pages.add("§1§l ---< §9§l" + lc("player-uppercase") + " §1§l>---"
                + "\n\n§0§o§l" + lc("unavailable") + " \n§r"
                + lc("player-not-online"));

        if (!job.equals("N/A"))
            pages.add("§1§l ----< §5§l" + lc("job-uppercase") + " §1§l>----"
                    + "\n\n§0§l" + lc("job-name") + " §r§1" + player.getJob()
                    + "\n\n§0§l" + lc("job-description")
                    + " §r§1§o" + player.getJob().getDescription());

        if (!company.equals("N/A")) {
            pages.add("§1§l --< §2§l" + lc("company-uppercase") + " §1§l>--"
                    + "\n\n§0§l" + lc("name") + " §r§1" + player.getCompany()
                    + "\n\n§0§l" + lc("description") + " §r§1§o"
                    + player.getCompany().getDescription()
                    + "\n\n§0§l" + lc("owner") + " §r§1"
                    + player.getCompany().getOwner().getUsername());
            pages.add("§1§l ---< §2§l" + lc("job-uppercase") + " §1§l>---"
                    + "\n\n§0§l" + lc("employees") + " §r§1"
                    + player.getCompany().getEmployees()
                    + "\n\n§0§l" + lc("established") + " §r§1"
                    + player.getCompany().getEstablishedDate()
                    + "\n\n§0§l" + lc("wage") + " §r§1" + PlayerRoutines
                    .formatDecimal(player.getCompany().getWage())
                    + "\n§r" + lc("paid-every") + " §1" + PlayerManagement
                    .prefs.autoEcoTimeSeconds / 60 + "§r " + lc("minutes"));
        }
        openBook(invoker, pages, "N/A", "N/A");
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

        pages.add("§1§l --< §2§l" + lc("company-uppercase") + " §1§l > --"
                + "\n\n§0§l" + lc("name") + " §r§2§l§o" + c
                + "\n\n§0§l" + lc("description") + " §r§1§o" + c.getDescription()
                + "\n\n§0§l" + lc("balance") + " §r§1" + PlayerRoutines.formatDecimal(c.getBalance())
                + "\n\n§0§l" + lc("employees") + " §r§1" + c.getEmployees());
        pages.add("§1§l --< §2§l" + lc("company-uppercase") + " §1§l>--"
                + "\n\n§0§l" + lc("wage") + " §r§1" + PlayerRoutines.formatDecimal(c.getWage())
                + "\n§r" + lc("paid-every") + " §1" + PlayerManagement
                .prefs.autoEcoTimeSeconds / 60 + "§r " + lc("minutes")
                + "\n\n§0" + lc("balance-after-payments")
                + " §r§1" + PlayerRoutines.formatDecimal(afterPayments)
                + "\n\n§0§l" + lc("owner") + " §r§1" + c.getOwner()
                + "\n\n§0§l" + lc("established") + " §r§1" + c.getEstablishedDate());

        for (int i = 0; i < employees.size(); i++) {
            StringBuilder sb = new StringBuilder("§1§l --< §2§l"
                    + lc("company-uppercase") + " §1§l>--"
                    + "\n\n§r§l" + lc("employees") + "\n\n§r");
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
        openBook(p, pages, "N/A", "N/A");
    }

    /**
     * Returns the number of punishments this player has or 0
     *
     * @param player the player whose number of punishments to retrieve
     * @return a color-formatted string
     */
    private static String formatPunishments(ServerPlayer player) {
        return player.getPunishments() > 0
                ? ChatColor.DARK_RED + String.valueOf(player.getPunishments())
                : ChatColor.DARK_BLUE + "0";
    }

}
