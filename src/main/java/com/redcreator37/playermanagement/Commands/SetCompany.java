package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.DataModels.Company;
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
 * Sets the player's company
 */
public class SetCompany implements CommandExecutor {

    /**
     * Main command process
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = PlayerRoutines.getPlayerFromSender(sender);
        if (p == null) return true;

        if (args.length < 1) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD + "Usage: "
                    + ChatColor.GREEN + "/setcompany company_name [player_name]");
            return true;
        }

        String requiredPermission = args.length == 1
                ? "management.user" : "management.admin";
        if (!PlayerRoutines.checkPlayerPermissions(p, requiredPermission))
            return true;

        String targetPlayerName = args.length == 1 ? p.getName() : args[1];
        ServerPlayer target = PlayerRoutines
                .getPlayerFromUsername(PlayerManagement.players, targetPlayerName);

        if (PlayerRoutines.checkPlayerNonExistent(p, target, targetPlayerName))
            return true;

        Company newCompany = PlayerManagement.companies.get(args[0]);
        if (newCompany == null) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "Unknown company: " + ChatColor.GREEN + args[0]
                    + ChatColor.GOLD + ".");
            return true;
        }

        if (!newCompany.getOwner().equals(target.getUsername()) &&
                !p.hasPermission("management.company.employ")) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "You can't employ yourself at this company!");
            return true;
        }

        Company prevCompany = target.getCompany();

        target.setCompany(newCompany);
        newCompany.setEmployees(newCompany.getEmployees() + 1);
        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {   // set the job and update the player list
                PlayerManagement.playerDb.update(target);
                PlayerManagement.players = PlayerManagement.playerDb.getAll();
                p.sendMessage(PlayerManagement.prefix + ChatColor.GREEN + target
                        + ChatColor.GOLD + " is now a part of the company "
                        + ChatColor.GREEN + args[0] + ChatColor.GOLD + ".");

                // decrease the employee count when switching to a different company
                if (!prevCompany.getName().equals("N/A"))
                    prevCompany.setEmployees(prevCompany.getEmployees() - 1);

                PlayerManagement.companyDb.update(newCompany);
                PlayerManagement.companyDb.update(prevCompany);
                PlayerManagement.companies = PlayerManagement.companyDb.getAll();
            } catch (SQLException e) {
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + "Error while updating the playerdata: " + ChatColor.RED
                        + e.getMessage());
            }
        });
        return true;
    }
}
