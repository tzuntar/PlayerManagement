package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.Database.CompanyDb;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Establishes a new in-game company
 */
public class EstablishCompany implements CommandExecutor {

    /**
     * Main command process
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = PlayerRoutines.getPlayerFromSender(sender);
        if (p == null || !PlayerRoutines
                .checkPlayerPermissions(p, "management.company.establish"))
            return true;

        if (args.length < 1) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD + "Usage: "
                    + ChatColor.GREEN + "/establish company_name");
            return true;
        }

        if (PlayerManagement.companies.get(args[0]) != null) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD + "Already exists!");
            return true;
        }

        if (!PlayerManagement.eco.has(p, PlayerManagement.companyEstablishPrice)) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "You do not have enough money to establish a new company!");
            return true;
        }

        Company newCompany = new Company(4097, args[0], p.getName());
        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {
                PlayerManagement.eco.withdrawPlayer(p, PlayerManagement
                        .companyEstablishPrice);
                newCompany.setBalance(new BigDecimal(PlayerManagement
                        .companyEstablishPrice / 2));

                CompanyDb.insertCompany(newCompany, PlayerManagement.databasePath);
                PlayerManagement.companies = CompanyDb
                        .getAllCompanies(PlayerManagement.databasePath);
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + "Company registration successful!");
            } catch (SQLException e) {
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + "Error while saving company data: " + ChatColor.RED
                        + e.getMessage());
            }
        });
        return true;
    }

}
