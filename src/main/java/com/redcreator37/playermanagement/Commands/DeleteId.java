package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

/**
 * Unregisters the player from the database
 */
public class DeleteId implements CommandExecutor {

    /**
     * Main command process
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        Player p = PlayerRoutines.playerFromSender(sender);
        if (p == null || !PlayerRoutines
                .checkPermission(p, "management.admin"))
            return true;

        if (args.length < 1) {
            p.sendMessage(PlayerManagement.prefix + CommandHelper
                    .parseCommandUsage("deleteid", new String[]{"*player_name"}));
            return true;
        }

        ServerPlayer target = PlayerManagement.players.get(PlayerRoutines
                .uuidFromUsername(PlayerManagement.players, args[0]));
        if (PlayerRoutines.checkPlayerNonExistent(p, target, args[0]))
            return true;

        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {
                PlayerManagement.playerDb.remove(target);
                // reload from the database
                PlayerManagement.players = PlayerManagement.playerDb.getAll();
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + "The player " + ChatColor.GREEN + target.getUsername()
                        + ChatColor.GOLD + " has been successfully unregistered.");
            } catch (SQLException e) {
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + "Error while removing the playerdata: " + ChatColor.RED
                        + e.getMessage());
            }
        });
        return true;
    }
}
