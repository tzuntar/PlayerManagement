package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.DataModels.PlayerTag;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
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
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        Player p = PlayerRoutines.playerFromSender(sender);
        if (p == null || !PlayerRoutines.checkPermission(p, "management.user"))
            return true;

        if (args.length < 1) {
            p.sendMessage(PlayerManagement.prefix + CommandHelper
                    .parseCommandUsage("registerid", new String[]{"Real Name"}));
            return true;
        }

        ServerPlayer test = PlayerManagement.players.get(p.getUniqueId().toString());
        if (test != null) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + PlayerManagement.strings.getString("already-registered"));
            return true;
        }

        // their inventory should not be full because we're going
        // to give them an ID card after registration
        if (PlayerRoutines.checkInventoryFull(p)) return true;
        ServerPlayer target = new ServerPlayer(4097,    // register with a dummy id
                new PlayerTag(p.getName(), p.getUniqueId().toString()));

        target.setName(CommandHelper.getFullEntry(args, 0));
        target.setJoinDate(PlayerRoutines
                .getCurrentDate(PlayerManagement.dateFormat));
        target.setJob(PlayerManagement.jobs.get("N/A"));
        target.setCompany(PlayerManagement.companies.get("N/A"));
        target.setNotes("");

        try {
            PlayerManagement.playerDb.insert(target);
            // reload from the database
            PlayerManagement.players = PlayerManagement.playerDb.getNewlyRegistered();

            ServerPlayer registeredPlayer = PlayerManagement.players
                    .get(p.getUniqueId().toString());
            if (registeredPlayer == null) {
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + PlayerManagement.strings.getString("failed-to-give-id"));
                return true;
            }

            PlayerCard.giveNewCard(p, registeredPlayer);
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + PlayerManagement.strings.getString("registration-successful"));
        } catch (SQLException e) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + PlayerManagement.strings.getString("error-updating-playerdata")
                    + ChatColor.RED + e.getMessage());
        }
        return true;
    }
}
