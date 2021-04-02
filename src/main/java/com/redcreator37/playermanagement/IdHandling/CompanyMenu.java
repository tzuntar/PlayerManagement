package com.redcreator37.playermanagement.IdHandling;

import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.Transaction;
import com.redcreator37.playermanagement.PlayerManagement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

import static com.redcreator37.playermanagement.Localization.lc;
import static com.redcreator37.playermanagement.PlayerManagement.eco;
import static com.redcreator37.playermanagement.PlayerManagement.getPlugin;
import static com.redcreator37.playermanagement.PlayerRoutines.formatDecimal;

/**
 * A simple chest-like company management UI
 */
public final class CompanyMenu implements Listener {

    /**
     * The default monetary value used for various transactions
     */
    private static final double DEF_AMOUNT = 10;
    /**
     * Represents the internal inventory
     */
    private final Inventory inventory;
    /**
     * The company the player is managing
     */
    private final Company company;
    /**
     * Used to perform a safety-check
     */
    private final String guiTitle;
    /**
     * The player that is currently using the inventory
     */
    private Player player;

    /**
     * Constructs a new CompanyMenu display
     *
     * @param player  to show the GUI
     * @param title   the GUI title
     * @param company the company to manage
     */
    public CompanyMenu(Player player, String title, Company company) {
        this.player = player;
        this.company = company;
        this.guiTitle = title;
        int slots = 18;  // must be a multiple of 9!

        inventory = Bukkit.getServer().createInventory(null, slots, title);
        Bukkit.getServer().getPluginManager().registerEvents(
                this, getPlugin(PlayerManagement.class));
        addItems();
        player.openInventory(inventory);
    }

    /**
     * Adds items to the GUI
     */
    public void addItems() {
        // 1st row
        newItem(Material.PAPER, "§a§l" + company, 1,
                new String[]{
                        MessageFormat.format(lc("company-menu-desc"),
                                company.getDescription()),
                        MessageFormat.format(lc("company-menu-balance"),
                                formatDecimal(company.getBalance())),
                        MessageFormat.format(lc("company-menu-employees"),
                                company.getEmployees()),
                        MessageFormat.format(lc("company-menu-wage"),
                                formatDecimal(company.getWage())),
                        MessageFormat.format(lc("company-menu-owner"),
                                company.getOwner()),
                        MessageFormat.format(lc("company-menu-est"),
                                company.getEstablishedDate())
                }, 0);
        newItem(Material.OAK_DOOR, MenuOptions.CLOSE.getCaption(), 1,
                new String[]{lc("company-menu-close-desc")}, 8);

        // 2nd row
        newItem(Material.GREEN_WOOL, MenuOptions.INC_WAGES.getCaption(), 1,
                new String[]{MessageFormat.format(lc(
                        "company-menu-increase-desc"), DEF_AMOUNT)}, 11);
        newItem(Material.RED_WOOL, MenuOptions.DEC_WAGES.getCaption(), 1,
                new String[]{MessageFormat.format(lc(
                        "company-menu-decrease-desc"), DEF_AMOUNT)}, 12);

        newItem(Material.GREEN_CONCRETE, MenuOptions.DEPOSIT.getCaption(), 1,
                new String[]{MessageFormat.format(lc(
                        "company-menu-deposit-desc"), DEF_AMOUNT)}, 14);
        newItem(Material.RED_CONCRETE, MenuOptions.WITHDRAW.getCaption(), 1,
                new String[]{MessageFormat.format(lc(
                        "company-menu-withdraw-desc"), DEF_AMOUNT)}, 15);
    }

    /**
     * Creates a new GUI item
     *
     * @param type    the item type to insert
     * @param caption the display title of the item
     * @param amount  the amount of the items on the stack of
     *                the same type
     * @param lore    the additional description text
     * @param slot    the inventory slot (must be valid!)
     */
    public void newItem(Material type, String caption, int amount, String[] lore, int slot) {
        if (slot > inventory.getSize())
            throw new IllegalArgumentException("Slot number must not exceed the size of the inventory");
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();

        item.setAmount(amount);
        Objects.requireNonNull(meta).setDisplayName(caption);
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }

