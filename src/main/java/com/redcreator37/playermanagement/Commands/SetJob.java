package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.DataModels.Job;
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
 * Sets the player's job
 */
public class SetJob implements CommandExecutor {

    /**
     * Main command process
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        Player p = PlayerRoutines.playerFromSender(sender);
        if (p == null) return true;

        if (args.length < 1) {
            p.sendMessage(PlayerManagement.prefix + CommandHelper
                    .parseCommandUsage("setjob", new String[]{"*job_name", "player_name"}));
            return true;
        }

        String requiredPermission = args.length == 1
                ? "management.user" : "management.admin";
        if (!PlayerRoutines.checkPermission(p, requiredPermission))
            return true;

        String targetName = args.length == 1 ? p.getName() : args[1];
        ServerPlayer target = PlayerManagement.players.get(args.length == 1
                ? p.getUniqueId().toString()
                : PlayerRoutines.uuidFromUsername(PlayerManagement.players, targetName));
        if (PlayerRoutines.checkPlayerNonExistent(p, target, targetName))
            return true;

        Job newJob = PlayerManagement.jobs.get(args[0]);
        if (newJob == null) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "Unknown job: " + ChatColor.GREEN + args[0]
                    + ChatColor.GOLD + ".");
            return true;
        }

        target.setJob(newJob);
        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {   // set the job and update the player list
                PlayerManagement.playerDb.update(target);
                PlayerManagement.players = PlayerManagement.playerDb.getAll();
                p.sendMessage(PlayerManagement.prefix + ChatColor.GREEN + target
                        + ChatColor.GOLD + " is now employed as " + ChatColor.GREEN
                        + newJob + ChatColor.GOLD + ".");
            } catch (SQLException e) {
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + "Error while updating the playerdata: " + ChatColor.RED
                        + e.getMessage());
            }
        });
        return true;
    }
}
