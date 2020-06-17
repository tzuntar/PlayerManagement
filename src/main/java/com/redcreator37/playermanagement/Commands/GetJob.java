package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Displays the specified player's job
 */
public class GetJob implements CommandExecutor {

    /**
     * Main command process
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = PlayerRoutines.getPlayerFromSender(sender);
        if (p == null || !PlayerRoutines
                .checkPlayerPermissions(p, "management.user"))
            return true;

        if (args.length < 1) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD + "Usage: "
                    + ChatColor.GREEN + "/getjob player_name");
            return true;
        }

        ServerPlayer target = PlayerRoutines
                .getPlayerFromUsername(PlayerManagement.players, args[0]);
        if (PlayerRoutines.checkPlayerNonExistent(p, target, args[0]))
            return true;

        p.sendMessage(PlayerManagement.prefix + ChatColor.GREEN + target
                + ChatColor.GOLD + " is employed as " + ChatColor.GREEN
                + target.getJob() + ChatColor.GOLD + ".");
        return true;
    }
}
