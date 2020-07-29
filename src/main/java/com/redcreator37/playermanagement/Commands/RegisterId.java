package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.Database.PlayerDb;
import com.redcreator37.playermanagement.PlayerCard;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

/**
 * Registers a new player to the database
 */
public class RegisterId implements CommandExecutor {

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
                    + ChatColor.GREEN + "/registerid Real Name");
            return true;
        }

        ServerPlayer test = PlayerRoutines
                .getPlayerFromUsername(PlayerManagement.players, p.getName());
        if (test != null) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "Already registered!");
            return true;
        }

        // their inventory should not be full because we're going
        // to give them an ID card after registration
        if (PlayerRoutines.checkInventoryFull(p)) return true;

        StringBuilder displayName = new StringBuilder();
        for (String arg : args) displayName.append(arg).append(" ");    // construct a new ServerPlayer
        ServerPlayer target = new ServerPlayer(4097, p.getName(),   // using a dummy id
                p.getUniqueId().toString());

        target.setName(displayName.toString());
        target.setJoinDate(PlayerRoutines
                .getCurrentDate(PlayerManagement.dateFormat));
        target.setJob(PlayerManagement.jobs.get("N/A"));
        target.setCompany(PlayerManagement.companies.get("N/A"));
        target.setNotes("");

        try {
            PlayerDb.insertPlayer(target, PlayerManagement.databasePath);
            PlayerManagement.players = PlayerDb // reload from the database
                    .getAllNewlyRegistered(PlayerManagement.databasePath);

            ServerPlayer registeredPlayer = PlayerRoutines  // re-read the player list
                    .getPlayerFromUsername(PlayerManagement.players, p.getName());
            if (registeredPlayer == null) {
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + "Failed to give the ID card!");
                return true;
            }

            PlayerCard.giveNewCard(p, registeredPlayer);
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "Registration successful! Check your inventory for your ID card.");
        } catch (SQLException e) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "Error while adding the playerdata: " + ChatColor.RED
                    + e.getMessage());
        }
        return true;
    }
}
