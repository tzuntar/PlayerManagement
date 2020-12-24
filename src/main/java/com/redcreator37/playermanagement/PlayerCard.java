package com.redcreator37.playermanagement;

import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.redcreator37.playermanagement.Localization.lc;

/**
 * Handles all in-game ID card actions
 */
public class PlayerCard implements Listener {

    /**
     * Handles all right-click events
     *
     * @param event the player interact event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerUse(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            List<String> lore = null;
            try {
                lore = Objects.requireNonNull(p.getInventory()
                        .getItemInMainHand().getItemMeta()).getLore();
            } catch (NullPointerException ignored) { }

            if (lore != null && lore.contains(PlayerManagement.cardItemLore))
                handlePlayerCardEvent(p, lore);
        }
    }

    /**
     * Handles card right click event
     *
     * @param player the player that caused the event
     * @param lore   the item (card) lore
     */
    private static void handlePlayerCardEvent(Player player, List<String> lore) {
        ServerPlayer target = PlayerManagement.players.get(lore.get(1));
        if (target != null) displayCardData(player, target);
        else player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                + lc("invalid-id-card"));
    }

    /**
     * Creates a new ID card item serialized with the player's
     * UUID and gives it to the player
     *
     * @param player the player the card will be given to
     * @param target the ServerPlayer object to get the data form
     */
    public static void giveNewCard(Player player, ServerPlayer target) {
        ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta data = stack.getItemMeta();
        Objects.requireNonNull(data).setDisplayName("ID Card: " + target);

        List<String> lore = new ArrayList<>();
        lore.add(PlayerManagement.cardItemLore);
        lore.add(target.getUuid());
        Objects.requireNonNull(data).setLore(lore);
        stack.setItemMeta(data);

        player.getInventory().addItem(stack);
    }

    /**
     * Returns a formatted graph based on these values
     *
     * @param level the value, <strong>must be &lt;= max</strong>
     * @param max   the max value for reference
     * @return the color formatted string
     */
    @SuppressWarnings("SameParameterValue")
    private static String getBarGraph(double level, double max) {
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
    public static void displayCardData(Player invoker, ServerPlayer player) {
        if (player == null) {   // invalid uuid or invalid card
            invoker.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
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
            String health = getBarGraph(p.getHealth(), 20);
            String foodLevel = getBarGraph(p.getFoodLevel(), 20);
            String saturationLevel = getBarGraph(p.getSaturation(), 20);
            double armor = Objects.requireNonNull(p
                    .getAttribute(Attribute.GENERIC_ARMOR)).getValue();
            String armorLevel = getBarGraph(armor, 20);

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
                    .autoEcoTimeSeconds / 60 + "§r " + lc("minutes"));
        }
        openBook(invoker, pages, "N/A", "N/A");
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
