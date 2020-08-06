package com.redcreator37.playermanagement;

import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.Transaction;
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
import java.util.Arrays;
import java.util.Objects;

import static com.redcreator37.playermanagement.PlayerManagement.eco;
import static com.redcreator37.playermanagement.PlayerManagement.getPlugin;
import static com.redcreator37.playermanagement.PlayerManagement.prefix;
import static com.redcreator37.playermanagement.PlayerRoutines.formatDecimal;

/**
 * A simple chest-like company management UI
 */
public class CompanyMenu implements Listener {

    /**
     * Represents the internal inventory
     */
    private final Inventory inventory;

    /**
     * The player that is currently using the inventory
     */
    private Player player;

    /**
     * The company the player is managing
     */
    private final Company company;

    /**
     * Used to perform a safety-check
     */
    private final String guiTitle;

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
                        "§bDescription: §f" + company.getDescription(),
                        "§6Balance: §f" + formatDecimal(company.getBalance()),
                        "§6Employees: §f" + company.getEmployees(),
                        "§6Salary: §f" + formatDecimal(company.getPaycheck()),
                        "§6Owner: §f" + company.getOwner(),
                        "§6Established: §f" + company.getEstablishedDate()
                }, 0);
        newItem(Material.OAK_DOOR, "Close", 1,
                new String[]{"§7§oCloses the menu and saves any changes"}, 8);

        // 2nd row
        newItem(Material.GREEN_WOOL, "Increase salary", 1,
                new String[]{"§fIncreases the salary for players by §a$10"}, 11);
        newItem(Material.RED_WOOL, "Decrease salary", 1,
                new String[]{"§fDecreases the salary for players by §a$10"}, 12);

        newItem(Material.GREEN_CONCRETE, "Deposit §a$10", 1,
                new String[]{"§fAdds §a$10 §fto the company's balance"}, 14);
        newItem(Material.RED_CONCRETE, "Withdraw §a$10", 1,
                new String[]{"§fRemoves §a$10§f from the company's balance"}, 15);
    }

    /**
     * Creates a new GUI item
     */
    public void newItem(Material type, String caption, int amount, String[] lore, int slot) {
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
     */
    public void processCommand(String clickedItem) {
        switch (clickedItem) {
            case "Close":
                player.closeInventory();
                break;
            case "Increase salary":
                company.setPaycheck(company.getPaycheck().add(new BigDecimal(10)));
                player.sendMessage(prefix + "§6The salary has been increased by §a$10§6.");
                break;
            case "Decrease salary":
                company.setPaycheck(company.getPaycheck().subtract(new BigDecimal(10)));
                player.sendMessage(prefix + "§6The salary has been decreased by §a$10§6.");
                break;
            default:
                if (clickedItem.contains("Deposit")) {
                    eco.withdrawPlayer(player, 10);
                    company.setBalance(company.getBalance().add(new BigDecimal(10)));
                    player.sendMessage(prefix + "§a$10§6 has been taken from your account.");

                    PlayerManagement.transactionDb.addAsync(player, new Transaction(4097,
                            company.getId(), "<-", "Deposit $10",
                            "Deposit $10 from the player "
                                    + player.getName(), new BigDecimal(10)));
                } else if (clickedItem.contains("Withdraw")) {
                    company.setBalance(company.getBalance().subtract(new BigDecimal(10)));
                    eco.depositPlayer(player, 10);
                    player.sendMessage(prefix + "§a$10§6 has been added to your account.");

                    PlayerManagement.transactionDb.addAsync(player, new Transaction(4097,
                            company.getId(), "->", "Withdraw $10",
                            "Withdraw $10 from the player "
                                    + player.getName(), new BigDecimal(10)));
                }
        }

        Bukkit.getScheduler().runTask(getPlugin(PlayerManagement.class),
                () -> PlayerManagement.companyDb.updateByPlayer(player, company));
    }

}