    /**
     * Handles the event when an item is clicked
     *
     * @param e the inventory click event
     */
    @EventHandler
    public void onInventoryItemClick(final InventoryClickEvent e) {
        // check if in the correct inventory
        if (!e.getView().getTitle().equals(this.guiTitle))
            return;

        // make sure the item exists in first place
        if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta() ||
                !Objects.requireNonNull(e.getCurrentItem().getItemMeta())
                        .hasDisplayName())
            return;

        player = (Player) e.getWhoClicked();
        e.setCancelled(true);

        // wait for the event to get cancelled
        if (e.isCancelled()) Bukkit.getScheduler().scheduleSyncDelayedTask(
                getPlugin(PlayerManagement.class), () -> {
                    try {
                        processCommand(e.getCurrentItem().getItemMeta().getDisplayName());
                        e.setCurrentItem(null);
                        player.closeInventory();
                    } catch (NullPointerException ignored) { }
                }, 0);
    }

    /**
     * Processes commands attached to the menu items
     *
     * @param clickedItem the display name of the clicked item
     */
    public void processCommand(String clickedItem) {
        String prefix = PlayerManagement.prefs.prefix;
        switch (MenuOptions.fromId(clickedItem)) {
            case CLOSE:
                player.closeInventory();
                break;
            case INC_WAGES:
                company.setWage(company.getWage().add(new BigDecimal(DEF_AMOUNT)));
                player.sendMessage(prefix
                        + MessageFormat.format(lc("wages-increased-by"), DEF_AMOUNT));
                break;
            case DEC_WAGES:
                try {
                    company.setWage(company.getWage().subtract(new BigDecimal(DEF_AMOUNT)));
                    player.sendMessage(prefix
                            + MessageFormat.format(lc("wages-decreased-by"), DEF_AMOUNT));
                } catch (IllegalArgumentException e) {
                    player.sendMessage(prefix + lc("wage-cannot-be-negative"));
                }
                break;
            case DEPOSIT:
                eco.withdrawPlayer(player, DEF_AMOUNT);
                company.setBalance(company.getBalance().add(new BigDecimal(DEF_AMOUNT)));
                player.sendMessage(prefix
                        + MessageFormat.format(lc("has-been-added-to-your-account"), DEF_AMOUNT));

                PlayerManagement.transactionDb.addAsync(player, new Transaction(4097,
                        company.getId(), "<-", MessageFormat.format(lc("deposit-amount"), DEF_AMOUNT),
                        MessageFormat.format(lc("deposit-to-player"),
                                DEF_AMOUNT, player.getName()), new BigDecimal(DEF_AMOUNT)));
                break;
            case WITHDRAW:
                company.setBalance(company.getBalance().subtract(new BigDecimal(DEF_AMOUNT)));
                eco.depositPlayer(player, DEF_AMOUNT);
                player.sendMessage(prefix
                        + MessageFormat.format(lc("has-been-added-to-your-account"), DEF_AMOUNT));

                PlayerManagement.transactionDb.addAsync(player, new Transaction(4097,
                        company.getId(), "->", MessageFormat.format(lc("withdraw-amount"), DEF_AMOUNT),
                        MessageFormat.format(lc("withdraw-from-player"),
                                DEF_AMOUNT, player.getName()), new BigDecimal(DEF_AMOUNT)));
                break;
        }
        Bukkit.getScheduler().runTask(getPlugin(PlayerManagement.class),
                () -> PlayerManagement.companyDb.updateByPlayer(player, company));
    }

    /**
     * Contains all implemented GUI menu options
     */
    private enum MenuOptions {
        CLOSE(lc("company-menu-close")),
        INC_WAGES(lc("company-menu-inc-wages")),
        DEC_WAGES(lc("company-menu-dec-wages")),
        DEPOSIT(MessageFormat.format(lc("company-menu-deposit"), DEF_AMOUNT)),
        WITHDRAW(MessageFormat.format(lc("company-menu-withdraw"), DEF_AMOUNT));

        private final String caption;

        MenuOptions(String value) {
            this.caption = value;
        }

        public static MenuOptions fromId(String id) {
            for (MenuOptions opt : MenuOptions.values())
                if (opt.getCaption().equals(id)) return opt;
            throw new RuntimeException("Invalid value");
        }

        public String getCaption() {
            return caption;
        }
    }

}
