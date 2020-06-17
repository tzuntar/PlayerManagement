package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.Database.PlayerDb;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * Sets the player's notes
 */
public class SetNotes implements CommandExecutor {

    /**
     * Main command process
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = PlayerRoutines.getPlayerFromSender(sender);
        if (p == null) return true;

        if (args.length < 2) {  // wrong usage
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD + "Usage: "
                    + ChatColor.GREEN + "/setnotes player_name Any Notes To Set");
            return true;
        }

        ServerPlayer target = PlayerRoutines
                .getPlayerFromUsername(PlayerManagement.players, args[0]);
        if (PlayerRoutines.checkPlayerNonExistent(p, target, p.getName()))
            return true;

        String requiredPermission = target.getUsername().equals(p.getName()) ?
                "management.user" : "management.admin";
        if (!PlayerRoutines.checkPlayerPermissions(p, requiredPermission))
            return true;

        StringBuilder notes = new StringBuilder();
        for (String arg : Arrays.copyOfRange(args, 1, args.length))
            notes.append(arg).append(" ");

        target.setNotes(notes.toString());
        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {   // set the notes and update the player list
                PlayerDb.updatePlayer(target, PlayerManagement.databasePath);
                PlayerManagement.players = PlayerDb
                        .getAllPlayers(PlayerManagement.databasePath);
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
