package com.redcreator37.playermanagement.IdHandling;

import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.Job;
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.redcreator37.playermanagement.Localization.lc;

/**
 * A common class for creating and displaying in-game book based data
 * representations
 */
public final class InfoCards {

    /**
     * Non-instantiable
     */
    private InfoCards() {
    }

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
                    + lc("invalid-id-card"));
            return;
        }

        OfflinePlayer offlinePl = Bukkit.getOfflinePlayer(player.getUuid());
        String balance = lc("unknown");
        try {
            balance = PlayerRoutines.formatDecimal(BigDecimal
                    .valueOf(PlayerManagement.eco.getBalance(offlinePl)));
        } catch (RuntimeException ignored) {}

        Player p = offlinePl.getPlayer();
        Optional<Job> job = player.getJob();
        Optional<Company> company = player.getCompany();
        Optional<String> notes = player.getNotes();

        String page1 = MessageFormat.format(lc("card-playerinfo-1"),
                player, player.getName(), player.getJoinDate(), job);
        String page2 = MessageFormat.format(lc("card-playerinfo-2"),
                company, notes, balance, formatPunishments(player));
        List<String> pages = new ArrayList<>(Arrays.asList(page1, page2));

        if (p != null) {    // these are only available if the player is online
            String health = drawBarGraph(p.getHealth(), 20);
            String foodLevel = drawBarGraph(p.getFoodLevel(), 20);
            String saturationLevel = drawBarGraph(p.getSaturation(), 20);
            double armor = Objects.requireNonNull(p
                    .getAttribute(Attribute.GENERIC_ARMOR)).getValue();
            String armorLevel = drawBarGraph(armor, 20);

            // values are cast to int for formatting purposes
            String page3 = MessageFormat.format(lc("card-playerinfo-3"),
                    (int) p.getHealth(), health,
                    p.getFoodLevel(), foodLevel,
                    (int) p.getSaturation(), saturationLevel,
                    (int) armor, armorLevel);
            pages.add(page3);

            DecimalFormat fourPlaces = new DecimalFormat("##.0000");
            String page4 = MessageFormat.format(lc("card-playerinfo-4"),
                    fourPlaces.format(p.getLocation().getX()),
                    fourPlaces.format(p.getLocation().getY()),
                    fourPlaces.format(p.getLocation().getZ()),
                    p.getLevel(), p.getExpToLevel(), p.getTotalExperience());
            pages.add(page4);
        } else pages.add(lc("card-player-offline"));

        if (job.isPresent()) {
            String jobPage = MessageFormat.format(lc("card-job"),
                    job.get(), job.get().getDescription());
            pages.add(jobPage);
        }

        if (company.isPresent()) {
            String ownerUsername = company.get().getOwner().isPresent()
                    ? company.get().getOwner().get().getUsername() : lc("unknown");
            String companyPage1 = MessageFormat.format(lc("card-company-1"),
                    company.get(), company.get().getDescription(), ownerUsername);
            String companyPage2 = MessageFormat.format(lc("card-company-2"),
                    company.get().getEmployees(),
                    company.get().getEstablishedDate(),
                    PlayerRoutines.formatDecimal(company.get().getWage()),
                    PlayerManagement.prefs.autoEcoTimeSeconds / 60);
            pages.addAll(Arrays.asList(companyPage1, companyPage2));
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
        BigDecimal afterPayments = c.getBalance().subtract(c.getWage()
                .multiply(BigDecimal.valueOf(c.getEmployees())));
        List<ServerPlayer> employees = PlayerManagement.players.getCompanyEmployees(c);

        String page1 = MessageFormat.format(lc("company-card-1"),
                c, c.getDescription(), PlayerRoutines.formatDecimal(c.getBalance()),
                c.getEmployees());
        String page2 = MessageFormat.format(lc("company-card-2"),
                PlayerRoutines.formatDecimal(c.getWage()),
                PlayerManagement.prefs.autoEcoTimeSeconds / 60,
                PlayerRoutines.formatDecimal(afterPayments),
                c.getOwner(), c.getEstablishedDate());
        List<String> pages = new ArrayList<>(Arrays.asList(page1, page2));

        for (int i = 0; i < employees.size(); i++) {
            StringBuilder sb = new StringBuilder(lc("company-card-employees"));
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
