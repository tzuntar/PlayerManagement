package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.EconomyProvider;
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
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        Player p = PlayerRoutines.playerFromSender(sender);
        if (p == null) return true;

        if (args.length < 1) {
            p.sendMessage(PlayerManagement.prefix + CommandHelper
                    .parseCommandUsage("setcompany", new String[]{"*company_name", "player_name"}));
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

        Company newCompany = PlayerManagement.companies.get(args[0]);
        if (newCompany == null) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + PlayerManagement.strings.getString("unknown-company")
                    + ChatColor.GREEN + args[0] + ChatColor.GOLD + ".");
            return true;
        }

        if (!newCompany.getOwner().getUsername().equals(target.getUsername()) &&
                !p.hasPermission("management.company.employ")) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + PlayerManagement.strings.getString("cant-employ-yourself"));
            return true;
        }

        target.setCompany(newCompany);
        newCompany.setEmployees(newCompany.getEmployees() + 1);
        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {   // set the job and update the player list
                PlayerManagement.playerDb.update(target);
                PlayerManagement.players = PlayerManagement.playerDb.getAll();
                p.sendMessage(PlayerManagement.prefix + ChatColor.GREEN + target
                        + ChatColor.GOLD + PlayerManagement.strings
                        .getString("now-part-of-company")
                        + ChatColor.GREEN + args[0] + ChatColor.GOLD + ".");

                // decrease the employee count when switching to a different company
                Company prevCompany = target.getCompany();
                if (!EconomyProvider.isPlayerUnemployed(target))
                    prevCompany.setEmployees(prevCompany.getEmployees() - 1);

                PlayerManagement.companyDb.update(newCompany);
                PlayerManagement.companyDb.update(prevCompany);
                PlayerManagement.companies = PlayerManagement.companyDb.getAll();
            } catch (SQLException e) {
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + PlayerManagement.strings.getString("error-updating-playerdata")
                        + ChatColor.RED + e.getMessage());
            }
        });
        return true;
    }
}
