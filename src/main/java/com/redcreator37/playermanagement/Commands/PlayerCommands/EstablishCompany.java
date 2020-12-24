package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.PlayerTag;
import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Establishes a new in-game company
 */
public class EstablishCompany extends PlayerCommand {

    public EstablishCompany() {
        super("establish", new HashMap<String, Boolean>() {{
            put("company_name", true);
        }}, new ArrayList<String>() {{
            add("company.management.establish");
        }});
    }

    /**
     * Runs this command and performs the actions
     *
     * @param player the {@link Player} who ran the command
     * @param args   the arguments entered by the player
     */
    @Override
    public void execute(Player player, String[] args) {
        if (PlayerManagement.companies.get(args[0]) != null) {
            player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                    + Localization.lc("already-exists"));
            return;
        }

        if (!PlayerManagement.eco.has(player, PlayerManagement.prefs.establishPrice)) {
            player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                    + Localization.lc("not-enough-money-to-establish"));
            return;
        }

        Company newCompany = new Company(4097, args[0]);
        newCompany.setOwner(new PlayerTag(player.getName(), player.getUniqueId().toString()));
        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {
                PlayerManagement.eco.withdrawPlayer(player, PlayerManagement
                        .prefs.establishPrice);
                newCompany.setBalance(new BigDecimal(PlayerManagement
                        .prefs.establishPrice / 2));

                PlayerManagement.companyDb.insert(newCompany);
                PlayerManagement.companies = PlayerManagement.companyDb.getAll();
                player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                        + Localization.lc("company-registration-successful"));
            } catch (SQLException e) {
                player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                        + Localization.lc("error-saving-company-data")
                        + ChatColor.RED + e.getMessage());
            }
        });
    }
}
