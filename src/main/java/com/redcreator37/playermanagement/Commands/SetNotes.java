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
 * Sets the player's notes
 */
public class SetNotes implements CommandExecutor {

    /**
     * Main command process
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        Player p = PlayerRoutines.playerFromSender(sender);
        if (p == null) return true;

        if (args.length < 2) {
            p.sendMessage(PlayerManagement.prefix + CommandHelper
                    .parseCommandUsage("setnotes", new String[]{"*player_name", "Any notes..."}));
            return true;
        }

        ServerPlayer target = PlayerManagement.players.get(PlayerRoutines
                .uuidFromUsername(PlayerManagement.players, args[0]));
        if (PlayerRoutines.checkPlayerNonExistent(p, target, p.getName()))
            return true;

        String requiredPermission = target.getUsername().equals(p.getName())
                ? "management.user" : "management.admin";
        if (!PlayerRoutines.checkPermission(p, requiredPermission))
            return true;

        target.setNotes(CommandHelper.getFullEntry(args, 1));
        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {   // set the notes and update the player list
                PlayerManagement.playerDb.update(target);
                PlayerManagement.players = PlayerManagement.playerDb.getAll();
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + "Notes updated.");
            } catch (SQLException e) {
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + "Error while updating the playerdata: " + ChatColor.RED
                        + e.getMessage());
            }
        });
        return true;
    }
}
