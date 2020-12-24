package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.PlayerTag;
import com.redcreator37.playermanagement.Localization;
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
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        Player p = PlayerRoutines.playerFromSender(sender);
        if (p == null || !PlayerRoutines
                .checkPermission(p, "management.company.establish"))
            return true;

        if (args.length < 1) {
            p.sendMessage(PlayerManagement.prefix + CommandHelper
                    .parseCommandUsage("establish", new String[]{"*company_name"}));
            return true;
        }

        if (PlayerManagement.companies.get(args[0]) != null) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + Localization.lc("already-exists"));
            return true;
        }

        if (!PlayerManagement.eco.has(p, PlayerManagement.companyEstablishPrice)) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + Localization.lc("not-enough-money-to-establish"));
            return true;
        }

        Company newCompany = new Company(4097, args[0]);
        newCompany.setOwner(new PlayerTag(p.getName(), p.getUniqueId().toString()));
        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {
                PlayerManagement.eco.withdrawPlayer(p, PlayerManagement
                        .companyEstablishPrice);
                newCompany.setBalance(new BigDecimal(PlayerManagement
                        .companyEstablishPrice / 2));

                PlayerManagement.companyDb.insert(newCompany);
                PlayerManagement.companies = PlayerManagement.companyDb.getAll();
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + Localization.lc("company-registration-successful"));
            } catch (SQLException e) {
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + Localization.lc("error-saving-company-data")
                        + ChatColor.RED + e.getMessage());
            }
        });
        return true;
    }

}
