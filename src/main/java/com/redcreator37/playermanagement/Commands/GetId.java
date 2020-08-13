package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.PlayerCard;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Gives the player a new ID card or displays the data for another
 * player if their name was specified and the command executor
 * has sufficient permissions
 */
public class GetId implements CommandExecutor {

    /**
     * Main command process
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        Player p = PlayerRoutines.playerFromSender(sender);
        if (p == null) return true;

        if (args.length < 1) {  // if there are no elements specified, just give the player a new card
            if (!PlayerRoutines.checkPermission(p, "management.user"))
                return true;

            ServerPlayer target = PlayerManagement.players.get(p.getUniqueId().toString());
            if (PlayerRoutines.checkPlayerNonExistent(p, target, p.getName()))
                return true;

            if (!PlayerManagement.eco.has(p, PlayerManagement.cardPrice)) {
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + "You do not have enough money for a new ID card!");
                return true;
            }

            if (p.getInventory().firstEmpty() == -1) {  // make sure the inventory isn't full
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + "Inventory full!");
                return true;
            }

            PlayerCard.giveNewCard(p, target);
            PlayerManagement.eco.withdrawPlayer(p, PlayerManagement.cardPrice);
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "Bought a new ID card for " + ChatColor.GREEN + "$"
                    + PlayerManagement.cardPrice + ChatColor.GOLD + ".");
            return true;
        }

        // if it came so far, we can assume that args.length >= 1
        if (!PlayerRoutines.checkPermission(p, "management.admin"))
            return true;

        ServerPlayer target = PlayerManagement.players.get(PlayerRoutines.uuidFromUsername(PlayerManagement.players, args[0]));

        if (PlayerRoutines.checkPlayerNonExistent(p, target, args[0]))
            return true;

        PlayerCard.displayCardData(p, target);
        return true;
    }
}
