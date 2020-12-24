package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerCard;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * Gives the player a new ID card or displays the data for another
 * player if their name was specified and the command executor
 * has sufficient permissions
 */
public class GetId extends PlayerCommand {

    public GetId() {
        super("getid", new HashMap<String, Boolean>() {{
            put("player_name", false);
        }}, new ArrayList<String>() {{
            add("management.user");
        }});
    }

    /**
     * Runs this command and performs the actions
     *
     * @param player the {@link Player} who ran the command
     * @param args   the arguments entered by the player
     */
    @Override
    public void execute(Player player, String[] args) {
        Optional<ServerPlayer> optTarget = getUserOrAdmin(player, args, 0, 0);
        if (!optTarget.isPresent()) return;
        ServerPlayer target = optTarget.get();

        if (args.length < 1) {  // if there are no elements specified, just give the player a new card
            if (!PlayerManagement.eco.has(player, PlayerManagement.prefs.cardPrice)) {
                player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                        + Localization.lc("not-enough-money-for-new-id"));
                return;
            }

            if (player.getInventory().firstEmpty() == -1) {  // make sure the inventory isn't full
                player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                        + Localization.lc("inventory-full"));
                return;
            }

            PlayerCard.giveNewCard(player, target);
            PlayerManagement.eco.withdrawPlayer(player, PlayerManagement.prefs.cardPrice);
            player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                    + Localization.lc("bought-new-id")
                    + ChatColor.GREEN + "$" + PlayerManagement.prefs.cardPrice + ChatColor.GOLD + ".");
            return;
        }

        // if it came so far, we can assume that args.length >= 1
        if (PlayerRoutines.lacksPermission(player, "management.admin"))
            return;

        PlayerCard.displayCardData(player, target);
    }
}
