package com.redcreator37.playermanagement.IdHandling;

import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.PlayerManagement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.redcreator37.playermanagement.Localization.lc;

/**
 * Handles all in-game ID card actions
 */
public final class PlayerCard implements Listener {

    /**
     * Handles card right click event
     *
     * @param player the player that caused the event
     * @param lore   the item (card) lore
     */
    private static void handlePlayerCardEvent(Player player, List<String> lore) {
        ServerPlayer target = PlayerManagement.players
                .byUuid(UUID.fromString(lore.get(1)));
        if (target != null) InfoCards.displayPlayerInfo(player, target);
        else player.sendMessage(PlayerManagement.prefs.prefix + lc("invalid-id-card"));
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
        Objects.requireNonNull(data).setDisplayName(MessageFormat
                .format(lc("id-card-display-name"), target));

        List<String> lore = new ArrayList<>();
        lore.add(PlayerManagement.prefs.cardItemLore);
        lore.add(target.getUuid().toString());
        Objects.requireNonNull(data).setLore(lore);
        stack.setItemMeta(data);

        player.getInventory().addItem(stack);
    }

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

            if (lore != null && lore.contains(PlayerManagement.prefs.cardItemLore))
                handlePlayerCardEvent(p, lore);
        }
    }

}
