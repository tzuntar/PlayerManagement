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

/**
 * Handles all in-game ID card actions
 */
public class PlayerCard implements Listener {

    /**
     * Handles all right-click events
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
        ServerPlayer target = PlayerManagement.players.stream()
                .filter(pl -> pl.getUuid().equals(lore.get(1)))
                .findFirst().orElse(null);

        if (target != null) displayCardData(player, target.getUuid());
        else player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                + "Invalid ID card!");
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
     * @param level the value, <strong>must be <= max</strong>
     * @param max   the max value for reference
     * @return the color formatted string
     */
    @SuppressWarnings("SameParameterValue")
    private static String getBarGraph(double level, double max) {
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
     * @param invoker    the player that the data will be displayed to
     * @param playerUuid which player to get the data from
     */
    public static void displayCardData(Player invoker, String playerUuid) {
        ServerPlayer target = null;
        for (ServerPlayer pl : PlayerManagement.players)    // attempt to find the player
            if (pl.getUuid().equals(playerUuid)) {
                target = pl;
                break;
            }

        Player p = Bukkit.getOfflinePlayer(UUID.fromString(playerUuid)).getPlayer();
        if (target == null || p == null) {   // invalid uuid or invalid card
            invoker.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "Invalid ID card!");
            return;
        }

        String health = getBarGraph(p.getHealth(), 20);
        String foodLevel = getBarGraph(p.getFoodLevel(), 20);
        String saturationLevel = getBarGraph(p.getSaturation(), 20);
        double armor = Objects.requireNonNull(p
                .getAttribute(Attribute.GENERIC_ARMOR)).getValue();
        String armorLevel = getBarGraph(armor, 20);

        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(playerUuid));
        List<String> pages = new ArrayList<>();
        pages.add("§1§l ---< §9§lPLAYER §1§l>---"
                + "\n\n§0§lUsername: §r§1" + target
                + "\n\n§0§lName: §r§1" + target.getName()
                + "\n\n§0§lRegistration date: §r§1" + target.getJoinDate()
                + "\n\n§0§lJob name: §r§1" + getJob(target));

        pages.add("§1§l ---< §9§lPLAYER §1§l>---"
                + "\n\n§0§lCompany: §r§1" + getCompany(target)
                + "\n\n§0§lNotes: §r§1§o" + getNotes(target)
                + "\n\n§0§lMoney: §r§1" + PlayerRoutines.formatDecimal(BigDecimal
                .valueOf(PlayerManagement.eco.getBalance(player)))
                + "\n\n§0§lPunishments: §r§1" + getPunishments(target));

        pages.add("§1§l ---< §9§lPLAYER §1§l>---"   // values are cast to int for formatting purposes
                + "\n\n §0§lHealth:      §1" + (int) p.getHealth() + "§8/§120"
                + "\n §r§8[" + health + "§8]"
                + "\n\n §0§lFood:         §1" + p.getFoodLevel() + "§8/§120"
                + "\n §r§8[" + foodLevel + "§8]"
                + "\n\n §0§lSaturation:  §1" + (int) p.getSaturation() + "§8/§120"
                + "\n §r§8[" + saturationLevel + "§8]"
                + "\n\n §0§lArmor:        §1" + (int) armor + "§8/§120"
                + "\n §r§8[" + armorLevel + "§8]");

        DecimalFormat fourPlaces = new DecimalFormat("##.0000");
        pages.add("§1§l ---< §9§lPLAYER §1§l>---"
                + "\n\n§0§lLocation:"
                + "\n§1§lX: §r" + fourPlaces.format(p.getLocation().getX())
                + "\n§1§lY: §r" + fourPlaces.format(p.getLocation().getY())
                + "\n§1§lZ: §r" + fourPlaces.format(p.getLocation().getZ())
                + "\n\n§r§lExperience:"
                + "\n§rLevel: §r§1" + p.getLevel()
                + "\n§rTo next level: §r§1" + p.getExpToLevel()
                + "\n§rTotal: §r§1" + p.getTotalExperience()
        );

        if (!getJob(target).equals("N/A"))
            pages.add("§1§l ----< §5§lJOB §1§l>----"
                    + "\n\n§0§lJob name: §r§1" + target.getJob()
                    + "\n\n§0§lJob description: §r§1§o" + target.getJob().getDescription());

        if (!getCompany(target).equals("N/A")) {
            pages.add("§1§l --< §2§lCOMPANY §1§l>--"
                    + "\n\n§0§lName: §r§1" + target.getCompany()
                    + "\n\n§0§lDescription: §r§1§o" + target.getCompany().getDescription()
                    + "\n\n§0§lOwner: §r§1" + target.getCompany().getOwner());
            pages.add("§1§l --< §2§lCOMPANY §1§l>--"
                    + "\n\n§0§lEmployees: §r§1" + target.getCompany().getEmployees()
                    + "\n\n§0§lEstablished: §r§1" + target.getCompany().getEstablishedDate()
                    + "\n\n§0§lSalary: §r§1" + PlayerRoutines
                    .formatDecimal(target.getCompany().getPaycheck())
                    + "\n§rPaid every §1" + PlayerManagement
                    .autoEcoTimeSeconds / 60 + "§r min.");
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
     * Returns this player's job name or N/A if none was found
     *
     * @param player the player whose job to look up
     * @return the job name in a string or N/A if not applicable
     */
    public static String getJob(ServerPlayer player) {
        return player.getJob().getName() == null ||
                player.getJob().getName().trim().equals("")
                ? "N/A" : player.getJob().getName();
    }

    /**
     * Returns this player's company name or N/A if none was found
     *
     * @param player the player whose company to look up
     * @return the company name in a string or N/A if not applicable
     */
    public static String getCompany(ServerPlayer player) {
        return player.getCompany() == null ||
                player.getCompany().getName().trim().equals("")
                ? "N/A" : player.getCompany().getName();
    }

    /**
     * Returns any notes this player might have or N/A if empty
     *
     * @param player the player whose notes to retrieve
     * @return the notes in a string or N/A if not applicable
     */
    private static String getNotes(ServerPlayer player) {
        return player.getNotes() == null ||
                player.getNotes().trim().equals("")
                ? "N/A" : player.getNotes();
    }

    /**
     * Returns any punishments this player might have or 0 by default
     *
     * @param player the player whose number of punishments to retrieve
     * @return a color-formatted string
     */
    private static String getPunishments(ServerPlayer player) {
        return player.getPunishments() > 0
                ? ChatColor.DARK_RED + String.valueOf(player.getPunishments())
                : ChatColor.DARK_BLUE + "0";
    }
}
